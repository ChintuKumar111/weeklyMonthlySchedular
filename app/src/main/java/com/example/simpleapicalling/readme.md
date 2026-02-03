# 📱 Freshyzo Module App — Developer Documentation

## 🧠 Project Overview

This Android application allows users to select **subscription plans** based on **weekly or monthly schedules**, choose **days/months**, assign **quantities**, pick **start/end dates**, and automatically calculate **total pricing**.

The project demonstrates:

* RecyclerView usage
* BottomSheet dialogs
* DatePicker dialogs
* Quantity selection logic
* Price calculation
* Kotlin + ViewBinding

---

## 🏗️ Architecture Overview

### Main Components:

| File                    | Purpose                          |
|-------------------------| -------------------------------- |
| `MainActivity.kt`       | Controls UI, logic, and pricing  |
| `DayQuantityAdapter.kt` | Handles day/month selection list |
| `DayDateModel.kt`       | Data model for days/months       |
| XML Layouts             | UI structure                     |

---

## 📦 Data Model — `DateModel.kt`

```kotlin
data class DateModel(
    val day: String,
    var quantity: Int,
    var isSelected: Boolean
)
```

### Field Meaning:

| Field        | Purpose           |
| ------------ | ----------------- |
| `day`        | Name of day/month |
| `quantity`   | Selected quantity |
| `isSelected` | Selection state   |

---

## 🧩 Adapter — `DayQuantityAdapter.kt`

### Responsibilities:

* Display days/months in RecyclerView
* Highlight selected items
* Update quantity
* Notify Activity when selection or quantity changes

### Key Features:

* Single selection mode
* Auto quantity = 1 when selecting
* Reset quantity on deselect
* Price recalculation callback

### Core Logic Summary:

| Action          | Result                   |
| --------------- | ------------------------ |
| Select item     | Highlight + quantity = 1 |
| Click again     | Deselect + quantity = 0  |
| Quantity change | Updates total price      |

---

## 🏠 Main Screen — `MainActivity.kt`

### Responsibilities:

* Manage Weekly / Monthly subscription UI
* Handle date picking
* Manage quantity dialogs
* Calculate total price
* Open BottomSheet selection

---

## 📅 Weekly Feature Flow

### Step 1 — Open Bottom Sheet

User selects days using RecyclerView.

### Step 2 — Select Quantity

User chooses quantity using AlertDialog.

### Step 3 — Update UI

Adapter updates UI state and total price.

### Step 4 — Date Selection

User selects start/end dates.

### Step 5 — Total Calculation

```
Total = sum(day quantities) × ₹60
```

---

## 📆 Monthly Feature Flow

### Step 1 — Select Months

User chooses number of months.

### Step 2 — Select Quantity

User selects product quantity.

### Step 3 — Auto Calculate Days

```
Total Days = months × 30
```

### Step 4 — Price Calculation

```
Total = totalDays × quantity × ₹60
```

---

## 💰 Pricing Rules

| Mode    | Formula                       |
| ------- | ----------------------------- |
| Weekly  | `sum(quantities) × 60`        |
| Monthly | `months × 30 × quantity × 60` |

---

## 📅 Date Picker Rules

| Rule                             | Purpose             |
| -------------------------------- | ------------------- |
| Start date cannot be past        | Valid booking       |
| End date must be after start     | Logical date range  |
| Monthly end date auto-calculated | Subscription length |

---

## 🎨 UI Behavior Summary

| Feature         | Behavior                        |
| --------------- | ------------------------------- |
| Selected item   | Green highlight                 |
| Unselected item | White background                |
| Quantity button | Visible only when item selected |
| Tabs            | Highlight active mode           |

---

## 🔄 Callback System

### Adapter → Activity

```kotlin
onQuantityChanged(total: Int)
onSelectionChanged(hasSelection: Boolean)
```

Used to:

* Update total price
* Show/hide quantity button

---

## 🧪 Sample Example

### Weekly Example

| Day       | Qty | Price    |
| --------- | --- | -------- |
| Mon       | 2   | ₹120     |
| Wed       | 1   | ₹60      |
| **Total** | 3   | **₹180** |

### Monthly Example

| Months | Qty | Days | Total   |
| ------ | --- | ---- | ------- |
| 2      | 3   | 60   | ₹10,800 |

---




## 🎓 Learning Value

This project helps understand:

* RecyclerView Adapter patterns
* Kotlin lambdas & callbacks
* UI state management
* Dialogs & BottomSheets
* Real-world pricing logic

---

## ✅ End of Documentation


