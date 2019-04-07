package hr.olfo.alarmclock.activities

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.*
import hr.olfo.alarmclock.AlarmClock
import hr.olfo.alarmclock.R
import hr.olfo.alarmclock.data.Alarm
import hr.olfo.alarmclock.data.AlarmDto
import hr.olfo.alarmclock.data.Device
//import hr.olfo.alarmclock.dialogs.DialogRepeat
import hr.olfo.alarmclock.dialogs.DialogRingtone
import hr.olfo.alarmclock.fragments.FragmentTimePicker
import hr.olfo.alarmclock.network.ApiController
//import hr.olfo.alarmclock.network.ApiGateway
import hr.olfo.alarmclock.network.ApiService
import hr.olfo.alarmclock.util.Constants

import kotlinx.android.synthetic.main.activity_alarm_create.*
import java.util.*
import hr.olfo.alarmclock.util.Util
import org.json.JSONObject
import java.lang.Exception
import kotlin.collections.ArrayList

class AlarmCreate : AppCompatActivity() {

    lateinit var alarm: Alarm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_create)

        val preferences: SharedPreferences = applicationContext.getSharedPreferences(Constants.PreferencesAlarms, Context.MODE_PRIVATE)

        val args: Bundle? = intent.extras
        val id = args?.getString(Constants.AlarmID, "") ?: ""

        val apiService = ApiService()
        val apiController = ApiController(apiService)

        if (id.isNotBlank()) {
            alarm = AlarmClock.gson.fromJson<Alarm>(preferences.getString(id, ""), Alarm::class.java)

            textName.text.clear()
            textName.text.insert(0, alarm.name)

            labelRingtone.text = alarm.ringtoneName

            /*seekBarVolume.progress = alarm.volume

            if (alarm.repeat[Day.Monday] == true &&
                    alarm.repeat[Day.Tuesday] == true &&
                    alarm.repeat[Day.Wednesday] == true &&
                    alarm.repeat[Day.Thursday] == true &&
                    alarm.repeat[Day.Friday] == true &&
                    alarm.repeat[Day.Saturday] == true &&
                    alarm.repeat[Day.Sunday] == true) {
                labelRepeat.text = "Every day"
            } else if (alarm.repeat[Day.Monday] == true &&
                    alarm.repeat[Day.Tuesday] == true &&
                    alarm.repeat[Day.Wednesday] == true &&
                    alarm.repeat[Day.Thursday] == true &&
                    alarm.repeat[Day.Friday] == true &&
                    alarm.repeat[Day.Saturday] == false &&
                    alarm.repeat[Day.Sunday] == false) {
                labelRepeat.text = "Weekdays"
            } else if (alarm.repeat[Day.Monday] == false &&
                    alarm.repeat[Day.Tuesday] == false &&
                    alarm.repeat[Day.Wednesday] == false &&
                    alarm.repeat[Day.Thursday] == false &&
                    alarm.repeat[Day.Friday] == false &&
                    alarm.repeat[Day.Saturday] == true &&
                    alarm.repeat[Day.Sunday] == true) {
                labelRepeat.text = "Weekends"
            } else {
                val list = mutableListOf<String>()
                if (alarm.repeat[Day.Monday] == true) list += "Mon"
                if (alarm.repeat[Day.Tuesday] == true) list += "Tue"
                if (alarm.repeat[Day.Wednesday] == true) list += "Wed"
                if (alarm.repeat[Day.Thursday] == true) list += "Thu"
                if (alarm.repeat[Day.Friday] == true) list += "Fri"
                if (alarm.repeat[Day.Saturday] == true) list += "Sat"
                if (alarm.repeat[Day.Sunday] == true) list += "Sun"
                labelRepeat.text = list.joinToString()
            }

            when (alarm.snoozeTime) {
                0 -> {
                    labelSnooze.text = "Off"
                    seekBarSnooze.progress = 0
                }
                1 -> {
                    labelSnooze.text = "1 min"
                    seekBarSnooze.progress = 1
                }
                5 -> {
                    labelSnooze.text = "5 min"
                    seekBarSnooze.progress = 2
                }
                10 -> {
                    labelSnooze.text = "10 min"
                    seekBarSnooze.progress = 3
                }
                15 -> {
                    labelSnooze.text = "15 min"
                    seekBarSnooze.progress = 4
                }
                20 -> {
                    labelSnooze.text = "20 min"
                    seekBarSnooze.progress = 5
                }
                25 -> {
                    labelSnooze.text = "25 min"
                    seekBarSnooze.progress = 6
                }
                30 -> {
                    labelSnooze.text = "30 min"
                    seekBarSnooze.progress = 7
                }
                60 -> {
                    labelSnooze.text = "1 h"
                    seekBarSnooze.progress = 8
                }
                120 -> {
                    labelSnooze.text = "2 h"
                    seekBarSnooze.progress = 9
                }
                180 -> {
                    labelSnooze.text = "3 h"
                    seekBarSnooze.progress = 10
                }

            }

            checkBoxSnoozeOnMove.isChecked = alarm.snoozeOnMove*/

            when (alarm.limit) {
                5 -> {
                    labelLimit.text = "5 sec"
                    seekBarLimit.progress = 0
                }
                10 -> {
                    labelLimit.text = "10 sec"
                    seekBarLimit.progress = 1
                }
                20 -> {
                    labelLimit.text = "20 sec"
                    seekBarLimit.progress = 2
                }
                30 -> {
                    labelLimit.text = "30 sec"
                    seekBarLimit.progress = 3
                }
                60 -> {
                    labelLimit.text = "1 min "
                    seekBarLimit.progress = 4
                }
                120 -> {
                    labelLimit.text = "2 min"
                    seekBarLimit.progress = 5
                }
                180 -> {
                    labelLimit.text = "3 min"
                    seekBarLimit.progress = 6
                }
                240 -> {
                    labelLimit.text = "4 min"
                    seekBarLimit.progress = 7
                }
                300 -> {
                    labelLimit.text = "5 min"
                    seekBarLimit.progress = 8
                }
                420 -> {
                    labelLimit.text = "7 min"
                    seekBarLimit.progress = 9
                }
                600 -> {
                    labelLimit.text = "10 min"
                    seekBarLimit.progress = 10
                }
            }
        } else {
            alarm = Alarm()
            alarm.ringtoneUri = Util.ringtones.keys.firstOrNull()?.toString() ?: ""
            labelRingtone.text = Util.ringtones.values.firstOrNull()?.toString() ?: "NONE"
            alarm.name = textName.text.toString()
            val c = Calendar.getInstance()
            alarm.timeH = c.get(Calendar.HOUR_OF_DAY)
            alarm.timeM = c.get(Calendar.MINUTE)
            alarm.limit = 60
            alarm.amount = 0
        }
        labelTime.text = Util.getDisplayTime(this, alarm.timeH, alarm.timeM)

        val devices = ArrayList<Device>()

        apiController.getArray("devices"){ response ->
            if (response != null){
                for (i in 0 until response.length()){
                    devices.add(AlarmClock.gson.fromJson(response.getJSONObject(i).toString(), Device::class.java))
                    val checkBox = CheckBox(this)
                    checkBox.setOnClickListener{
                        if (checkBox.isChecked)
                            alarm.devices.add(devices[i].uid)
                        else
                            alarm.devices.remove(devices[i].uid)
                    }
                    checkBox.text = devices[i].name
                    devicesList.addView(checkBox)
                }
            }
        }

        textName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                alarm.name = s?.toString() ?: "Alarm"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

