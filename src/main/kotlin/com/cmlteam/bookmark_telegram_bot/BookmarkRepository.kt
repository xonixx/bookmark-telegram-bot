package com.cmlteam.bookmark_telegram_bot

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BookmarkRepository : MongoRepository<Bookmark, String> {

}