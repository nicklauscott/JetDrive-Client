package com.niclauscott.jetdrive.core.database.converter

import androidx.room.TypeConverter
import com.niclauscott.jetdrive.core.database.entities.TransferStatus

class Converters {

    @TypeConverter
    fun fromTransferStatus(status: TransferStatus): String = status.name

    @TypeConverter
    fun toTransferStatus(status: String): TransferStatus = TransferStatus.valueOf(status)

    @TypeConverter
    fun fromIntList(value: List<Int>): String = value.joinToString(",")

    @TypeConverter
    fun toIntList(value: String): List<Int> =
        if (value.isBlank()) emptyList()
        else value.split(",").map { it.toInt() }

}
