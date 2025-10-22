# Habit Tracker 🎯

A modern, advanced Habit Tracker Android application built with the latest technologies and based on Clean Architecture principles.




## Description 📝

This project is not just a simple application but a showcase of a robust implementation of MVI and Clean Architecture in a modern, 100% Jetpack Compose environment. The primary goal is to build a maintainable, testable, and scalable application for managing and tracking users' daily habits with complex and diverse scheduling capabilities.

## ScreenShots

| ![shot1](Screenshots/sreenshot%20(1).png) | ![shot2](Screenshots/sreenshot%20(2).png) |![shot3](Screenshots/sreenshot%20(3).png) |![shot4](Screenshots/sreenshot%20(4).png) | ![shot5](Screenshots/sreenshot%20(5).png) |
|----------|:----------:|:--------:|:---------:|:---------:|


## Features ✨

* **Create & Manage Habits:** With the ability to define a title, description, and reminders.
* **Multiple Habit Types:**
    * **Simple:** For straightforward done/undone habits.
    * **Countable:** For habits like "drink 8 glasses of water."
    * **Timed:** For habits like "meditate for 10 minutes," complete with a built-in timer.
* **Advanced Scheduling:**
    * **Daily** (with the ability to define exception days).
    * **Weekly** (selection of specific days of the week).
    * **Monthly** (selection of specific days of the month).
    * **Interval** (every X days).
* **Timeline View:** A beautiful and intuitive timeline to display the habits for each day.
* **Horizontal Calendar:** Smooth navigation between days with a swipeable calendar.
* **Smart Reminders:** A reminder system using custom notifications (requiring user permission).
* **Data Management:** A screen for searching and filtering habits based on their type and schedule.
* **Foreground Service:** Ensures the timer functionality works reliably even after the app is closed.

## Architecture 🏛️

This project is built upon the principles of **Clean Architecture** and follows the **MVI (Model-View-Intent)** pattern to create clean, modular, and testable code.

`UI (Compose) → ViewModel (MVI) → Domain → Data (Repository)`




* **Presentation Layer:** Contains the `ViewModels`, which act as State Holders and manage the UI logic following the MVI pattern.
* **Domain Layer:** Includes the core business models and UseCases. This layer is completely independent of the Android framework.
* **Data Layer:** Consists of `Repositories` and Data Sources like the Room Database and services. This layer is responsible for managing and providing data.

## Tech Stack & Libraries 🛠️

* **Language:** 100% [Kotlin](https://kotlinlang.org/)
* **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
    * **Material 3:** For the visual design system.
    * **Navigation Compose:** for in-app navigation.
* **Architecture:**
    * Clean Architecture
    * MVI (Model-View-Intent)
* **Asynchronicity:**
    * [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
    * [Flow](https://kotlinlang.org/docs/flow.html)
* **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
* **Database:** [Room](https://developer.android.com/training/data-storage/room)
* **State Management:** ViewModel & SavedStateHandle
* **Date & Time:** [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP)  and the [PersianDate](https://github.com/samanzamani/PersianDate) library.

## Getting Started 🚀

To run this project on your local machine, follow these steps:

1.  Clone this repository:
    ```bash
    git clone https://github.com/amirr-sharifi/HabitTracker-jetpackCompose.git
    ```
2.  Open the project in the latest version of Android Studio.
3.  Let Gradle download the dependencies automatically.
4.  Run the project.

