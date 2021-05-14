package com.cmlteam.bookmark_telegram_bot

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(value = "bookmark")
class Bookmark {
    @Id
    var id: String? = null
    var userId = 0
    var messageId = 0

    var url: String = ""

    @CreatedDate
    var createDate: Instant? = null

    @LastModifiedDate
    var lastModifiedDate: Instant? = null

    constructor()
    constructor(url: String, userId: Int, messageId: Int) : this(
        null,
        userId,
        messageId,
        url,
        Instant.now(),
        Instant.now()
    )

    constructor(
        id: String?,
        userId: Int,
        messageId: Int,
        url: String,
        createDate: Instant?,
        lastModifiedDate: Instant?
    ) {
        this.id = id
        this.userId = userId
        this.messageId = messageId
        this.url = url
        this.createDate = createDate
        this.lastModifiedDate = lastModifiedDate
    }
}