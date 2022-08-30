package io.redandroid.navigator.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.ksp.writeTo
import io.redandroid.navigator.api.Destination
import java.util.*

private const val PACKAGE = "io.redandroid.navigator"
private const val NAVIGATOR_COMPOSABLE_NAME = "Navigator"
private const val SCREEN_BUILDER = "ScreenBuilder"
private const val START_DESTINATION = "startDestination"

private val screenBuilderClass = ClassName(PACKAGE, SCREEN_BUILDER)
private val composableClass = ClassName("androidx.compose.runtime", "Composable")
private val composeAnnotation = AnnotationSpec.builder(composableClass).build()
private val navHostControllerClass = ClassName("androidx.navigation", "NavHostController")
private val navBackStackEntryClass = ClassName("androidx.navigation", "NavBackStackEntry")
private val rememberNavControllerName = MemberName("androidx.navigation.compose", "rememberNavController")

fun CodeGenerator.generateCode(destinations: List<DestinationDescription>, dependencies: Dependencies) {

	val home = destinations.firstOrNull { it.isHome }?.name ?: error("Couldn't find a ${Destination::class.simpleName} marked as home")

	val startDestinationProperty = PropertySpec.builder(START_DESTINATION, String::class).addModifiers(KModifier.PRIVATE).initializer("%S", home).mutable(mutable = false).build()

	val fileSpec = FileSpec.builder(PACKAGE, NAVIGATOR_COMPOSABLE_NAME)
		.addImport("androidx.navigation.compose", "NavHost", "composable")
		.addProperty(startDestinationProperty)
		.addFunction(createNavigatorComposable(destinations))
		.addType(createScreenBuilder(destinations))
		.apply {
			destinations.forEach { destination ->
				addType(destination.toContextClass())
			}
		}.build()

	fileSpec.writeTo(codeGenerator = this, dependencies)
}

private fun createNavigatorComposable(destinations: List<DestinationDescription>): FunSpec {
	val navHostControllerParam = ParameterSpec.builder("navHostController", navHostControllerClass)
		.defaultValue("%M()", rememberNavControllerName)
		.build()

	val screenBuilderLambda = LambdaTypeName.get(receiver = screenBuilderClass, returnType = UNIT)
	val screenBuilderParam = ParameterSpec.builder("builder", screenBuilderLambda).build()

	return FunSpec.builder(NAVIGATOR_COMPOSABLE_NAME)
		.addAnnotation(composeAnnotation)
		.addParameter(navHostControllerParam)
		.addParameter(screenBuilderParam)
		.beginControlFlow("NavHost(navController = %L, startDestination = %L)", navHostControllerParam.name, START_DESTINATION)
		.addStatement("val screenBuilder = %L()", SCREEN_BUILDER)
		.addStatement("screenBuilder.builder()")
		.apply {
			destinations.forEach { destination ->
				addCode(destination.toNavigationComposableCodeBlock())
			}
		}
		.endControlFlow()
		.build()
}

private fun createScreenBuilder(destinations: List<DestinationDescription>): TypeSpec =
	TypeSpec.classBuilder(SCREEN_BUILDER)
		.apply {
			destinations.forEach { destination ->
				val destinationScreenName = destination.name.replaceFirstChar { it.lowercase(Locale.getDefault()) }
				val composableProperty = "${destinationScreenName}Composable"
				val destinationContextClass = ClassName(PACKAGE, destination.contextName)
				val destinationContextLambda = LambdaTypeName.get(receiver = destinationContextClass, returnType = UNIT).copy(annotations = listOf(composeAnnotation))

				addProperty(
					PropertySpec.builder(composableProperty, destinationContextLambda.copy(nullable = true), KModifier.INTERNAL)
						.initializer("null")
						.mutable(mutable = true)
						.build()
				)
				addFunction(
					FunSpec.builder("${destinationScreenName}Screen")
						.addParameter(ParameterSpec.builder("composable", destinationContextLambda).build())
						.addStatement("%L = composable", composableProperty)
						.build()
				)
			}
		}
		.build()

private fun DestinationDescription.toContextClass(): TypeSpec {
	val navControllerParam = "navHostController"
	val navBackStackEntryParam = "navBackStackEntry"
	return TypeSpec.classBuilder(contextName)
		.primaryConstructor(
			FunSpec.constructorBuilder()
				.addParameter(navControllerParam, navHostControllerClass)
				.addParameter(navBackStackEntryParam, navBackStackEntryClass)
				.build()
		)
		.addProperty(PropertySpec.builder(navControllerParam, navHostControllerClass, KModifier.PRIVATE).initializer(navControllerParam).build())
		.addProperty(PropertySpec.builder(navBackStackEntryParam, navBackStackEntryClass, KModifier.PRIVATE).initializer(navBackStackEntryParam).build())
		.apply {
			parameters.forEach { parameter ->
				addProperty(
					PropertySpec.builder(parameter.name, ClassName("", parameter.type))
						.mutable(mutable = false)
						.getter(
							FunSpec.getterBuilder()
								.addStatement(
									"return %L.arguments?.getString(%S)?.to${parameter.type.typeString()}OrNull() ?: error(%S)",
									navBackStackEntryParam,
									parameter.name,
									"Required parameter ${parameter.name} not provided"
								)
								.build()
						)
						.build()
				)
			}

			navigationTargets.forEach { navigationTarget ->
				val paramsRoute = navigationTarget.parameters.joinToString(separator = "/") { "\${${it.name}}" }
				val paramsRouteWithSlash = if (paramsRoute.isNotBlank()) "/$paramsRoute" else ""

				addFunction(
					FunSpec.builder("navigateTo${navigationTarget.name}")
						.apply {
							navigationTarget.parameters.forEach { navigationParameter ->
								val parameterType = ClassName("", navigationParameter.type)
								addParameter(
									ParameterSpec.builder(navigationParameter.name, parameterType).build()
								)
							}
						}
						.addStatement("%L.navigate(%P)", navControllerParam, "${navigationTarget.name}$paramsRouteWithSlash")
						.build()
				)
			}
		}
		.build()
}

private fun DestinationDescription.toNavigationComposableCodeBlock(): CodeBlock {
	val params = parameters.joinToString("/") { "{${it.name}}" }
	val paramSuffix = if (params.isNotBlank()) "/$params" else ""
	val destinationScreenName = name.replaceFirstChar { it.lowercase(Locale.getDefault()) }

	return CodeBlock.builder()
		.beginControlFlow("composable(route = %S)", name + paramSuffix)
		.addStatement("screenBuilder.%LComposable?.invoke(%L(navHostController, it))", destinationScreenName, contextName)
		.endControlFlow()
		.build()
}

private val DestinationDescription.contextName: String
	get() = "${name}Context"