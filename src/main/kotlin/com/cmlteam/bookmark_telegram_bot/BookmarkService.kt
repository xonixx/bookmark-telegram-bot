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
    fun save(bookmark: Bookmark) {
        bookmarkRepository.save(bookmark)
    }

    fun getTotal(userId: Int): Long {
        return bookmarkRepository.countByUserId(userId)
    }

    fun getRandom(userId: Int): Bookmark? {
        val matchStage: MatchOperation = Aggregation.match(Criteria.where("userId").isEqualTo(userId))
        val sampleStage: SampleOperation = Aggregation.sample(1)
        val aggregation: Aggregation = Aggregation.newAggregation(matchStage, sampleStage)
        val records: AggregationResults<Bookmark> =
            mongoTemplate.aggregate(aggregation, Bookmark.COLLECTION, Bookmark::class.java)
        return records.firstOrNull()
    }
}