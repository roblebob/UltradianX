package com.roblebob.ultradianx.repository.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AppState")
class AppState(
    @JvmField @field:ColumnInfo(name = "key") val key: String,
    @JvmField @field:ColumnInfo(name = "value") var value: String) {
    @JvmField @PrimaryKey(autoGenerate = true) var id = 0
}