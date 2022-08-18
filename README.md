# Navigator

This navigator library offers a KSP API to generate needed boiler plate code for Jetpack Compose Navigation, when going full Compose with your app.

## API

The API consists of three Annotations
- **Destination**: Creates a destination in the Compose NavHost. This is a placeholder where a Composable can be called from to create a screen.
- **Navigation**: A Destination can define navigations to out Destinations, creating a navigation graph. The provided Desination placeholder will receive a navigation method usable to navigate to another screen.
- **Parameter**: A Destination can define parameters it wants to have, the parameters will also be generated in the Desitination placeholder and in the navigation method.

## Example

```Kotlin
@Destination(isHome = true)
@Navigation(to = Details::class)
object Home

@Destination
@Parameter(name = "myParam", type = Int::class)
object Details
```

This configuration leads to the following generated Navigator

```Kotlin
Navigator {
  homeScreen {
    // navigateToDetails(123)
  }
  detailsScreen {
    // myParam
  }
}
```

With the Destination **Home** the method **homeScreen** was generated, providing also the **navigateToDetails** method as defined in the Destination.
The **Details** Destination generated **detailsScreen** with the posibility to access **myParam** of type Int.