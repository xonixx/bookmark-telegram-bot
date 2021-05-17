package com.cmlteam.bookmark_telegram_bot

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UtilsTests {

	@Test
	fun testIsValidUrl() {
		assertTrue(isValidUrl("http://google.com"))
		assertTrue(isValidUrl("http://google.com/"))
		assertTrue(isValidUrl("https://google.com/"))
		assertTrue(isValidUrl("HTTPS://GOOGLE.com/"))
		assertTrue(isValidUrl("https://google.com/aaa?bbb"))
		assertTrue(isValidUrl("https://google.com/aaa?bbb#ccc"))
		assertFalse(isValidUrl(null))
		assertFalse(isValidUrl(""))
		assertFalse(isValidUrl("ftp://google.com/"))
		assertFalse(isValidUrl("some random text"))
		assertFalse(isValidUrl(" https://google.com/"))
		assertFalse(isValidUrl("https://google.com/ "))
	}

	@Test
	fun testExtractLinks() {
		assertEquals(3, extractLinks("""
			aaaa
			http://google.com
			bbb
			ccccc
			https://example.com/123
			HTTPS://DOMAIN.NET
		""".trimIndent()).size)
	}

}
