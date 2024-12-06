PennMobile Android
===================

Penn's mobile app for Android, created in a partnership between Penn Labs and the UA.

<a href="https://play.google.com/store/apps/details?id=com.pennapps.labs.pennmobile"><img width="200px" alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png"/></a>

## Features

* Interactive home page with events, polls, and posts
* View dining information, including hours and menus 
* Book group study rooms (GSRs) and manage your bookings
* View college house laundry machine availability
* Check the capacity and business of fitness locations
* Access relevant campus information and support resources

## For Developers

Before making a pull request, it's important to lint the codebase to maintain readability. We use the default [ktlint formatter](https://github.com/pinterest/ktlint) by Pinterest

### Installation:

- **On macOS:** You can use Homebrew. Run the following command: ``brew install ktlint``
- **On Windows:** Download the ktlint file and associated batch from the GitHub repository and add it to your PATH manually (for example, C:\Program Files\ktlint)

### Usage:

Navigate to the root of the project (``penn-mobile-android``) and run the following command: ``ktlint --format``
This command will automatically fix some errors and log the remaining ones. Feel free to refer to the [CLI reference](https://pinterest.github.io/ktlint/latest/install/cli/#command-line-usage) to use other commands, like ``ktlint --reporter=plain?group_by_file`` to group errors by file.

Note: If on Windows, use Git Bash as the terminal and refer to the [Windows guide](https://pinterest.github.io/ktlint/latest/install/cli/#microsoft-windows-users)



