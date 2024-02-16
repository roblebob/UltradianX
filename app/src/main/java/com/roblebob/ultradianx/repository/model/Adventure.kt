package com.roblebob.ultradianx.repository.model

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.work.Data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Duration
import java.time.Instant

@Entity(tableName = "Adventure", indices = [Index(value = ["title"], unique = true)])
class Adventure {
    @JvmField @PrimaryKey(autoGenerate = true)  var id = 0
    @ColumnInfo(name = "active")                var active: Boolean
    @JvmField @ColumnInfo(name = "title")       var title: String
    @JvmField @ColumnInfo(name = "tag")         var tag: String
    @JvmField @ColumnInfo(name = "details")     var details: ArrayList<String>
    @JvmField @ColumnInfo(name = "priority")    var priority: Double
    @JvmField @ColumnInfo(name = "lastTime")    var lastTime: String
    @JvmField @ColumnInfo(name = "lastTimePassive") var lastTimePassive: String
    @JvmField @ColumnInfo(name = "clockify")    var clockify: String
    @JvmField @ColumnInfo(name = "target")      var target : Int  // in minutes

    constructor(active: Boolean, title: String, tag: String, details: ArrayList<String>,
                clockify: String, priority: Double, lastTime: String, lastTimePassive: String,
                target: Int) {
        this.active = active
        this.title = title
        this.tag = tag
        this.details = details
        this.clockify = clockify
        this.priority = priority
        this.lastTime = lastTime
        this.lastTimePassive = lastTimePassive
        this.target = target
    }
    @Ignore constructor(adventure: Adventure) {
        id = adventure.id
        active = adventure.active
        title = adventure.title
        tag = adventure.tag
        details = adventure.details
        clockify = adventure.clockify
        priority = adventure.priority
        lastTime = adventure.lastTime
        lastTimePassive = adventure.lastTimePassive
        target = adventure.target
    }
    @Ignore constructor(data: Data) {
        id = data.getInt("id", -1)
        active = data.getBoolean("active", false)
        title = data.getString("title")!!
        tag = data.getString("tags")!!
        details = Gson().fromJson(
            data.getString("details"),
            object : TypeToken<ArrayList<String?>?>() {}.type
        )
        clockify = data.getString("clockify")!!
        priority = data.getDouble("priority", 0.0)
        lastTime = data.getString("lastTime")!!
        lastTimePassive = data.getString("lastTimePassive")!!
        target = data.getInt("target", -1)
    }


    @get:Ignore
    val grow: Double
        get() = if (target == 0) 0.0 else 100.0 / (24.0 * 60.0 * 60.0) // 1 day

    @get:Ignore
    val decay: Double
        get() = if (target == 0) 0.0 else 100.0 / (target * 60.0) // since we need it in secs



    @Ignore
    fun refresh() {
        val now = Instant.now()
        val duration = Duration.between(Instant.parse(lastTime), now)

        priority = (priority + ( duration.seconds * (if (active) -decay else grow)))
            .coerceAtLeast(0.0).coerceAtMost(100.0)

        this.lastTime = now.toString().also {
            if (!active) {
                lastTimePassive = it
            }
        }
    }






    @Ignore
    fun abort() {
        if (active) {
            refresh()
            active = false
            val duration = Duration.between(
                Instant.parse(lastTime),
                Instant.parse(lastTimePassive)
            )
            priority +=  duration.seconds * decay // reverse that was subtracted while being active
            + duration.seconds * grow // ...and add the passive part, instead

        } else {
            Log.e(this.javaClass.simpleName, "abort() called on inactive adventure")
        }
    }

    @Ignore
    fun update(data: Data) {

        if (data.getInt("id", -1) == id) {

            if (data.hasKeyWithValueOfType("active", Boolean::class.javaObjectType)) {
                active = data.getBoolean("active", false)
            }
            if (data.hasKeyWithValueOfType("title", String::class.javaObjectType)) {
                title = data.getString("title")!!
            }
            if (data.hasKeyWithValueOfType("tags", String::class.javaObjectType)) {
                tag = data.getString("tags")!!
            }
            if (data.hasKeyWithValueOfType("details", String::class.javaObjectType)) {
                details = Gson().fromJson(
                    data.getString("details"),
                    object : TypeToken<ArrayList<String?>?>() {}.type
                )
            }
            if (data.hasKeyWithValueOfType("clockify", String::class.javaObjectType)) {
                clockify = data.getString("clockify")!!
            }
            if (data.hasKeyWithValueOfType("priority", Double::class.javaObjectType)) {
                priority = data.getDouble("priority", 0.0)
                Log.e(this.javaClass.simpleName, "update() called with: priority = $priority")
            }
            if (data.hasKeyWithValueOfType("lastTime", String::class.javaObjectType)) {
                lastTime = data.getString("lastTime")!!
            }
            if (data.hasKeyWithValueOfType("lastTimePassive", String::class.javaObjectType)) {
                lastTimePassive = data.getString("lastTimePassive")!!
            }
            if (data.hasKeyWithValueOfType("target", Int::class.javaObjectType)) {
                target = data.getInt("target", 0)
            }
            refresh()
        }
    }

    override fun toString(): String = "Adventure{ id=$id, active=$active, title=$title, tags=$tag details=$details, priority=$priority, lastTime='$lastTime', lastTimePassive='$lastTimePassive', clockify='$clockify', target=$target }"

    @Ignore fun toData(): Data = Data.Builder()
        .putInt("id", id)
        .putBoolean("active", active)
        .putString("title", title)
        .putString("tags", tag)
        .putString("details", Gson().toJson(details))
        .putString("clockify", clockify)
        .putDouble("priority", priority)
        .putString("lastTime", lastTime)
        .putString("lastTimePassive", lastTimePassive)
        .putInt("target", target)
        .build()

    companion object {
        @Ignore
        fun newAdventure(title: String): Adventure {
            return Adventure(
                false,
                title,
                "",
                ArrayList(),
                "",
                0.0,
                Instant.now().toString(),
                Instant.now().toString(),
                1
            )
        }
    }
}