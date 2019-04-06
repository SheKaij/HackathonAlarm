package hr.olfo.alarmclock.data

import java.util.ArrayList

class AlarmDto {
    var timeH: Int = 0
    var timeM: Int = 0
    var limit: Int = 0
    var amount: Int = 0
    var devices: ArrayList<String> = ArrayList()
}