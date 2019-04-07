package hr.olfo.alarmclock.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioButton
import hr.olfo.alarmclock.AlarmClock
import hr.olfo.alarmclock.R
import kotlinx.android.synthetic.main.activity_choose_charity.*

class ChooseCharity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_charity)

        when (AlarmClock.charity) {
            "random" ->
                radioGroup.check(R.id.radioRandom)
            "charity1" ->
                radioGroup.check(R.id.radioCharity1)
            "charity2" ->
                radioGroup.check(R.id.radioCharity2)
            "charity3" ->
                radioGroup.check(R.id.radioCharity3)
            "charity4" ->
                radioGroup.check(R.id.radioCharity4)
            "charity5" ->
                radioGroup.check(R.id.radioCharity5)
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.radioRandom ->
                    if (checked) {
                        AlarmClock.charity = "random"
                        finish()
                    }
                R.id.radioCharity1 ->
                    if (checked) {
                        AlarmClock.charity = "charity1"
                        finish()
                    }
                R.id.radioCharity2 ->
                    if (checked) {
                        AlarmClock.charity = "charity2"
                        finish()
                    }
                R.id.radioCharity3 ->
                    if (checked) {
                        AlarmClock.charity = "charity3"
                        finish()
                    }
                R.id.radioCharity4 ->
                    if (checked) {
                        AlarmClock.charity = "charity4"
                        finish()
                    }
                R.id.radioCharity5 ->
                    if (checked) {
                        AlarmClock.charity = "charity5"
                        finish()
                    }
            }
        }
    }
}