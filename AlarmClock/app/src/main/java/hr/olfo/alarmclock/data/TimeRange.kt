package hr.olfo.alarmclock.data

import hr.olfo.alarmclock.util.Day
import java.util.*

class TimeRange {
    var beginDate: Date? = null
    var endDate: Date? = null

    val shifts = mutableListOf<Shift>()

    class Shift {
        val activeDays = mutableMapOf<Day, Time?>().apply {
            for (day in Day.values()) {
                this[day] = null
            }
        }
    }

    data class Time(val hours: Int, val minutes: Int = 0, val seconds: Int = 0)
}