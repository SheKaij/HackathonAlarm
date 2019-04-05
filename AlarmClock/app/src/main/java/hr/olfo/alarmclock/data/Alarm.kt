package hr.olfo.alarmclock.data

import hr.olfo.alarmclock.AlarmClock
import hr.olfo.alarmclock.util.Day
import hr.olfo.alarmclock.util.Util
import java.util.*
import kotlin.collections.HashMap

class Alarm {
    val id = UUID.randomUUID().toString()

    var name: String = ""
    var enabled: Boolean = true
    var lastTime: String = ""

    // TODO: Replace with TimeRange
    var timeH: Int = 0
    var timeM: Int = 0

    var repeat = mutableMapOf<Day, Boolean>()

    var volume: Int = 100

    var ringtoneUri: String = ""
    var ringtoneName: String = "Default"

    var snoozeTime = 0
    var snoozeOnMove = false

    fun activeOnDay(day: Day) = repeat[day]

    fun activeOnDay(day: Int): Boolean {
        return when (day) {
            1-> repeat[Day.Sunday] ?: false
            2-> repeat[Day.Monday] ?: false
            3-> repeat[Day.Tuesday] ?: false
            4-> repeat[Day.Wednesday] ?: false
            5-> repeat[Day.Thursday] ?: false
            6-> repeat[Day.Friday] ?: false
            7-> repeat[Day.Saturday] ?: false
            else -> false
        }
    }

    fun clone(): Alarm {
        val result = Alarm()
        result.name = name
        result.enabled = enabled

        result.timeH = timeH
        result.timeM = timeM

        result.repeat = HashMap(repeat)
        result.volume = volume

        result.ringtoneUri = ringtoneUri
        result.ringtoneName = ringtoneName

        result.snoozeTime = snoozeTime
        result.snoozeOnMove = snoozeOnMove
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Alarm) return false
        return id == other.id
    }
}