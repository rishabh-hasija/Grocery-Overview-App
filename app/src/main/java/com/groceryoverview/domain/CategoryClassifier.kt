package com.groceryoverview.domain

class CategoryClassifier {
    fun classify(productName: String): ItemCategory {
        val n = productName.lowercase()
        // Token string for word-boundary matching of short, ambiguous keywords
        // (e.g. "eis" must not match "reis", "ham" must not match "shampoo").
        val tokens = " " + n.replace(Regex("[^a-z0-9äöüß]"), " ") + " "
        fun has(vararg words: String) = words.any { n.contains(it) }
        fun hasWord(vararg words: String) = words.any { tokens.contains(" $it ") }

        return when {
            // ── Beverages (checked before Dairy to avoid "almond milk" / "mandelmilch" hitting Dairy) ──
            has(
                // English
                "water", "juice", "soda", "coffee", "tea", "lemonade", "sparkling",
                "kombucha", "energy drink", "sports drink", "gatorade", "powerade",
                "vitamin water", "almond milk", "oat milk", "soy milk", "smoothie", "cider",
                // German
                "wasser", "mineralwasser", "sprudel", "saft", "limo", "limonade",
                "kaffee", "tee", "cola", "bier", "wein", "sekt", "kakao",
                "milchshake", "shake", "nektar", "eistee", "energydrink",
                "mandelmilch", "hafermilch", "sojamilch", "reismilch",
                "fruchtsaft", "gemüsesaft", "smoothie",
                // Indian / international
                "chai", "masala tea", "thums up", "limca", "frooti",
                "maaza", "nimbu", "rooh afza", "falooda"
            ) || hasWord("lassi") -> ItemCategory.Beverages

            // ── Dairy ──
            has(
                // English
                "milk", "yogurt", "cheese", "butter", "cream", "creamer", "cottage",
                "sour cream", "half and half", "kefir", "whey", "cheddar", "mozzarella",
                "parmesan", "brie", "gouda", "ricotta", "custard", "dairy",
                // German
                "milch", "joghurt", "yoghurt", "käse", "kase", "butter", "sahne",
                "quark", "schmand", "frischkäse", "frischkase", "schmelzkäse",
                "schmelzkase", "emmental", "emmentaler", "camembert", "limburger",
                "hüttenkäse", "hüttenkase", "molke", "buttermilch", "dickmilch",
                "crème fraîche", "creme fraiche", "mascarpone",
                "eier", "egg",
                // Indian / international
                "paneer", "dahi", "khoya", "malai", "shrikhand"
            ) || hasWord("ei") -> ItemCategory.Dairy

            // ── Produce ──
            has(
                // English
                "apple", "banana", "tomato", "lettuce", "spinach", "carrot", "onion",
                "potato", "pepper", "cucumber", "broccoli", "celery", "garlic",
                "mushroom", "avocado", "lemon", "lime", "orange", "grape", "berry",
                "strawberry", "blueberry", "mango", "kale", "zucchini", "squash",
                "cabbage", "beet", "radish", "herb", "cilantro", "parsley", "basil",
                "ginger", "scallion",
                // German
                "apfel", "äpfel", "apfel", "banane", "tomate", "salat", "spinat",
                "karotte", "möhre", "mohre", "zwiebel", "kartoffel", "paprika",
                "gurke", "brokkoli", "sellerie", "knoblauch", "champignon", "pilz",
                "avocado", "zitrone", "limette", "orange", "mandarine", "traube",
                "erdbeere", "heidelbeere", "himbeere", "brombeere", "kirsche",
                "mango", "ananas", "kiwi", "melone", "pfirsich", "pflaume",
                "kohl", "rotkohl", "weißkohl", "rosenkohl", "wirsing",
                "zucchini", "kürbis", "rübe", "radieschen", "lauch", "porree",
                "fenchel", "artischocke", "spargel", "mais", "erbse", "bohne",
                "petersilie", "basilikum", "schnittlauch", "dill", "thymian",
                "rosmarin", "ingwer", "obst", "gemüse", "gemuese", "frucht",
                // Indian / international
                "okra", "bhindi", "brinjal", "aubergine", "eggplant", "karela",
                "curry leaves", "curryblätter", "koriander", "drumstick",
                "bottle gourd", "lauki", "doodhi", "tinda", "arbi", "taro",
                "green chilli", "grüne chili"
            ) -> ItemCategory.Produce

            // ── Bakery ──
            has(
                // English
                "bread", "bun", "cake", "muffin", "bagel", "croissant", "roll",
                "tortilla", "pita", "focaccia", "loaf", "biscuit", "donut", "pastry",
                "brownie", "waffle", "pancake mix",
                // German
                "brot", "brötchen", "semmel", "toast", "toastbrot", "weißbrot",
                "vollkornbrot", "roggenbrot", "mehrkornbrot", "baguette",
                "kuchen", "torte", "muffin", "brezel", "laugenbrezel",
                "croissant", "hörnchen", "waffel", "pfannkuchen", "berliner",
                "donut", "gebäck", "gebaeck", "backware", "laib", "stollen",
                "streuselkuchen", "käsekuchen", "kasekuchen", "ciabatta",
                // Indian / international
                "naan", "roti", "chapati", "paratha", "puri", "kulcha", "pav",
                "bhatura"
            ) -> ItemCategory.Bakery

            // ── Meat ──
            has(
                // English
                "chicken", "beef", "pork", "fish", "turkey", "bacon", "sausage",
                "salmon", "shrimp", "lamb", "steak", "ground meat", "tilapia", "tuna",
                "crab", "lobster", "pepperoni", "salami", "prosciutto",
                "brisket", "wing",
                // German
                "hähnchen", "hahnchen", "hühnchen", "huhn", "hühn",
                "rind", "rindfleisch", "rinderhack", "hackfleisch",
                "schwein", "schweinefleisch", "schweinefilet", "schweinekotelett",
                "fisch", "lachs", "forelle", "hering", "kabeljau", "thunfisch",
                "garnele", "krabbe", "tintenfisch",
                "pute", "truthahn", "gans",
                "schinken", "speck", "wurst", "bratwurst", "weißwurst", "leberwurst",
                "salami", "aufschnitt", "fleischwurst", "wiener", "frankfurter",
                "lamm", "lammfleisch", "kalbfleisch", "kalb",
                "steak", "schnitzel", "kotelett", "filet", "braten", "gulasch",
                "fischstäbchen", "fischstaebchen"
            ) || hasWord("ham", "ente", "deli", "rib", "ribs") -> ItemCategory.Meat

            // ── Frozen ──
            has(
                // English
                "frozen", "ice cream", "popsicle", "gelato", "sorbet",
                // German
                "tiefkühl", "tiefkuhl", "tk-", "gefroren", "eingefroren",
                "eiscreme", "speiseeis", "softeis", "wassereis",
                "vanilleeis", "schokoeis", "milcheis", "fruchteis", "eiskonfekt",
                "tiefkühlpizza", "tiefkühlgemüse", "tiefkühlobst",
                "tiefkühlkost", "gefriergut"
            ) || hasWord("eis") -> ItemCategory.Frozen

            // ── Snacks ──
            has(
                // English
                "chips", "cookie", "crackers", "candy", "chocolate", "pretzel", "popcorn",
                "granola bar", "trail mix", "gummy", "licorice", "snack", "nuts",
                "cashew", "almond", "pistachio", "peanut", "sunflower seed", "jerky",
                "fruit snack",
                // German
                "chips", "keks", "kekse", "cracker", "schokolade", "schokoladen",
                "bonbon", "süßigkeit", "sussigkeit", "praline", "pralinee",
                "gummibär", "gummibaren", "lakritz", "popcorn", "müsliriegel",
                "muslieriegel", "riegel", "nuss", "nüsse", "nuesse",
                "erdnuss", "cashew", "mandel", "pistazie", "walnuss",
                "sonnenblumenkern", "kürbiskern", "kurbiskern",
                "snack", "knabber", "salzstange",
                // Indian / international
                "namkeen", "bhujia", "sev ", "samosa", "pakora", "murukku",
                "mathri", "papdi", "chakli", "banana chips", "bhel", "chivda",
                "khakhra", "soan papdi", "ladoo", "laddu", "barfi", "burfi",
                "gulab jamun", "jalebi", "halwa"
            ) -> ItemCategory.Snacks

            // ── Household ──
            has(
                // English
                "soap", "detergent", "paper towel", "toilet paper", "trash bag",
                "cleaning", "bleach", "sponge", "dish soap", "laundry", "fabric softener",
                "air freshener", "candle", "aluminum foil", "plastic wrap", "ziploc",
                "garbage bag", "mop",
                // German
                "seife", "waschmittel", "spülmittel", "spulmittel", "geschirrspül",
                "küchenrolle", "kuchenrolle", "toilettenpapier", "klopapier",
                "müllbeutel", "mullbeutel", "müllsack", "mullsack",
                "reiniger", "reinigungsmittel", "allzweckreiniger",
                "bleichmittel", "chlor", "schwamm", "spülschwamm",
                "wischmopp", "besen", "kehrblech",
                "weichspüler", "weichspuler", "flüssigwaschmittel",
                "lufterfrischer", "kerze", "alufolie", "frischhaltefolie",
                "gefrierbeutel", "haushalt"
            ) -> ItemCategory.Household

            // ── Personal Care ──
            has(
                // English
                "shampoo", "toothpaste", "deodorant", "conditioner", "lotion",
                "moisturizer", "sunscreen", "razor", "bandage", "vitamins", "medicine",
                "ibuprofen", "acetaminophen", "floss", "mouthwash", "cotton", "feminine",
                "nail polish", "perfume", "cologne",
                // German
                "shampoo", "zahnpasta", "zahncreme", "deo", "deodorant",
                "duschgel", "duschbad", "badezusatz", "schaumbad",
                "körperlotion", "korperlotion", "handcreme", "gesichtscreme",
                "sonnencreme", "sonnenschutz", "after sun",
                "rasierer", "rasiergel", "rasierschaum", "rasierklinge",
                "pflaster", "verband", "wundpflaster",
                "vitamin", "medikament", "tablette", "kapsel", "ibuprofen",
                "paracetamol", "aspirin",
                "zahnseide", "mundwasser", "mundspülung",
                "wattestäbchen", "wattepads", "binden", "tampons",
                "nagellack", "parfüm", "parfum", "duftwasser"
            ) -> ItemCategory.PersonalCare

            // ── Baby ──
            has(
                // English
                "diaper", "formula", "baby food", "baby wipe", "baby lotion",
                "pacifier", "baby wash", "baby shampoo", "infant", "toddler",
                "baby cereal", "baby bottle", "teething",
                // German
                "windel", "windeln", "baby", "säuglingsnahrung", "sauglingsnahrung",
                "babynahrung", "babybrei", "beikost",
                "feuchttücher", "babytuch", "babyöl", "babyol",
                "babycreme", "schnuller", "fläschchen", "flaschchenhttps",
                "babyshampoo", "badewanne", "babywanne",
                "infant", "kleinkind"
            ) -> ItemCategory.Baby

            // ── Pet ──
            has(
                // English
                "cat food", "dog food", "pet food", "kitty litter", "cat litter",
                "dog treat", "cat treat", "pet shampoo", "flea", "aquarium",
                "bird seed", "hamster", "fish food",
                // German
                "katzenfutter", "hundefutter", "tierfutter", "tiernahrung",
                "katzenstreu", "tierstreu", "vogelfutter",
                "hundeknochen", "hundespielzeug", "katzenspielzeug",
                "tiershampoo", "hundeleine", "halsband",
                "aquarium", "goldfisch", "hamster", "kaninchen"
            ) -> ItemCategory.Pet

            // ── Pantry ──
            has(
                // English
                "rice", "pasta", "sauce", "cereal", "flour", "sugar", "salt", "pepper",
                "oil", "vinegar", "broth", "stock", "canned", "beans", "lentils",
                "chickpea", "oats", "quinoa", "couscous", "breadcrumb", "baking soda",
                "baking powder", "yeast", "honey", "syrup", "jam", "peanut butter",
                "mayonnaise", "ketchup", "mustard", "soy sauce", "hot sauce",
                // German
                "reis", "nudeln", "spaghetti", "penne", "makkaroni", "fusilli",
                "soße", "sobe", "sauce", "tomatensauce", "bolognese", "pesto",
                "mehl", "weizenmehl", "vollkornmehl",
                "zucker", "rohrzucker", "puderzucker", "vanillezucker",
                "salz", "meersalz", "pfeffer", "paprikapulver", "curry", "gewürz", "gewurz",
                "öl", "ol", "olivenöl", "olivenol", "sonnenblumenöl", "rapsöl",
                "essig", "weinessig", "apfelessig",
                "brühe", "bruhe", "bouillon", "suppe", "konserve", "dose",
                "bohnen", "linsen", "kichererbsen", "erbsen", "mais",
                "haferflocken", "müsli", "musli", "cornflakes", "granola",
                "quinoa", "bulgur", "couscous",
                "semmelbrösel", "semmelbrosel", "paniermehl",
                "backpulver", "natron", "hefe",
                "honig", "sirup", "ahornsirup", "marmelade", "konfitüre",
                "konfiture", "gelee", "aufstrich",
                "erdnussbutter", "nussnougatcreme", "nutella",
                "mayonnaise", "mayo", "ketchup", "senf", "mostrich",
                "sojasoße", "sojasosse", "worcester", "tabasco",
                // Indian / international staples
                "atta", "maida", "besan", "sooji", "suji", "rava", "poha",
                "basmati", "dal", "daal", "toor", "moong", "mung", "chana",
                "urad", "masoor", "rajma", "chole", "ghee", "masala",
                "turmeric", "haldi", "jeera", "cumin", "dhania", "cardamom",
                "elaichi", "clove", "laung", "zimt", "saffron", "safran",
                "hing", "asafoetida", "methi", "fenugreek", "chutney", "achar",
                "pickle", "papad", "papadum", "tamarind", "imli", "jaggery",
                "coconut milk", "kokosmilch", "kokosnussmilch", "tofu",
                "curry paste", "currypaste", "kichererbsenmehl", "sambar",
                "rasam", "idli", "dosa", "vermicelli", "seviyan", "kheer mix"
            ) -> ItemCategory.Pantry

            else -> ItemCategory.Unknown
        }
    }
}
