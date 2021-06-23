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

    val bookmarkId = bookmarkRepository.findByUrlAndUserId(link1, user1.id())!!.id

    assertThat(botTester.processUserText(user1, link1).text)
        .matches("⚠️ Already in backlog /mark_read_${bookmarkId}. Links in backlog: 1 /random")
  }

  @Test
  fun testAddTwoLinks() {
    botTester.processUserText(user1, link1)

    assertEquals(
        "✅ Ok, saved link. Links in backlog: 2 /random",
        botTester.processUserText(user1, link2).text)
  }

  @Test
  fun testAddTwoLinksByDifferentUsers() {
    botTester.processUserText(user1, link1)

    assertEquals(
        "✅ Ok, saved link. Links in backlog: 1 /random",
        botTester.processUserText(user2, link2).text)
  }

  @Test
  fun testMarkRead() {
    botTester.processUserText(user1, link1)
    botTester.processUserText(user1, link2)

    val bookmarkId = bookmarkRepository.findByUrlAndUserId(link2, user1.id())!!.id

    assertThat(botTester.processUserText(user1, "/mark_read_${bookmarkId}").text)
        .isEqualTo("✅ $link2 marked read.\n/undo_$bookmarkId\nLinks in backlog: 1 /random")
  }

  @Test
  fun testMarkUnread() {
    botTester.processUserText(user1, link1)
    botTester.processUserText(user1, link2)

    val bookmarkId = bookmarkRepository.findByUrlAndUserId(link2, user1.id())!!.id

    botTester.processUserText(user1, "/mark_read_${bookmarkId}")

    assertThat(botTester.processUserText(user1, "/undo_${bookmarkId}").text)
        .isEqualTo("✅ $link2 marked unread.\n/mark_read_$bookmarkId\nLinks in backlog: 2 /random")
  }

  @Test
  fun testRandomEmpty() {
    assertThat(botTester.processUserText(user1, "/random").text)
        .isEqualTo("⚠️ You don't have any bookmarks yet")
  }

  @Test
  fun testRandomOne() {
    botTester.processUserText(user1, link1)

    val bookmarkId = bookmarkRepository.findByUrlAndUserId(link1, user1.id())!!.id

    assertThat(botTester.processUserText(user1, "/random").text)
        .isEqualTo("$link1\n\n/random /mark_read_$bookmarkId")
  }

  @Test
  fun testRandomTwo() {
    botTester.processUserText(user1, link1)
    botTester.processUserText(user1, link2)

    val bookmarkId1 = bookmarkRepository.findByUrlAndUserId(link1, user1.id())!!.id
    val bookmarkId2 = bookmarkRepository.findByUrlAndUserId(link2, user1.id())!!.id

    val repliesSet = HashSet<String>()

    for (i in 1..20) {
      repliesSet.add(botTester.processUserText(user1, "/random").text)
    }

    assertThat(repliesSet)
        .isEqualTo(
            setOf(
                "$link1\n\n/random /mark_read_$bookmarkId1",
                "$link2\n\n/random /mark_read_$bookmarkId2"))
  }
}
