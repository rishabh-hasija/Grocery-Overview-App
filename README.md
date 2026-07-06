# Grocery Overview Android App (v1.1.0)

A simple Android app for scanning grocery receipts, extracting purchased items and prices, storing receipt data locally, and reviewing spending summaries.

## What's new in v1.1.0

- **German store receipt parsing**: dedicated support for receipt formats from REWE, EDEKA, Lidl, ALDI (Süd/Nord), Kaufland, Netto, PENNY, dm, Rossmann and independent stores (including Indian grocery stores). Handles quantity lines ("2 x 1,19"), weight-priced items ("0,754 kg x 1,63 EUR/kg"), Pfand (deposits), discounts (including trailing-minus notation), VAT class markers, and both "1,99" and "1.99" decimal formats.
- **Automatic store, date and total detection** from the scanned receipt text.
- **Analytics screen with time filters**: Week, Month, 3 Months, 6 Months, Year, plus a Custom date range picker (up to one year). Shows total spend in €, spend and quantity per item, and category breakdown with item counts.
- **Expanded categories**: Deposit and Discount categories, plus Indian/international product keywords (atta, dal, paneer, masala, ghee, naan, and many more).
- **Unit tests**: parser tests with realistic receipt samples per store, plus classifier, aggregator and date-filter tests. CI runs them on every push.

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
- Tap **Update App** to check a hosted release manifest and install a newer APK directly from the app.

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

## App updates

The app now includes an in-app **Update App** button. It reads the manifest from the GitHub Releases URL configured in `app/build.gradle.kts` and then downloads the APK from GitHub Releases.

To publish a new update:

1. Push a tag such as `v1.0.2` or run the `Build and Publish Release` workflow manually.
2. Add the release signing secrets in GitHub Actions:
   - `ANDROID_KEYSTORE_BASE64`
   - `ANDROID_KEYSTORE_PASSWORD`
   - `ANDROID_KEY_ALIAS`
   - `ANDROID_KEY_PASSWORD`
3. Bump the app version in `app/build.gradle.kts`.
4. The workflow generates `update.json` from the build version and uploads both `grocery-overview-release.apk` and `update.json` to the GitHub Release.

When a newer `versionCode` is found, the app downloads the APK and opens the Android installer for the user to confirm the update.

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
