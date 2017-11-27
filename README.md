PennMobile Android
===================

Penn's mobile app for Android, created in a partnership between Penn Labs and the UA.

<a href="https://play.google.com/store/apps/details?id=com.pennapps.labs.pennmobile"><img width="200px" alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png"/></a>

## Features

* Course search from registrar
* Directory search for faculty
* List dining halls with open/close label
* View dining menus for residential dining halls
* Read Penn News from multiple sources (mobile websites)
* View transit routes, static
* Navigate using Penn Transit system
* View college house laundry machine availability
* Call campus help support resources

## Contributing

To get started running the app, just follow the following instructions:

1. Install Android Studio from the [Android developers website](https://developer.android.com/sdk/installing/studio.html).
2. Git clone the repository into your Android Studio workspace
3. Using the SDK Manager, install the Android SDK 25, Build Tools 25.0.2, Google repository, Android support library, and any other dependencies of the app.
4. Open the project using Android Studio by double clicking the `build.gradle` file in the root directory of the project.
5. Copy the `api-keys.xml.sample` file to `PennMobile/src/main/res/values/api-keys.xml` and follow the instructions inside the file to obtain API keys.
6. Build the app and make sure that Gradle fetches all the proper dependencies. If you are prompted to install additional dependencies, definitely do so.
7. You should be able to build the app, and then run it on an Android simulator or an actual Android phone/tablet.
