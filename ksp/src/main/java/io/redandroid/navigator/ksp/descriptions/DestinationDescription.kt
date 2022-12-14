package io.redandroid.navigator.ksp.descriptions

data class DestinationDescription(
	val name: String,
	val parameters: List<ParameterDescription>,
	val navigationTargets: List<NavigationTarget>,
	val isHome: Boolean
)