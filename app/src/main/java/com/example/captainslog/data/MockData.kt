package com.example.captainslog.data

import com.example.captainslog.data.models.LogEntry
import com.example.captainslog.data.models.User
import com.example.captainslog.data.models.FriendRequest
import com.example.captainslog.data.models.RequestStatus
import java.util.Date
import java.util.Calendar

object MockData {
    val currentUser = User(
        id = "user_1",
        username = "captain_picard",
        displayName = "Jean-Luc Picard",
        profilePictureUrl = null
    )

    val friends = listOf(
        User(
            id = "user_2",
            username = "cmdr_riker",
            displayName = "William Riker",
            profilePictureUrl = null
        ),
        User(
            id = "user_3",
            username = "lt_data",
            displayName = "Data",
            profilePictureUrl = null
        ),
        User(
            id = "user_4",
            username = "dr_crusher",
            displayName = "Beverly Crusher",
            profilePictureUrl = null
        )
    )

    val logEntries = listOf(
        LogEntry(
            id = "log_1",
            userId = currentUser.id,
            title = "First Contact with the Borg",
            audioFileUrl = "mock://audio/log_1.mp3",
            transcription = "Captain's log, stardate 42761.3. We have encountered a mysterious vessel of unknown origin. The ship appears to be a massive cube, unlike anything in our database. I've ordered yellow alert and am proceeding with caution.",
            createdAt = getDateDaysAgo(5),
            duration = 45, // seconds
            isShared = true,
            sharedWith = listOf("user_2", "user_3")
        ),
        LogEntry(
            id = "log_2",
            userId = currentUser.id,
            title = "Diplomatic Mission to Romulus",
            audioFileUrl = "mock://audio/log_2.mp3",
            transcription = "Captain's log, stardate 43125.8. We're en route to the Neutral Zone for a diplomatic mission with the Romulan Empire. Ambassador Spock will be joining us. I must admit, this is a delicate situation.",
            createdAt = getDateDaysAgo(3),
            duration = 62,
            isShared = false,
            sharedWith = emptyList()
        ),
        LogEntry(
            id = "log_3",
            userId = currentUser.id,
            title = "Anomaly in the Gamma Quadrant",
            audioFileUrl = "mock://audio/log_3.mp3",
            transcription = "Captain's log, stardate 43349.2. We've detected an unusual subspace anomaly in the Gamma Quadrant. Lieutenant Commander Data is running a full analysis. Initial readings suggest this could be a stable wormhole.",
            createdAt = getDateDaysAgo(1),
            duration = 38,
            isShared = false,
            sharedWith = emptyList()
        ),
        LogEntry(
            id = "log_4",
            userId = currentUser.id,
            title = "Shore Leave on Risa",
            audioFileUrl = "mock://audio/log_4.mp3",
            transcription = "Captain's log, stardate 43745.9. The crew has earned some well-deserved shore leave on Risa. I've decided to join them this time. Commander Riker assures me the planet's hospitality is unmatched.",
            createdAt = getDateDaysAgo(0),
            duration = 28,
            isShared = true,
            sharedWith = listOf("user_2")
        ),
        LogEntry(
            id = "log_5",
            userId = currentUser.id,
            title = "Spock and Bones Debate",
            audioFileUrl = "mock://audio/log_5.mp3",
            transcription = "Personal log. I witnessed an interesting philosophical debate between Ambassador Spock and Dr. McCoy today about the nature of humanity. Spock argued for pure logic while Bones passionately defended emotion and intuition. Both made compelling points.",
            createdAt = getDateDaysAgo(7),
            duration = 95,
            isShared = false,
            sharedWith = emptyList()
        )
    )

    val friendRequests = listOf(
        FriendRequest(
            id = "req_1",
            fromUserId = "user_5",
            toUserId = currentUser.id,
            status = RequestStatus.PENDING,
            createdAt = getDateDaysAgo(2)
        )
    )

    private fun getDateDaysAgo(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return calendar.time
    }
}
