package com.groceryoverview.domain

class CategoryClassifier {
    fun classify(productName: String): ItemCategory {
        val n = productName.lowercase()
        fun has(vararg words: String) = words.any { n.contains(it) }

        return when {
            // Beverages checked before Dairy so "almond milk"/"oat milk" don't hit has("milk")
            has(
                "water", "juice", "soda", "coffee", "tea", "lemonade", "sparkling",
                "kombucha", "energy drink", "sports drink", "gatorade", "powerade",
                "vitamin water", "almond milk", "oat milk", "soy milk", "smoothie", "cider"
            ) -> ItemCategory.Beverages

            has(
                "milk", "yogurt", "cheese", "butter", "cream", "creamer", "cottage",
                "sour cream", "half and half", "kefir", "whey", "cheddar", "mozzarella",
                "parmesan", "brie", "gouda", "ricotta", "custard", "dairy"
            ) -> ItemCategory.Dairy

            has(
                "apple", "banana", "tomato", "lettuce", "spinach", "carrot", "onion",
                "potato", "pepper", "cucumber", "broccoli", "celery", "garlic",
                "mushroom", "avocado", "lemon", "lime", "orange", "grape", "berry",
                "strawberry", "blueberry", "mango", "kale", "zucchini", "squash",
                "cabbage", "beet", "radish", "herb", "cilantro", "parsley", "basil",
                "ginger", "scallion"
            ) -> ItemCategory.Produce

            has(
                "bread", "bun", "cake", "muffin", "bagel", "croissant", "roll",
                "tortilla", "pita", "focaccia", "loaf", "biscuit", "donut", "pastry",
                "brownie", "waffle", "pancake mix"
            ) -> ItemCategory.Bakery

            has(
                "chicken", "beef", "pork", "fish", "turkey", "ham", "bacon", "sausage",
                "salmon", "shrimp", "lamb", "steak", "ground meat", "tilapia", "tuna",
                "crab", "lobster", "deli", "pepperoni", "salami", "prosciutto",
                "brisket", "rib", "wing"
            ) -> ItemCategory.Meat

            has(
                "frozen", "ice cream", "popsicle", "gelato", "sorbet", "frozen pizza",
                "frozen meal", "tv dinner", "edamame frozen", "frozen vegetable",
                "frozen fruit", "ice"
            ) -> ItemCategory.Frozen

            has(
                "chips", "cookie", "crackers", "candy", "chocolate", "pretzel", "popcorn",
                "granola bar", "trail mix", "gummy", "licorice", "snack", "nuts",
                "cashew", "almond", "pistachio", "peanut", "sunflower seed", "jerky",
                "fruit snack"
            ) -> ItemCategory.Snacks

            has(
                "soap", "detergent", "paper towel", "toilet paper", "trash bag",
                "cleaning", "bleach", "sponge", "dish soap", "laundry", "fabric softener",
                "air freshener", "candle", "aluminum foil", "plastic wrap", "ziploc",
                "garbage bag", "mop"
            ) -> ItemCategory.Household

            has(
                "shampoo", "toothpaste", "deodorant", "conditioner", "lotion",
                "moisturizer", "sunscreen", "razor", "bandage", "vitamins", "medicine",
                "ibuprofen", "acetaminophen", "floss", "mouthwash", "cotton", "feminine",
                "nail polish", "perfume", "cologne"
            ) -> ItemCategory.PersonalCare

            has(
                "diaper", "formula", "baby food", "baby wipe", "baby lotion",
                "pacifier", "baby wash", "baby shampoo", "infant", "toddler",
                "baby cereal", "baby bottle", "teething"
            ) -> ItemCategory.Baby

            has(
                "cat food", "dog food", "pet food", "kitty litter", "cat litter",
                "dog treat", "cat treat", "pet shampoo", "flea", "aquarium",
                "bird seed", "hamster", "fish food"
            ) -> ItemCategory.Pet

            has(
                "rice", "pasta", "sauce", "cereal", "flour", "sugar", "salt", "pepper",
                "oil", "vinegar", "broth", "stock", "canned", "beans", "lentils",
                "chickpea", "oats", "quinoa", "couscous", "breadcrumb", "baking soda",
                "baking powder", "yeast", "honey", "syrup", "jam", "peanut butter",
                "mayonnaise", "ketchup", "mustard", "soy sauce", "hot sauce"
            ) -> ItemCategory.Pantry

            else -> ItemCategory.Unknown
        }
    }
}
