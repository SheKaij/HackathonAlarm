package hr.olfo.alarmclock.fragments

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import hr.olfo.alarmclock.AlarmClock
import hr.olfo.alarmclock.R
import hr.olfo.alarmclock.activities.AlarmCreate
import hr.olfo.alarmclock.data.Alarm
import hr.olfo.alarmclock.util.Constants
import hr.olfo.alarmclock.util.Util

import kotlinx.android.synthetic.main.fragment_alarm_preview.*

class FragmentAlarmPreview: Fragment(), PopupMenu.OnMenuItemClickListener {

    private var preferences: SharedPreferences? = null
    private var alarmID: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_alarm_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = activity.applicationContext.getSharedPreferences(Constants.PreferencesAlarms, Context.MODE_PRIVATE)

        alarmID = arguments.getString(Constants.AlarmID, "")
        val alarm = AlarmClock.gson.fromJson<Alarm>(preferences?.getString(alarmID, ""), Alarm::class.java)

        labelName.text = alarm.name
        labelTime.text = Util.getDisplayTime(activity, alarm.timeH, alarm.timeM)

        checkBoxEnable.isChecked = alarm.enabled
        checkBoxEnable.setOnClickListener {
            alarm.enabled = checkBoxEnable.isChecked
            val alarmData = AlarmClock.gson.toJson(alarm)
            preferences?.edit()?.also {
                it.putString(alarm.id, alarmData)
            }?.apply()
            AlarmClock.instance.doWithService {
                it.refreshAlarms()
            }
        }

        buttonOptions.setOnClickListener {
            val pm = PopupMenu(activity, buttonOptions)
            pm.setOnMenuItemClickListener(this)
            val inflater = pm.menuInflater
            inflater.inflate(R.menu.menu_alarm_options, pm.menu)
            pm.show()
        }

        view.setOnClickListener {
            val intent = Intent(activity, AlarmCreate::class.java)
            intent.putExtra(Constants.AlarmID, arguments.getString(Constants.AlarmID, ""))
            startActivity(intent)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                val alarmList = preferences?.getStringSet(Constants.AlarmList, mutableSetOf())
                preferences?.edit()?.also {
                    it.remove(alarmID)
                    it.putStringSet(Constants.AlarmList, alarmList?.filter{it != alarmID}?.toSet())
                }?.apply()
                view.visibility = View.GONE

            }
            R.id.duplicate -> {
                val intent = Intent(activity, AlarmCreate::class.java)
                val newAlarm = AlarmClock.gson.fromJson<Alarm>(preferences?.getString(alarmID, ""), Alarm::class.java).clone()

                val alarmData = AlarmClock.gson.toJson(newAlarm)
                val set = preferences?.getStringSet(Constants.AlarmList, mutableSetOf())
                preferences?.edit()?.also {
                    it.putString(newAlarm.id, alarmData)
                    set?.add(newAlarm.id)
                    it.putStringSet(Constants.AlarmList, set)
                }?.apply()

                intent.putExtra(Constants.AlarmID, newAlarm.id)
                startActivity(intent)
            }
            else -> return false
        }
        return true
    }

    companion object {
        fun create(alarmID: String): FragmentAlarmPreview {
            val result = FragmentAlarmPreview()
            val bundle = Bundle()
            bundle.putString(Constants.AlarmID, alarmID)
            result.arguments = bundle
            return result
        }
    }
}