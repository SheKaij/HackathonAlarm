package hr.olfo.alarmclock.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import hr.olfo.alarmclock.AlarmClock
import hr.olfo.alarmclock.R
import hr.olfo.alarmclock.data.Alarm
import hr.olfo.alarmclock.fragments.FragmentAlarmPreview
import hr.olfo.alarmclock.util.Constants
import hr.olfo.alarmclock.util.Util

import kotlinx.android.synthetic.main.activity_alarm_preview.*

class AlarmPreview : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_preview)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val intent = Intent(this, AlarmCreate::class.java)
            startActivity(intent)
        }

        refreshAlarmList()
    }

    fun refreshAlarmList() {
        val preferences: SharedPreferences = applicationContext.getSharedPreferences(Constants.PreferencesAlarms, Context.MODE_PRIVATE)

        alarms.removeAllViewsInLayout()

        val fm = fragmentManager
        val ft = fm.beginTransaction()

        val alarmList = preferences.getStringSet(Constants.AlarmList, mutableSetOf())
        alarmList.forEach {
            val frag = FragmentAlarmPreview.create(it)
            ft.add(alarms.id, frag, "preview$it")
        }

        ft.commit()
    }

    override fun onResume() {
        super.onResume()
        refreshAlarmList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_alarm_preview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.disable_all -> {
                val preferences: SharedPreferences = applicationContext.getSharedPreferences(Constants.PreferencesAlarms, Context.MODE_PRIVATE)
                val alarmList = preferences.getStringSet(Constants.AlarmList, emptySet())

                val alarms = alarmList.mapNotNull { AlarmClock.gson.fromJson(preferences.getString(it, ""), Alarm::class.java) }

                alarms.forEach {alarm ->
                    alarm.enabled = false

                    val alarmData = AlarmClock.gson.toJson(alarm)
                    preferences.edit().also {
                        it.putString(alarm.id, alarmData)
                    }.apply()
                }

                refreshAlarmList()
                AlarmClock.instance.doWithService {
                    it.refreshAlarms()
                }
            }
            R.id.choose_charity -> {
                val intent = Intent(this, ChooseCharity::class.java)
                startActivity(intent)
            }
            else -> return false
        }
        return true
    }
}
