package com.example.noteapp.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.Serializable

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val noteTitle : String,
    val noteBody : String,
    val priority: Priority?= Priority.MEDIUM,
    val isChecked:Boolean?=false

): Serializable
enum class Priority{
    HIGH,
    MEDIUM,
    LOW
}
class PriorityConverter {

    @TypeConverter
    fun fromPriority(priority: Priority): Int {
        return priority.ordinal + 1
    }

    @TypeConverter
    fun toPriority(priorityInt: Int): Priority {
        return when (priorityInt) {
            1 -> Priority.HIGH
            2 -> Priority.MEDIUM
            3 -> Priority.LOW
            else -> throw IllegalArgumentException("Invalid priority value: $priorityInt")
        }
    }
}