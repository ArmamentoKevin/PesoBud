# PesoBud - Student Expense Tracker

## PesoBud is a modern, student-focused expense tracking Android application built using Kotlin and Jetpack Compose. It empowers students to track their daily spending, set monthly budgets, filter expense history, and receive visual budget health warnings to build better financial habits.

## AI Assistance Disclosure

This project was developed with the assistance of AI tools, as part of the course requirements for Project Parts 2 and 3.

- Claude was used to plan the project, break the work into incremental steps and review the code for my project to check what is still missing, what can be added to the project etc.
- Gemini Agent was used to assist me on most of the Kotlin and Jetpack Compose code and debugging problems.

## Features Implemented

1. Add & Manage Expenses:
   - Record expense amount, category (Food, Transport, School, Load/Data, Others), note, and date.

2. Edit, Delete & Undo:
   - Edit any existing expense pre-filled with its recorded details.
   - Delete confirmation dialog.
   - Undo action on deletion.

3. Expense History & Filtering:
   - View all recorded expenses sorted newest first.
   - Search by date, category, and note.

4. Monthly Overview & Budget Tracking:
   - Set or update monthly budget limit with validation.
   - Monthly reset: Spent totals calculate strictly for the current calendar month, while full history is preserved.
   - Visual circular donut progress ring and progress bars.
   - Clear status indicators: Under Budget (Green), Near Limit (Amber warning banner at 80%), and Over Budget (Red warning banner at 100%).

5. Local Data Persistence:
   - Built on Room Database.

## Tech Stack

- Language: Kotlin 2.0
- UI Framework: Jetpack Compose with Material 3 Design
- Database: Room Database
- Navigation: Jetpack Navigation Compose

## How to Build & Run

1. Open the project in Android Studio.
2. Sync the project with Gradle files.
3. Select an Android Virtual Device (Emulator) or connected physical Android device (API 28+).
4. Click Run app.
