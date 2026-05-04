package com.groceryoverview.domain

class CategoryClassifier {
    fun classify(productName: String): ItemCategory {
        val normalized = productName.lowercase()

        return when {
            normalized.contains("milk") || normalized.contains("yogurt") || normalized.contains("cheese") || normalized.contains("butter") -> ItemCategory.Dairy
            normalized.contains("apple") || normalized.contains("banana") || normalized.contains("tomato") || normalized.contains("lettuce") || normalized.contains("spinach") -> ItemCategory.Produce
            normalized.contains("bread") || normalized.contains("bun") || normalized.contains("cake") -> ItemCategory.Bakery
            normalized.contains("chicken") || normalized.contains("beef") || normalized.contains("pork") || normalized.contains("fish") -> ItemCategory.Meat
            normalized.contains("frozen") || normalized.contains("ice cream") -> ItemCategory.Frozen
            normalized.contains("water") || normalized.contains("juice") || normalized.contains("soda") || normalized.contains("coffee") -> ItemCategory.Beverages
            normalized.contains("chips") || normalized.contains("cookie") || normalized.contains("crackers") -> ItemCategory.Snacks
            normalized.contains("soap") || normalized.contains("detergent") || normalized.contains("paper towel") -> ItemCategory.Household
            normalized.contains("shampoo") || normalized.contains("toothpaste") || normalized.contains("deodorant") -> ItemCategory.PersonalCare
            normalized.contains("diaper") || normalized.contains("formula") -> ItemCategory.Baby
            normalized.contains("cat food") || normalized.contains("dog food") -> ItemCategory.Pet
            normalized.contains("rice") || normalized.contains("pasta") || normalized.contains("sauce") || normalized.contains("cereal") -> ItemCategory.Pantry
            else -> ItemCategory.Unknown
        }
    }
}
