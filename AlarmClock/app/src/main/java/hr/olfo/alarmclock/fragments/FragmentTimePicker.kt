package hr.olfo.alarmclock.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat
import android.widget.TimePicker
import hr.olfo.alarmclock.util.Constants
import java.util.*

class FragmentTimePicker: DialogFragment(), TimePickerDialog.OnTimeSetListener {

    lateinit var listener: TimePickerDialog.OnTimeSetListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val h = arguments?.getInt(Constants.ARGUMENT_HOUR) ?: c.get(Calendar.HOUR_OF_DAY)
        val m = arguments?.getInt(Constants.ARGUMENT_MINUTE) ?: c.get(Calendar.MINUTE)

        return TimePickerDialog(activity, this, h, m, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener.onTimeSet(view, hourOfDay, minute)
    }

}