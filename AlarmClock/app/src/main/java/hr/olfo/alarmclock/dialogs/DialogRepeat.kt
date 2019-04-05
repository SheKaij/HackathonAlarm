package hr.olfo.alarmclock.dialogs

import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hr.olfo.alarmclock.R
import hr.olfo.alarmclock.activities.AlarmCreate
import hr.olfo.alarmclock.util.Constants
import kotlinx.android.synthetic.main.dialog_repeat.*

class DialogRepeat: DialogFragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_repeat, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkBoxMonday.isChecked = arguments.getBoolean(Constants.ARGUMENT_MON)
        checkBoxTuesday.isChecked = arguments.getBoolean(Constants.ARGUMENT_TUE)
        checkBoxWednesday.isChecked = arguments.getBoolean(Constants.ARGUMENT_WED)
        checkBoxThursday.isChecked = arguments.getBoolean(Constants.ARGUMENT_THU)
        checkBoxFriday.isChecked = arguments.getBoolean(Constants.ARGUMENT_FRI)

        checkBoxSaturday.isChecked = arguments.getBoolean(Constants.ARGUMENT_SAT)
        checkBoxSunday.isChecked = arguments.getBoolean(Constants.ARGUMENT_SUN)

        buttonWeekdays.isChecked = checkBoxMonday.isChecked && checkBoxTuesday.isChecked && checkBoxWednesday.isChecked && checkBoxThursday.isChecked && checkBoxFriday.isChecked
        buttonWeekends.isChecked = checkBoxSaturday.isChecked && checkBoxSunday.isChecked

        buttonCancel.setOnClickListener(this)
        buttonOk.setOnClickListener(this)

        val checkBoxListener = View.OnClickListener {
            buttonWeekdays.isChecked = checkBoxMonday.isChecked && checkBoxTuesday.isChecked && checkBoxWednesday.isChecked && checkBoxThursday.isChecked && checkBoxFriday.isChecked
            buttonWeekends.isChecked = checkBoxSaturday.isChecked && checkBoxSunday.isChecked
        }

        checkBoxMonday.setOnClickListener(checkBoxListener)
        checkBoxTuesday.setOnClickListener(checkBoxListener)
        checkBoxWednesday.setOnClickListener(checkBoxListener)
        checkBoxThursday.setOnClickListener(checkBoxListener)
        checkBoxFriday.setOnClickListener(checkBoxListener)
        checkBoxSaturday.setOnClickListener(checkBoxListener)
        checkBoxSunday.setOnClickListener(checkBoxListener)

        buttonWeekdays.setOnClickListener {
            checkBoxMonday.isChecked = buttonWeekdays.isChecked
            checkBoxTuesday.isChecked = buttonWeekdays.isChecked
            checkBoxWednesday.isChecked = buttonWeekdays.isChecked
            checkBoxThursday.isChecked = buttonWeekdays.isChecked
            checkBoxFriday.isChecked = buttonWeekdays.isChecked
        }

        buttonWeekends.setOnClickListener {
            checkBoxSaturday.isChecked = buttonWeekends.isChecked
            checkBoxSunday.isChecked = buttonWeekends.isChecked
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        (activity as AlarmCreate).dialogClosed(Constants.DialogIDRepeat)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            buttonCancel.id -> dismiss()
            buttonOk.id -> {
                (activity as AlarmCreate).setRepeat(
                    checkBoxMonday.isChecked,
                    checkBoxTuesday.isChecked,
                    checkBoxWednesday.isChecked,
                    checkBoxThursday.isChecked,
                    checkBoxFriday.isChecked,
                    checkBoxSaturday.isChecked,
                    checkBoxSunday.isChecked
                )
                dismiss()
            }
        }
    }
}