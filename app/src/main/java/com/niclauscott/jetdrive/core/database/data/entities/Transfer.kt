package com.niclauscott.jetdrive.core.database.data.entities

import java.util.UUID

interface Transfer {
    val transferId: UUID
    val transferName: String
    val transferQueuePosition: Int
}