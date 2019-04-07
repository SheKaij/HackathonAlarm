package hr.olfo.alarmclock.activities

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

        var mediaPlayerSuccess: MediaPlayer? = MediaPlayer.create(this, R.raw.cheer)
        var mediaPlayerFail: MediaPlayer? = MediaPlayer.create(this, R.raw.boo)

        var disabled = false
        var timeLeft = alarm.limit

        val counting = object : Runnable {
            override fun run(){
                if (!disabled && timeLeft > 0){
                    timeLeft -= 1
                    labelInfo.text = String.format("%02d:%02d", timeLeft.div(60), timeLeft.rem(60))
                    handler.postDelayed(this, 1000)
                    apiController.get("state") { response ->
                        disabled = response.toString() == "{}"
                    }
                } else if (disabled){
                    AlarmClock.instance.doWithService {
                        it.stopAlarm()
                    }
                    mediaPlayerSuccess?.start()
                    labelInfo.text = "Congratulations, You did it!"
                    handler.postDelayed({finish()}, 3000)
                } else {
                    AlarmClock.instance.doWithService {
                        it.stopAlarm()
                    }
                    mediaPlayerFail?.start()
                    labelInfo.text = "Thank you for your donation!"
                    handler.postDelayed({finish()}, 3000)
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
}