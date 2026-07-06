package com.groceryoverview.domain

/**
 * Detects the store a receipt came from by scanning the OCR text header.
 * Covers the major German chains; falls back to the first plausible header
 * line for independent shops (e.g. local or Indian grocery stores).
 */
object StoreDetector {

    private val CHAIN_PATTERNS: List<Pair<Regex, String>> = listOf(
        Regex("""(?i)\brewe\b""") to "REWE",
        Regex("""(?i)\bedeka\b""") to "EDEKA",
        Regex("""(?i)\blidl\b""") to "Lidl",
        Regex("""(?i)\baldi\s*s(ü|ue?)d\b""") to "ALDI SÜD",
        Regex("""(?i)\baldi\s*nord\b""") to "ALDI Nord",
        Regex("""(?i)\baldi\b""") to "ALDI",
        Regex("""(?i)\bkaufland\b""") to "Kaufland",
        Regex("""(?i)\bnetto\b.{0,25}(marken|discount|city)""") to "Netto",
        Regex("""(?i)^\s*netto\s*$""", RegexOption.MULTILINE) to "Netto",
        Regex("""(?i)\bpenny\b""") to "PENNY",
        Regex("""(?i)\bdm[-\s]?drogerie\b""") to "dm",
        Regex("""(?i)\brossmann\b""") to "ROSSMANN",
        Regex("""(?i)\bm(ü|ue?)ller\b.{0,20}(handels|drogerie)""") to "Müller",
        Regex("""(?i)\bnorma\b""") to "NORMA",
        Regex("""(?i)\btegut\b""") to "tegut",
        Regex("""(?i)\bglobus\b""") to "GLOBUS",
        Regex("""(?i)\bhit\s+markt\b""") to "HIT",
        Regex("""(?i)\balnatura\b""") to "Alnatura",
        Regex("""(?i)\bdenn'?s\b""") to "denn's Biomarkt",
        Regex("""(?i)\bfamila\b""") to "famila",
        Regex("""(?i)\bmarktkauf\b""") to "Marktkauf",
        Regex("""(?i)\bcombi\b""") to "Combi",
        Regex("""(?i)\bv-?markt\b""") to "V-Markt"
    )

    private val HEADER_NOISE = Regex(
        """(?i)(gmbh|co\.\s*kg|ohg|e\.\s*k\.|stra(ß|ss)e|str\.|tel\.?|fax|www\.|\.de|\.com|ust|uid|steuer|filiale|kundenbeleg|beleg|bon|kasse|datum|uhrzeit|willkommen|danke?\b|vielen dank)"""
    )

    /**
     * @return the detected chain name, a plausible independent-store name from
     * the header, or null if nothing usable was found.
     */
    fun detect(rawText: String): String? {
        // Lowercase before matching: Kotlin/Java (?i) only case-folds ASCII,
        // so uppercase "Ü" would not match a pattern's lowercase "ü" otherwise.
        val headText = rawText.lineSequence().take(12).joinToString("\n").lowercase()
        val bodyText = rawText.lowercase()
        for ((pattern, name) in CHAIN_PATTERNS) {
            if (pattern.containsMatchIn(headText) || pattern.containsMatchIn(bodyText)) {
                return name
            }
        }
        // Fallback: first header line that looks like a shop name.
        return rawText.lineSequence()
            .take(6)
            .map { it.trim() }
            .firstOrNull { line ->
                line.length in 3..40 &&
                    line.count { it.isLetter() } >= 3 &&
                    line.count { it.isDigit() } <= 2 &&
                    !HEADER_NOISE.containsMatchIn(line.lowercase())
            }
    }
}
