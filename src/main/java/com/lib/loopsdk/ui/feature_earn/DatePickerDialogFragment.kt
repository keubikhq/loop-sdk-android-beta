package com.lib.loopsdk.ui.feature_earn

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.lib.loopsdk.core.util.getFormattedDateString
import java.util.Calendar

class DatePickerDialogFragment (
    private val listener:DateSelectedListener
        ): DialogFragment() ,DatePickerDialog.OnDateSetListener{

    var dateSelected:String=""


    @NonNull
    @Override
    override fun onCreateDialog(@Nullable savedInstanceState: Bundle?): Dialog {
        val mCalendar: Calendar = Calendar.getInstance()
        val year: Int = mCalendar.get(Calendar.YEAR)
        val month: Int = mCalendar.get(Calendar.MONTH)
        val dayOfMonth: Int = mCalendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog= DatePickerDialog(
            requireContext(),
            this,
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.datePicker.maxDate=System.currentTimeMillis()

        return  datePickerDialog
    }

    override fun onDateSet(p0: DatePicker?, yy: Int, mm: Int, dd: Int) {

        val month =mm+1
        var monthString = month.toString()
        if (monthString.length == 1) {
            monthString = "0$monthString"
        }

        var dateString = dd.toString()
        if (dateString.length == 1) {
            dateString = "0$dateString"
        }

        dateSelected=dateString+"/"+monthString+"/"+yy.toString()
        val dateAPIFormat = getFormattedDateString(dateSelected, "dd/MM/yyyy", "yyyy-MM-dd")
        listener.onDateSelectedListener(dateSelected,dateAPIFormat)
            this.dismiss()

    }

    interface DateSelectedListener{
        fun onDateSelectedListener(dateSelected: String, dateAPIFormat: String)
    }


}