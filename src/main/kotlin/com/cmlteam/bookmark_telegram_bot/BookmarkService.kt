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
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
    private val mongoTemplate: MongoTemplate
) {
  /**
   * Only store non-existed in DB
   * @return previously existed bookmark record for the URL or null
   */
  fun storeBookmark(bookmark: Bookmark): Bookmark? {
    val existing = bookmarkRepository.findByUrlAndUserId(bookmark.url, bookmark.userId)
    if (null == existing) {
      bookmarkRepository.save(bookmark)
      return null
    }
    return existing
  }

  /** @return the number of really stored (the ones not yet existing) */
  fun storeBookmarks(bookmarks: Collection<Bookmark>): Int {
    var res = 0
    for (bookmark in bookmarks) {
      if (storeBookmark(bookmark) == null) res++
    }
    return res
  }

  fun getTotal(userId: Int): Long {
    return bookmarkRepository.countByUserIdAndReadNot(userId, true)
  }

  fun getRandom(userId: Int): Bookmark? {
    val matchStage: MatchOperation =
        Aggregation.match(Criteria.where("userId").isEqualTo(userId).and("read").ne(true))
    val sampleStage: SampleOperation = Aggregation.sample(1)
    val aggregation: Aggregation = Aggregation.newAggregation(matchStage, sampleStage)
    val records: AggregationResults<Bookmark> =
        mongoTemplate.aggregate(aggregation, Bookmark.COLLECTION, Bookmark::class.java)
    return records.firstOrNull()
  }

  /** @return success? */
  fun markRead(id: String, userId: Int, read: Boolean): Bookmark? {
    // we additionally filter by userId to prevent attempts at changing non-own data
    val bookmark = bookmarkRepository.findByIdAndUserId(id, userId)
    if (bookmark != null) {
      bookmark.read = read
      return bookmarkRepository.save(bookmark)
    }
    return null
  }
}