/*        buttonRepeat.setOnClickListener {
            buttonRepeat.isEnabled = false
            val repeat = DialogRepeat()
            val bundle = Bundle()
            bundle.putBoolean(Constants.ARGUMENT_MON, alarm.repeat[Day.Monday] ?: false)
            bundle.putBoolean(Constants.ARGUMENT_TUE, alarm.repeat[Day.Tuesday] ?: false)
            bundle.putBoolean(Constants.ARGUMENT_WED, alarm.repeat[Day.Wednesday] ?: false)
            bundle.putBoolean(Constants.ARGUMENT_THU, alarm.repeat[Day.Thursday] ?: false)
            bundle.putBoolean(Constants.ARGUMENT_FRI, alarm.repeat[Day.Friday] ?: false)
            bundle.putBoolean(Constants.ARGUMENT_SAT, alarm.repeat[Day.Saturday] ?: false)
            bundle.putBoolean(Constants.ARGUMENT_SUN, alarm.repeat[Day.Sunday] ?: false)
            repeat.arguments = bundle

            val fm = fragmentManager
            repeat.show(fm!!, "repeatDialog")
        }*/

        buttonRingtone.setOnClickListener {
            buttonRingtone.isEnabled = false

            val ringtone = DialogRingtone()
            val bundle = Bundle()
            bundle.putString(Constants.ArgumentRingtone, alarm.ringtoneUri)
            ringtone.arguments = bundle

            val fm = fragmentManager
            ringtone.show(fm!!, "ringtoneDialog")
        }

        /*seekBarVolume.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                alarm.volume = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })*/

        /*seekBarSnooze.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when (progress) {
                    0 -> {
                        labelSnooze.text = "Off"
                        alarm.snoozeTime = 0
                    }
                    1 -> {
                        labelSnooze.text = "1 min"
                        alarm.snoozeTime = 1
                    }
                    2 -> {
                        labelSnooze.text = "5 min"
                        alarm.snoozeTime = 5
                    }
                    3 -> {
                        labelSnooze.text = "10 min"
                        alarm.snoozeTime = 10
                    }
                    4 -> {
                        labelSnooze.text = "15 min"
                        alarm.snoozeTime = 15
                    }
                    5 -> {
                        labelSnooze.text = "20 min"
                        alarm.snoozeTime = 20
                    }
                    6 -> {
                        labelSnooze.text = "25 min"
                        alarm.snoozeTime = 25
                    }
                    7 -> {
                        labelSnooze.text = "30 min"
                        alarm.snoozeTime = 30
                    }
                    8 -> {
                        labelSnooze.text = "1 h"
                        alarm.snoozeTime = 60
                    }
                    9 -> {
                        labelSnooze.text = "2 h"
                        alarm.snoozeTime = 120
                    }
                    10 -> {
                        labelSnooze.text = "3 h"
                        alarm.snoozeTime = 180
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        checkBoxSnoozeOnMove.setOnClickListener {
            alarm.snoozeOnMove = checkBoxSnoozeOnMove.isChecked
        }*/



        seekBarLimit.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when (progress) {
                    0 -> {
                        labelLimit.text = "5 sec"
                        alarm.limit = 5
                    }
                    1 -> {
                        labelLimit.text = "10 sec"
                        alarm.limit = 10
                    }
                    2 -> {
                        labelLimit.text = "20 sec"
                        alarm.limit = 20
                    }
                    3 -> {
                        labelLimit.text = "30 sec"
                        alarm.limit = 30
                    }
                    4 -> {
                        labelLimit.text = "1 min"
                        alarm.limit = 60
                    }
                    5 -> {
                        labelLimit.text = "2 min"
                        alarm.limit = 120
                    }
                    6 -> {
                        labelLimit.text = "3 min"
                        alarm.limit = 180
                    }
                    7 -> {
                        labelLimit.text = "4 min"
                        alarm.limit = 240
                    }
                        8 -> {
                        labelLimit.text = "5 min"
                        alarm.limit = 300
                    }
                    9 -> {
                        labelLimit.text = "7 min"
                        alarm.limit = 420
                    }
                    10 -> {
                        labelLimit.text = "10 min"
                        alarm.limit = 600
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        moneyAmount.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    alarm.amount = Integer.parseInt(s?.toString())
                } catch (e: Exception) {
                    alarm.amount = 0
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        buttonSave.setOnClickListener {
            val alarmData = AlarmClock.gson.toJson(alarm)
            val set = preferences.getStringSet(Constants.AlarmList, mutableSetOf())
            preferences.edit().also {
                it.putString(alarm.id, alarmData)
                set.add(alarm.id)
                it.putStringSet(Constants.AlarmList, set)
            }.apply()

            AlarmClock.instance.doWithService {
                it.refreshAlarms()
            }

            val alarmDto = AlarmDto()
            alarmDto.timeH = alarm.timeH
            alarmDto.timeM = alarm.timeM
            alarmDto.limit = alarm.limit
            alarmDto.amount = alarm.amount
            alarmDto.devices = alarm.devices
            val alarmJson = JSONObject(AlarmClock.gson.toJson(alarmDto))
            apiController.post("alarms", alarmJson){ response ->
                if( response != null )
                    finish()
                else
                    text.visibility = View.VISIBLE
                    text.text = "NETWORK ERROR"
            }
        }
    }

    fun showTimePicker(view: View) {
        val frag = FragmentTimePicker()
        val bundle = Bundle()
        bundle.putInt(Constants.ARGUMENT_HOUR, alarm.timeH)
        bundle.putInt(Constants.ARGUMENT_MINUTE, alarm.timeM)
        frag.arguments = bundle
        frag.listener = TimePickerDialog.OnTimeSetListener { _, h, m ->
            alarm.timeH = h
            alarm.timeM = m
            labelTime.text = Util.getDisplayTime(this, h, m)
        }
        frag.show(supportFragmentManager, "timePicker")
    }

/*//    fun setRepeat(mon: Boolean, tue: Boolean, wed: Boolean, thu: Boolean, fri: Boolean, sat: Boolean, sun: Boolean) {
//        if (mon && tue && wed && thu && fri && sat && sun) {
//            labelRepeat.text = "Every day"
//        } else if (mon && tue && wed && thu && fri && !sat && !sun) {
//            labelRepeat.text = "Weekdays"
//        } else if (!mon && !tue && !wed && !thu && !fri && sat && sun) {
//            labelRepeat.text = "Weekends"
//        } else {
//            val list = mutableListOf<String>()
//            if (mon) list += "Mon"
//            if (tue) list += "Tue"
//            if (wed) list += "Wed"
//            if (thu) list += "Thu"
//            if (fri) list += "Fri"
//            if (sat) list += "Sat"
//            if (sun) list += "Sun"
//            labelRepeat.text = list.joinToString()
//        }
//
//        alarm.repeat[Day.Monday] = mon
//        alarm.repeat[Day.Tuesday] = tue
//        alarm.repeat[Day.Wednesday] = wed
//        alarm.repeat[Day.Thursday] = thu
//        alarm.repeat[Day.Friday] = fri
//        alarm.repeat[Day.Saturday] = sat
//        alarm.repeat[Day.Sunday] = sun
//    }*/

    fun setRingtone(uri: Uri, name: String) {
        labelRingtone.text = name

        alarm.ringtoneUri = uri.toString()
        alarm.ringtoneName = name
    }

    fun dialogClosed(id: String) {
        when (id) {
//            Constants.DialogIDRepeat -> buttonRepeat.isEnabled = true
            Constants.DialogIDRingtone -> buttonRingtone.isEnabled = true
        }
    }
}
