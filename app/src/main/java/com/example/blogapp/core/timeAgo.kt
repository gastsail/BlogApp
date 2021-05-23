package com.example.blogapp.core

import java.util.*

internal object timeAgo {
    fun getTimeAgo(time_ago: Long): String {
        var time_ago = time_ago
        time_ago = time_ago / 1000
        val cur_time = Calendar.getInstance().timeInMillis / 1000
        val time_elapsed = cur_time - time_ago
        // Seconds
        return if (time_elapsed <= 60) {
            "Just now"
        } else {
            val minutes = Math.round((time_elapsed / 60).toFloat())
            if (minutes <= 60) {
                if (minutes == 1) {
                    "a minute ago"
                } else {
                    "$minutes minutes ago"
                }
            } else {
                val hours = Math.round((time_elapsed / 3600).toFloat())
                if (hours <= 24) {
                    if (hours == 1) {
                        "An hour ago"
                    } else {
                        "$hours hrs ago"
                    }
                } else {
                    val days = Math.round((time_elapsed / 86400).toFloat())
                    if (days <= 7) {
                        if (days == 1) {
                            "Yesterday"
                        } else {
                            "$days days ago"
                        }
                    } else {
                        val weeks = Math.round((time_elapsed / 604800).toFloat())
                        if (weeks <= 4.3) {
                            if (weeks == 1) {
                                "A week ago"
                            } else {
                                "$weeks weeks ago"
                            }
                        } else {
                            val months = Math.round((time_elapsed / 2600640).toFloat())
                            if (months <= 12) {
                                if (months == 1) {
                                    "A month ago"
                                } else {
                                    "$months months ago"
                                }
                            } else {
                                val years = Math.round((time_elapsed / 31207680).toFloat())
                                if (years == 1) {
                                    "One year ago"
                                } else {
                                    "$years years ago"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}