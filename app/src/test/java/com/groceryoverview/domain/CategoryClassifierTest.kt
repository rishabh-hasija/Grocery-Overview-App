package com.groceryoverview.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryClassifierTest {

    private val classifier = CategoryClassifier()

    private fun check(expected: ItemCategory, vararg names: String) {
        for (name in names) {
            assertEquals("'$name'", expected, classifier.classify(name))
        }
    }

    @Test
    fun `classifies German supermarket products`() {
        check(ItemCategory.Dairy, "G&G H-MILCH 1,5%", "NATURJOGHURT", "GOUDA JUNG", "BIO-EIER 10ER")
        check(ItemCategory.Produce, "BIO BANANE", "TOMATEN RISPEN", "KARTOFFELN FESTK.")
        check(ItemCategory.Bakery, "ROGGENBROT 500G", "LAUGENBREZEL", "CIABATTA")
        check(ItemCategory.Meat, "RINDERHACK 400G", "HÄHNCHENBRUSTFILET", "LEBERWURST")
        check(ItemCategory.Frozen, "TIEFKÜHLPIZZA MARGHERITA", "SPEISEEIS VANILLE")
        check(ItemCategory.Beverages, "MINERALWASSER CLASSIC", "APFELSAFT", "MANDELMILCH")
        check(ItemCategory.Snacks, "CHIO CHIPS", "STICKS LAKRITZ", "MÜSLIRIEGEL")
        check(ItemCategory.Household, "KLOPAPIER 3-LAGIG", "SPÜLMITTEL KONZENTRAT")
        check(ItemCategory.PersonalCare, "ZAHNPASTA MINZE", "ISANA SHAMPOO")
        check(ItemCategory.Baby, "BABYLOVE WINDELN GR.4")
        check(ItemCategory.Pet, "KATZENFUTTER GEFLÜGEL")
        check(ItemCategory.Pantry, "SPAGHETTI NO.5", "WEIZENMEHL 405", "OLIVENÖL EXTRA")
    }

    @Test
    fun `classifies Indian and international products`() {
        check(
            ItemCategory.Pantry,
            "AASHIRVAAD ATTA 5KG", "TOOR DAL 1KG", "GARAM MASALA",
            "BASMATI RICE", "GHEE PURE 1L", "HALDI POWDER", "ACHAR MIXED PICKLE", "PAPAD UDAD"
        )
        check(ItemCategory.Dairy, "PANEER FRESH 400G", "DAHI NATURAL")
        check(ItemCategory.Bakery, "TANDOORI NAAN 4PC", "CHAPATI 10PC")
        check(ItemCategory.Snacks, "ALOO BHUJIA", "SAMOSA 12PC", "SOAN PAPDI")
        check(ItemCategory.Beverages, "MANGO LASSI", "MASALA CHAI")
        check(ItemCategory.Produce, "OKRA FRESH", "BHINDI 500G")
    }

    @Test
    fun `unknown products fall back to Unknown`() {
        check(ItemCategory.Unknown, "XYZQWERTY", "GESCHENKKARTE 25")
    }
}
