package hr.olfo.alarmclock.dialogs

import android.app.DialogFragment
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import hr.olfo.alarmclock.R
import hr.olfo.alarmclock.activities.AlarmCreate
import hr.olfo.alarmclock.util.Constants
import hr.olfo.alarmclock.util.Util

import kotlinx.android.synthetic.main.dialog_ringtone.*
import android.media.RingtoneManager
import android.media.Ringtone



class DialogRingtone: DialogFragment(), View.OnClickListener {

    var selected: Uri? = null
    lateinit var selectedName: String

    var prevRingtone: Ringtone? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_ringtone, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selected = if (Util.ringtones.keys.contains(Uri.parse(arguments.getString(Constants.ArgumentRingtone)))) {
            val res = Uri.parse(arguments.getString(Constants.ArgumentRingtone))
            selectedName = Util.ringtones[res] ?: "<ERROR>"
            res
        } else {
            Util.ringtones.keys.firstOrNull()
        }

        Util.ringtones.forEach { uri, name ->
            val rb = RadioButton(activity).also {
                it.text = name
                it.contentDescription = uri.toString()
            }
            rb.setOnClickListener {
                selected = uri
                selectedName = name

                prevRingtone?.stop()
                prevRingtone = RingtoneManager.getRingtone(activity, selected)
                prevRingtone?.play()
            }

            ringtoneButtons.addView(rb)
        }
        ringtoneButtons.check(ringtoneButtons.getChildAt(0).id)

        buttonCancel.setOnClickListener(this)
        buttonOk.setOnClickListener(this)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        (activity as AlarmCreate).dialogClosed(Constants.DialogIDRingtone)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            buttonCancel.id -> {
                prevRingtone?.stop()
                dismiss()
            }
            buttonOk.id -> {
                prevRingtone?.stop()
                selected?.also {(activity as AlarmCreate).setRingtone(it, selectedName)}
                dismiss()
            }
        }
    }
}