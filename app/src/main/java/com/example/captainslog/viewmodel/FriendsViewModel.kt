package com.example.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import com.example.captainslog.data.MockData
import com.example.captainslog.data.models.User
import com.example.captainslog.data.models.FriendRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FriendsViewModel : ViewModel() {
    private val _friends = MutableStateFlow(MockData.friends)
    val friends: StateFlow<List<User>> = _friends.asStateFlow()

    private val _friendRequests = MutableStateFlow(MockData.friendRequests)
    val friendRequests: StateFlow<List<FriendRequest>> = _friendRequests.asStateFlow()

    fun acceptRequest(requestId: String) {
        // In real app, this would update the repository
        _friendRequests.value = _friendRequests.value.filter { it.id != requestId }
    }

    fun rejectRequest(requestId: String) {
        _friendRequests.value = _friendRequests.value.filter { it.id != requestId }
    }
}
