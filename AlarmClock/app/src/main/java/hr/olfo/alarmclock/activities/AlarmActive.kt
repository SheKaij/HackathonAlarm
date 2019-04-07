package hr.olfo.alarmclock.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import hr.olfo.alarmclock.AlarmClock
import hr.olfo.alarmclock.AlarmService
import hr.olfo.alarmclock.R
import hr.olfo.alarmclock.data.Alarm
import hr.olfo.alarmclock.network.ApiController
import hr.olfo.alarmclock.network.ApiService
import hr.olfo.alarmclock.util.Constants
import kotlinx.android.synthetic.main.activity_alarm_active.*

class AlarmActive: AppCompatActivity(), AlarmService.SnoozeListener {

    lateinit var alarm: Alarm
    val handler = Handler()
    val apiService = ApiService()
    val apiController = ApiController(apiService)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_active)

        AlarmClock.instance.doWithService {
            it.snoozeListener = this
        }

        val preferences: SharedPreferences = applicationContext.getSharedPreferences(Constants.PreferencesAlarms, Context.MODE_PRIVATE)

        val args: Bundle? = intent.extras
        val id = args?.getString(Constants.AlarmID, "") ?: ""
        if (id.isBlank()) {
            finish()
        } else {
            alarm = AlarmClock.gson.fromJson<Alarm>(preferences.getString(id, ""), Alarm::class.java)
        }

        labelAlarmName.text = alarm.name

        var disabled = false
        var timeLeft = alarm.limit
        labelInfo.text = timeLeft.toString()

        val counting = object : Runnable {
            override fun run(){
                if (!disabled && timeLeft > 0){
                    timeLeft -= 1
                    labelInfo.text = (timeLeft).toString()
                    handler.postDelayed(this, 1000)
                    apiController.get("state") { response ->
                        disabled = response == null
                    }
                } else if (disabled){
                    labelInfo.text = "Congratulations, You did it!"
                    handler.postDelayed({stopAlarm()}, 2000)
                } else {
                    labelInfo.text = "Thank you for your donation!"
                    handler.postDelayed({stopAlarm()}, 2000)
                }
            }
        }
        handler.postDelayed(counting, 1000)

        /*if (alarm.snoozeTime == 0) buttonSnooze.visibility = View.GONE
        buttonSnooze.setOnClickListener {
            onSnooze()
            AlarmClock.instance.doWithService {
                it.snoozeAlarm()
            }
        }

        buttonOff.setOnClickListener {
            AlarmClock.instance.doWithService {
                it.stopAlarm()
            }
            finish()
        }*/
    }

    /*override fun onBackPressed() {
        onSnooze()
        AlarmClock.instance.doWithService {
            it.snoozeAlarm()
        }
    }*/

    override fun onSnooze() {
        /*if (buttonSnooze.isEnabled) {
            buttonSnooze.isEnabled = false
            handler.postDelayed({
                buttonSnooze.isEnabled = true
            }, alarm.snoozeTime * 60000L)
        }*/
    }

    fun stopAlarm(){
        AlarmClock.instance.doWithService {
            it.stopAlarm()
        }
        finish()
    }
}