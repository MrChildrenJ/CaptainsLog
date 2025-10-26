package com.example.captainslog.data.models

import java.util.Date

data class FriendRequest(
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val status: RequestStatus,
    val createdAt: Date
)

enum class RequestStatus {
    PENDING, ACCEPTED, REJECTED
}
