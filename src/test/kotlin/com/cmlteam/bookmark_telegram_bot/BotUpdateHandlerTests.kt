package com.cmlteam.bookmark_telegram_bot

import com.cmlteam.telegram_bot_common.test.BotTester
import com.cmlteam.telegram_bot_common.test.TelegramFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("test")
class BotUpdateHandlerTests(
    @Autowired private val bookmarkRepository: BookmarkRepository,
    @Autowired private val bookmarkService: BookmarkService,
) {
  private val telegramFactory: TelegramFactory = TelegramFactory()
  private val botTester: BotTester =
      BotTester(
          BotUpdateHandlerImpl(
              bookmarkService,
              object : AdminUserChecker {
                override val adminUser: Long
                  get() = 7
              },
              100))

  private val user1 = telegramFactory.user(1, "John", "Doe")
  private val user2 = telegramFactory.user(2, "Jane", "Smith")
  private val link1 = "http://google.com"
  private val link2 = "https://example.com"

  @BeforeEach
  fun cleanupDb() {
    bookmarkRepository.deleteAll()
  }

  @Test
  fun testAddLink() {
    assertEquals(
        "✅ Ok, saved link. Links in backlog: 1 /random",
        botTester.processUserText(user1, link1).text)
  }

  @Test
  fun testAddSameLink() {
    botTester.processUserText(user1, link1)

    assertThat(botTester.processUserText(user1, link1).text)
        .matches("⚠️ Already in backlog /mark_read_[a-f\\d]{24}. Links in backlog: 1 /random")
  }

  @Test
  fun testAddTwoLinks() {
    botTester.processUserText(user1, link1)
    assertEquals(
        "✅ Ok, saved link. Links in backlog: 2 /random",
        botTester.processUserText(user1, link2).text)
  }
}
