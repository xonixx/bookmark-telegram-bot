package com.cmlteam.bookmark_telegram_bot

import java.time.Instant
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document

@Document(value = Bookmark.COLLECTION)
class Bookmark(
    @Id var id: String? = null,
    var userId: Int = 0,
    var messageId: Int = 0,
    var url: String = "",
    var read: Boolean = false,
    @CreatedDate var createDate: Instant? = null,
    @LastModifiedDate var lastModifiedDate: Instant? = null
) {
  constructor() : this("", 0, 0)
  constructor(
      url: String,
      userId: Int,
      messageId: Int
  ) : this(null, userId, messageId, url, false, Instant.now(), Instant.now())

  companion object {
    const val COLLECTION = "bookmark"
  }
}
