package com.yourssu.inhouse.member.implement

object KoreanTextUtils {
    private val CHO = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
        'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )
    private val JUNG = charArrayOf(
        'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ',
        'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ',
        'ㅡ', 'ㅢ', 'ㅣ'
    )
    private val JONG = charArrayOf(
        ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ',
        'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ',
        'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    private val CHOSUNG_SET = CHO.toHashSet()

    fun disassemble(text: String): String {
        val sb = StringBuilder()
        for (c in text) {
            val code = c.code
            if (code in 0xAC00..0xD7A3) {
                val offset = code - 0xAC00
                val jong = offset % 28
                val jung = (offset / 28) % 21
                val cho = offset / 28 / 21
                sb.append(CHO[cho])
                sb.append(JUNG[jung])
                if (jong > 0) sb.append(JONG[jong])
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun extractChosung(text: String): String {
        val sb = StringBuilder()
        for (c in text) {
            val code = c.code
            if (code in 0xAC00..0xD7A3) {
                val cho = (code - 0xAC00) / 28 / 21
                sb.append(CHO[cho])
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun isChosungPattern(text: String): Boolean =
        text.isNotEmpty() && text.all { it in CHOSUNG_SET }

    fun matches(field: String, query: String): Boolean {
        if (query.isBlank()) return true
        val lower = field.lowercase()
        val lowerQuery = query.lowercase()

        if (isChosungPattern(query)) {
            return extractChosung(lower).contains(query)
        }

        return disassemble(lower).contains(disassemble(lowerQuery))
    }
}
