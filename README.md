# Grocery Overview Android App

An Android app for scanning grocery bills, extracting purchased products and prices, and summarizing shopping behavior over time.

## Project Structure

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

## What the app does

1. Scan a grocery bill using the camera.
2. Extract purchased products and prices from the receipt text.
3. Store receipts locally with purchase dates.
4. Build summaries for any selected time period.
5. Group products into categories to help identify buying patterns and optimization opportunities.

## Suggested tech stack

- Kotlin
- Jetpack Compose for UI
- CameraX for bill capture
- ML Kit Text Recognition for OCR
- Room for local storage
- Hilt for dependency injection
- WorkManager for background syncing or heavy processing later

## Core flow

1. User captures or imports a receipt image.
2. OCR extracts the raw text.
3. A parser converts OCR text into receipt line items.
4. Items are normalized and categorized.
5. The app stores the receipt locally.
6. Summary screens aggregate items by date range, category, and product.

## Storage policy

- The app processes the receipt image only in memory.
- It stores extracted text, parsed line items, dates, totals, and categories.
- It does not persist receipt images, file paths, or image blobs, which keeps device storage usage low.

## Data model

- `Receipt`
  - id
  - storeName
  - purchaseDate
  - rawText
  - totalAmount

- `ReceiptItem`
  - id
  - receiptId
  - name
  - quantity
  - unitPrice
  - totalPrice
  - category

## Key screens

- Home dashboard
- Scan receipt screen
- Receipt detail screen
- Time period summary screen
- Category breakdown screen
- Product trend screen

## Implementation notes

- Start with local-only storage.
- Use heuristics for category assignment first, then improve with user corrections.
- Keep the OCR parser modular so it can be improved without touching the UI.
- Allow manual item correction because OCR on grocery bills is often imperfect.

## Next build step

Implement the Android project scaffolding, then wire the scan flow to OCR, persistence, and summary views.
