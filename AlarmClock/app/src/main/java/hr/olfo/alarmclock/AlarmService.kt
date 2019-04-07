package hr.olfo.alarmclock

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import hr.olfo.alarmclock.activities.AlarmActive
import hr.olfo.alarmclock.data.Alarm
import hr.olfo.alarmclock.util.Constants
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AlarmService : Service(), SensorEventListener {

    private lateinit var preferences: SharedPreferences

    val timer = Timer("AlarmClock")
    val alarms = mutableListOf<Alarm>()

    var current: Alarm? = null
    var mp: MediaPlayer? = null
    val handler = Handler()
    var sm: SensorManager? = null

    override fun onBind(intent: Intent): IBinder {
        preferences = applicationContext.getSharedPreferences(Constants.PreferencesAlarms, Context.MODE_PRIVATE)
        when (intent.action) {
            Constants.ActionInit -> {
                val alarmList = preferences.getStringSet(Constants.AlarmList, emptySet())
                alarms += alarmList.mapNotNull { gson.fromJson(preferences.getString(it, ""), Alarm::class.java) }

                var c = Calendar.getInstance()
                val delay = 60 - c.get(Calendar.SECOND)

                timer.scheduleAtFixedRate(object: TimerTask() {
                    override fun run() {
                        c = Calendar.getInstance()
                        val h = c.get(Calendar.HOUR_OF_DAY)
                        val m = c.get(Calendar.MINUTE)
                        val d = c.get(Calendar.DAY_OF_WEEK)

                        alarms.filter { it.enabled }.forEach {
                            if (it.timeH == h && it.timeM == m && (it.activeOnDay(d) || !it.repeat.values.contains(true))) {
                                val timestamp = SimpleDateFormat("mm-hh-dd-MM-yyyy", Locale.US)
                                Log.i("WHAT THE FUCK",it.lastTime + "?=" + timestamp.format(Date()))
                                if (it.lastTime != timestamp.format(Date())) {
                                    current = it
                                    startAlarm(it.uid)
                                }

                            }
                        }
                    }
                }, delay * 1000L, 1000)
            }
        }

        return AlarmBinder(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        sm?.unregisterListener(this)
    }

    fun startAlarm(id: String) {
        mp = MediaPlayer().also {
            it.isLooping = true
        }
        val alarm = gson.fromJson(preferences.getString(id, ""), Alarm::class.java)
        val volume = alarm.volume.toFloat() / 100

        try {
            mp?.setDataSource(this@AlarmService, Uri.parse(alarm.ringtoneUri))
            mp?.prepare()
            mp?.setVolume(volume, volume)
            mp?.start()
        } catch (e: IOException) {
            mp = null
        }

        if (current?.snoozeOnMove == true) {
            sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val accelerometer = sm?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

            sm?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
        }

        val timestamp = SimpleDateFormat("mm-hh-dd-MM-yyyy", Locale.US)
        alarm.lastTime = timestamp.format(Date())

        if (!alarm.repeat.values.contains(true)) {
            alarm.enabled = false
        }
        val alarmData = AlarmClock.gson.toJson(alarm)
        preferences.edit().also {
            it.putString(id, alarmData)
        }.apply()
        alarms.removeAll { it.uid == id }
        alarms += alarm

        val intent = Intent(this, AlarmActive::class.java)
        intent.putExtra(Constants.AlarmID, id)
        startActivity(intent)
    }

    class AlarmBinder(val service: AlarmService) : Binder() {
        var snoozeListener: SnoozeListener? = null

        fun refreshAlarms() {
            service.alarms.clear()
            val alarmList = service.preferences.getStringSet(Constants.AlarmList, emptySet())
            service.alarms += alarmList.mapNotNull { gson.fromJson(service.preferences.getString(it, ""), Alarm::class.java) }
        }

        fun snoozeAlarm() {
            if (service.current?.snoozeTime ?: 0 > 0) {
                snoozeListener?.onSnooze()
                service.mp?.stop()

                service.handler.postDelayed({
                    service.mp?.start()
                }, service.current?.snoozeTime!! * 60000L)
            }
        }

        fun stopAlarm() {
            service.current = null
            service.sm?.unregisterListener(service)
            service.mp?.stop()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val diff = Math.sqrt((x*x + y*y + z*z).toDouble())
            if (diff > 0.5) {
                AlarmClock.instance.doWithService {
                    it.snoozeAlarm()
                }
            }
        }
    }

    interface SnoozeListener {
        fun onSnooze()
    }

    companion object {
        val gson = Gson()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}