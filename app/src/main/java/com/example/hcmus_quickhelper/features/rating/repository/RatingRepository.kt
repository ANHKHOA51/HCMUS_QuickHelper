package com.example.hcmus_quickhelper.features.rating.repository

import com.example.hcmus_quickhelper.features.rating.datasource.RatingDataSource
import com.example.hcmus_quickhelper.features.rating.model.Rating

class RatingRepository (
    private val dataSource: RatingDataSource
) {
    suspend fun insertRating(rating: Rating): Rating {
        return dataSource.insert(rating)
    }

    suspend fun getRatingByHelperId(helperId: Int): List<Rating> {
        return dataSource.getByHelperId(helperId)
    }

    suspend fun updateRatingByHelperId(rating: Int, helperId: Int) {
        dataSource.updateRating(rating.toDouble(), helperId)
    }
}