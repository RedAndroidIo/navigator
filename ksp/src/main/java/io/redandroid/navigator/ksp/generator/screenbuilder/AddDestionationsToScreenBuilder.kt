package io.redandroid.navigator.ksp.generator.screenbuilder

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import io.redandroid.navigator.ksp.decapitalize
import io.redandroid.navigator.ksp.descriptions.DestinationDescription
import io.redandroid.navigator.ksp.generator.PACKAGE
import io.redandroid.navigator.ksp.generator.composeAnnotation
import io.redandroid.navigator.ksp.generator.contextName

internal fun TypeSpec.Builder.addDestinationsToScreenBuilder(destinations: List<DestinationDescription>): TypeSpec.Builder {
	destinations.forEach { destination ->
		val destinationScreenName = destination.name.decapitalize()
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
	return this
}