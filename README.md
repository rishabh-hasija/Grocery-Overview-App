# Grocery Overview Android App

A simple Android app for scanning grocery receipts, extracting purchased items and prices, storing receipt data locally, and reviewing spending summaries.

## Updated repository contents

This repository now includes:
- App usage instructions
- Feature descriptions
- Local data storage details
- Release APK location for testing

## How to use the app

1. Open the app.
2. From Home, tap **Scan Receipt**.
3. The camera preview will open.
4. Hold the receipt steady and tap **Capture and Scan**.
5. The app extracts receipt text in memory, parses items, and saves the data.
6. Return to Home and tap **View Summary** to review spending.

## Features and how to use them

- **Home Screen**
  - Shows the number of saved receipts.
  - Tap **Scan Receipt** to add a new receipt.
  - Tap **View Summary** to see spending analysis.

- **Scan Receipt**
  - Captures the receipt with the camera.
  - Uses ML Kit OCR to extract text.
  - Parses the receipt into structured data and stores it locally.

- **Summary Screen**
  - Displays total spending across saved receipts.
  - Shows spending by item category.

## Where scanned receipt data is stored

- Data is stored locally on the device using Room.
- Stored receipt data includes:
  - date of purchase
  - raw extracted text
  - total amount
  - parsed receipt items
  - item category assignments
- Receipt images are not stored in the database.

## Analysis the app provides

- Total amount spent.
- Spending grouped by category.
- Item totals computed from stored receipt items.

### Viewing the analysis

1. Scan one or more receipts.
2. On the Home screen, tap **View Summary**.
3. Review the total spent and category spending breakdown.

## Release APK location

After building the release version, the APK is available at:

- `app/build/outputs/apk/release/app-release-unsigned.apk`
- `app/build/outputs/apk/release/app-release-signed.apk`

Use `app-release-signed.apk` for testing on Android devices.

## Project structure

```text
Grocery Overview App/
├── app/
│   ├── src/main/
│   │   ├── java/com/groceryoverview/
│   │   │   ├── data/
│   │   │   │   ├── local/          # Room database, DAO, entities, repository
│   │   │   │   └── ocr/            # Receipt text extraction using ML Kit
│   │   │   ├── domain/             # Business models and summary logic
│   │   │   ├── ui/                 # ViewModel, UI state, Compose screens
│   │   │   ├── AppContainer.kt     # Dependency container
│   │   │   └── MainActivity.kt     # App entry point
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
├── .gitignore
└── README.md
```
