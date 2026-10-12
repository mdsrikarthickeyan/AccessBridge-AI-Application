package com.example.model

data class ReportStatusHistoryItem(
    val id: String,
    val reportId: String,
    val fromStatus: BarrierStatus?,
    val toStatus: BarrierStatus,
    val changedBy: String,
    val changedRole: String,
    val timestamp: String,
    val reviewNote: String,
    val actionTaken: String
)
