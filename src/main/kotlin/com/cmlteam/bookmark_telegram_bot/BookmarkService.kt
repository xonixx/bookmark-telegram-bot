package com.cmlteam.bookmark_telegram_bot

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.aggregation.SampleOperation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Service


@Service
class BookmarkService(private val bookmarkRepository: BookmarkRepository, private val mongoTemplate: MongoTemplate) {
    /**
     * Only store unread
     * @return was stored?
     */
    fun storeBookmark(bookmark: Bookmark): Boolean {
        if (!bookmarkRepository.existsByUrlAndUserId(bookmark.url, bookmark.userId)) {
            bookmarkRepository.save(bookmark)
            return true
        }
        return false
    }

    /**
     * @return the number of really stored (the ones not yet existing)
     */
    fun storeBookmarks(bookmarks: Collection<Bookmark>): Int {
        var res = 0
        for (bookmark in bookmarks) {
            if (storeBookmark(bookmark))
                res++
        }
        return res
    }

    fun getTotal(userId: Int): Long {
        return bookmarkRepository.countByUserIdAndReadNot(userId, true)
    }

    fun getRandom(userId: Int): Bookmark? {
        val matchStage: MatchOperation =
            Aggregation.match(
                Criteria.where("userId").isEqualTo(userId)
                    .and("read").ne(true)
            )
        val sampleStage: SampleOperation = Aggregation.sample(1)
        val aggregation: Aggregation = Aggregation.newAggregation(matchStage, sampleStage)
        val records: AggregationResults<Bookmark> =
            mongoTemplate.aggregate(aggregation, Bookmark.COLLECTION, Bookmark::class.java)
        return records.firstOrNull()
    }

    /**
     * @return success?
     */
    fun markRead(id: String, userId: Int, read: Boolean): Bookmark? {
        // we additionally filter by userId to prevent attempts at changing non-own data
        val bookmarkOpt = bookmarkRepository.findByIdAndUserId(id, userId)
        if (bookmarkOpt.isPresent) {
            val bookmark = bookmarkOpt.get()
            bookmark.read = read
            bookmarkRepository.save(bookmark)
            return bookmark
        }
        return null
    }
}