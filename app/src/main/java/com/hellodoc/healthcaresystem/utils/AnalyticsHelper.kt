package com.hellodoc.healthcaresystem.utils

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

/**
 * Helper class for tracking user behavior with Firebase Analytics
 */
object AnalyticsHelper {
    private val firebaseAnalytics = Firebase.analytics
    private const val TAG = "AnalyticsHelper"

    // Screen view tracking
    fun logScreenView(screenName: String, screenClass: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
        Log.d(TAG, "Screen View: $screenName")
    }

    // Service selection tracking
    fun logServiceSelected(serviceId: String, serviceName: String) {
        firebaseAnalytics.logEvent("service_selected") {
            param(FirebaseAnalytics.Param.ITEM_ID, serviceId)
            param(FirebaseAnalytics.Param.ITEM_NAME, serviceName)
        }
        Log.d(TAG, "Service Selected: $serviceName")
    }

    // Specialty selection tracking
    fun logSpecialtySelected(specialtyId: String, specialtyName: String) {
        firebaseAnalytics.logEvent("specialty_selected") {
            param(FirebaseAnalytics.Param.ITEM_ID, specialtyId)
            param(FirebaseAnalytics.Param.ITEM_NAME, specialtyName)
        }
        Log.d(TAG, "Specialty Selected: $specialtyName")
    }

    // Doctor selection tracking
    fun logDoctorSelected(doctorId: String, doctorName: String) {
        firebaseAnalytics.logEvent("doctor_selected") {
            param(FirebaseAnalytics.Param.ITEM_ID, doctorId)
            param(FirebaseAnalytics.Param.ITEM_NAME, doctorName)
        }
        Log.d(TAG, "Doctor Selected: $doctorName")
    }

    // News view tracking
    fun logNewsViewed(newsId: String, newsTitle: String) {
        firebaseAnalytics.logEvent("news_viewed") {
            param(FirebaseAnalytics.Param.ITEM_ID, newsId)
            param(FirebaseAnalytics.Param.ITEM_NAME, newsTitle)
        }
        Log.d(TAG, "News Viewed: $newsTitle")
    }

    // AI assistant usage tracking
    fun logAIAssistantQuery(query: String) {
        firebaseAnalytics.logEvent("ai_assistant_query") {
            param("query", query)
        }
        Log.d(TAG, "AI Assistant Query: $query")
    }

    // Post interaction tracking
    fun logPostInteraction(postId: String, interactionType: String) {
        firebaseAnalytics.logEvent("post_interaction") {
            param(FirebaseAnalytics.Param.ITEM_ID, postId)
            param("interaction_type", interactionType) // like, comment, report, etc.
        }
        Log.d(TAG, "Post Interaction: $interactionType on post $postId")
    }

    // Report event tracking
    fun logReportSubmission(reportType: String) {
        firebaseAnalytics.logEvent("report_submitted") {
            param("report_type", reportType)
        }
        Log.d(TAG, "Report Submitted: $reportType")
    }

    // Scroll depth tracking
    fun logScrollDepth(screenName: String, depth: Int) {
        firebaseAnalytics.logEvent("scroll_depth") {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param("depth", depth.toString())
        }
        Log.d(TAG, "Scroll Depth: $depth on $screenName")
    }

    // Function to set user properties
    fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
        Log.d(TAG, "User Property: $name = $value")
    }

    // Custom event logging
    fun logCustomEvent(eventName: String, params: Bundle) {
        firebaseAnalytics.logEvent(eventName, params)
        Log.d(TAG, "Custom Event: $eventName")
    }
}