package com.yourssu.inhouse.member

import com.yourssu.inhouse.member.implement.KoreanTextUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KoreanTextUtilsTest {

    @Test
    fun `disassemble decomposes Korean syllables into jamo`() {
        assertEquals("ㅎㅗㅇㄱㅣㄹㄷㅗㅇ", KoreanTextUtils.disassemble("홍길동"))
    }

    @Test
    fun `disassemble preserves ASCII characters`() {
        assertEquals("hong123", KoreanTextUtils.disassemble("hong123"))
    }

    @Test
    fun `disassemble handles mixed Korean and ASCII input`() {
        val result = KoreanTextUtils.disassemble("홍hong")
        assertEquals("ㅎㅗㅇhong", result)
    }

    @Test
    fun `disassemble handles syllable with final consonant`() {
        // 강 = ㄱ + ㅏ + ㅇ
        assertEquals("ㄱㅏㅇ", KoreanTextUtils.disassemble("강"))
    }

    @Test
    fun `extractChosung extracts initial consonants from Korean syllables`() {
        assertEquals("ㅎㄱㄷ", KoreanTextUtils.extractChosung("홍길동"))
    }

    @Test
    fun `extractChosung preserves non-Korean characters`() {
        assertEquals("hong", KoreanTextUtils.extractChosung("hong"))
    }

    @Test
    fun `extractChosung handles mixed input`() {
        assertEquals("ㅎhong", KoreanTextUtils.extractChosung("홍hong"))
    }

    @Test
    fun `matches returns true for blank query`() {
        assertTrue(KoreanTextUtils.matches("홍길동", ""))
        assertTrue(KoreanTextUtils.matches("홍길동", "   "))
    }

    @Test
    fun `matches returns true for exact full match`() {
        assertTrue(KoreanTextUtils.matches("홍길동", "홍길동"))
    }

    @Test
    fun `matches returns true for partial Korean match`() {
        assertTrue(KoreanTextUtils.matches("홍길동", "길동"))
    }

    @Test
    fun `matches returns true for chosung-only pattern`() {
        assertTrue(KoreanTextUtils.matches("홍길동", "ㅎㄱㄷ"))
    }

    @Test
    fun `matches returns true for partial chosung pattern`() {
        assertTrue(KoreanTextUtils.matches("홍길동", "ㅎㄱ"))
    }

    @Test
    fun `matches returns false for non-matching chosung pattern`() {
        assertFalse(KoreanTextUtils.matches("홍길동", "ㅎㄴㄷ"))
    }

    @Test
    fun `matches returns false for completely different string`() {
        assertFalse(KoreanTextUtils.matches("홍길동", "김철수"))
    }

    @Test
    fun `matches is case-insensitive for ASCII`() {
        assertTrue(KoreanTextUtils.matches("hong", "HONG"))
        assertTrue(KoreanTextUtils.matches("HONG", "hong"))
    }

    @Test
    fun `matches returns true for single syllable query`() {
        assertTrue(KoreanTextUtils.matches("홍길동", "홍"))
    }

    @Test
    fun `matches returns false when query not contained in field`() {
        assertFalse(KoreanTextUtils.matches("홍길동", "홍길동동"))
    }
}
