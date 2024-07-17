package com.ahmedzenhom.mytasks.utils.others

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.lifecycle.LiveData
import com.hadilq.liveevent.LiveEvent
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object DateUtils {


    fun getFormattedDate(date: Date?): String {
        if (date == null) return ""
        return SimpleDateFormat("EEE, dd MMMM yyyy hh:mm a").format(date)
    }

    fun getFormattedDateShort(date: Date?): String {
        if (date == null) return ""
        return SimpleDateFormat("dd MMMM yyyy, hh:mm a").format(date)
    }

    fun pickDateAndTime(context: Context, initial: Date): LiveData<Date> {
        val liveData = LiveEvent<Date>()
        with(Calendar.getInstance()) {
            time = initial
            DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)
                    TimePickerDialog(
                        context,
                        { _, selectedHour, selectedMinute ->
                            set(Calendar.HOUR_OF_DAY, selectedHour)
                            set(Calendar.MINUTE, selectedMinute)
                            liveData.value = time
                        }, get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE), false
                    ).show()
                }, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        return liveData
    }


}