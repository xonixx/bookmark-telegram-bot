package com.cmlteam.bookmark_telegram_bot

import org.springframework.stereotype.Service

@Service
class BookmarkService(private val bookmarkRepository: BookmarkRepository) {
    fun save(bookmark: Bookmark) {
        bookmarkRepository.save(bookmark)
    }

}