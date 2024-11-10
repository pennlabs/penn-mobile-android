package com.pennapps.labs.pennmobile.coursealert.classes

import com.google.gson.annotations.SerializedName

/**
 * Created by Ali Krema, Fall 2022.
 */

data class PennCourseAlertRegistration(
    @SerializedName("cancelled_at")
    val cancelledAt: String = "",
    @SerializedName("notification_sent")
    val notificationSent: Boolean = false,
    @SerializedName("is_active")
    val isActive: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("close_notification_sent_at")
    val closeNotificationSentAt: String = "",
    @SerializedName("section")
    val section: String = "",
    @SerializedName("close_notification")
    val closeNotification: Boolean = false,
    @SerializedName("deleted_at")
    val deletedAt: String = "",
    @SerializedName("close_notification_sent")
    val closeNotificationSent: Boolean = false,
    @SerializedName("auto_resubscribe")
    val autoResubscribe: Boolean = false,
    @SerializedName("original_created_at")
    val originalCreatedAt: String = "",
    @SerializedName("deleted")
    val deleted: Boolean = false,
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("last_notification_sent_at")
    val lastNotificationSentAt: String = "",
    @SerializedName("is_waiting_for_close")
    val isWaitingForClose: String = "",
    @SerializedName("notification_sent_at")
    val notificationSentAt: String = "",
    @SerializedName("cancelled")
    val cancelled: Boolean = false,
    @SerializedName("id")
    val id: Int,
    @SerializedName("user")
    val user: String = "",
    @SerializedName("section_status")
    val sectionStatus: String = "",
)
