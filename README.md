![](art/header.png)

# Moviesy

![build](https://github.com/KaustubhPatange/Moviesy/workflows/build/badge.svg)
![issues](https://img.shields.io/github/issues/KaustubhPatange/Moviesy.svg)

A **_beautiful_** client for [YTS](https://www.google.com/search?q=yts) website which also provides built-in _torrent_, _subtitles_ downloader ❤️

> Disclaimer: The app let's you download and stream HD movies on the go, if this is something you don't approve then you should stop using it. This is indeed a hobby project :)

[![Moviesy App](https://img.shields.io/badge/Download-APK-red.svg?style=for-the-badge&logo=android)](https://github.com/KaustubhPatange/Moviesy/releases/download/v1.4/app-release_v1.4.apk)

## Features

- Clean & **beautiful** UI.
- Built-in _movie_ & _subtitle_ downloader
- Supports torrent **streaming**.
- **Watch** movie using in-built player.
- **Cast** to chromecast devices.
- Lot more...

## Compilation Guide

Since this project involves a private payment system 📃 some of the source files has been ignored. Read this [guide](https://github.com/KaustubhPatange/Moviesy/wiki/Compilation-guide) in order to compile 🗃 the project.

## Project Libraries

- [After](app/after) - A library that helps you to dispatch events "after" some time one of which is displaying prompts.
- [ShimmerImageView](app/shimmer) - An extension over Facebook's standard [shimmer](https://facebook.github.io/shimmer-android) effect library.
- [Auto Bindings](https://github.com/KaustubhPatange/AutoBindings) - Set of annotations that aims to eliminate biolerplate code.

## Built with 🛠

- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - Data objects that notify views when the underlying database changes.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes.
  - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - Generates a binding class for each XML layout file present in that module and allows you to more easily write code that interacts with views.
  - [Room](https://developer.android.com/topic/libraries/architecture/room) - SQLite object mapping library.
  - [Paging](https://developer.android.com/topic/libraries/architecture/paging) - Library helps you load and display small chunks of data at a time. Loading partial data on demand reduces usage of network bandwidth and system resources.
  - [Saving Sates](https://developer.android.com/topic/libraries/architecture/saving-states) - Uses recommended solutions for saving & restoring UI state through viewModel.
  - [Workmanager](https://developer.android.com/topic/libraries/architecture/workmanager) - An API that makes it easy to schedule deferrable, asynchronous tasks that are expected to run even if the app exits or the device restarts.
- [Navigation Component](https://developer.android.com/guide/navigation) - Jetpack's recommended way to implement navigation aiming to improve user experience.
- [Dependency Injection](https://developer.android.com/training/dependency-injection) -
  - [Hilt-Dagger](https://dagger.dev/hilt/) - Standard library to incorporate Dagger dependency injection into an Android application.
  - [Hilt-ViewModel](https://developer.android.com/training/dependency-injection/hilt-jetpack) - DI for injecting `ViewModel`.
- [Retrofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android and Java.
- [Material Components for Android](https://github.com/material-components/material-components-android) - Modular and customizable Material Design UI components for Android.
- [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html) - For writing Gradle build scripts using Kotlin.

## Contribute

If you want to contribute to this project, you're always welcome!
See [Contributing Guidelines](CONTRIBUTING.md).

## License

- [The Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)

```
Copyright 2020 Kaustubh Patange

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
