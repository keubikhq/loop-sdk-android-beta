package com.lib.loopsdk.core.util

import android.annotation.SuppressLint
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object TimeUtils {
    val ONE_MILISECOND: Long = 1
    val ONE_SECOND: Long = 1000 * ONE_MILISECOND
    val ONE_MINUTE: Long = 60 * ONE_SECOND
    val ONE_HOUR: Long = 60 * ONE_MINUTE
    val ONE_DAY: Long = 24 * ONE_HOUR
    val ONE_WEEK: Long = 6 * ONE_DAY
    val ONE_MONTH: Long = 30 * ONE_DAY
    val ONE_YEAR: Long = 12 * ONE_MONTH

    val THIRTEEN_YEARS: Long = 13 * ONE_YEAR
    val EIGHTEEN_YEARS: Long = 18 * ONE_YEAR
    val HUNDERED_TWENTY_YEARS: Long = 120 * ONE_YEAR
}

fun Date.toDDMMYYYY(): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    return dateFormat.format(this)
}

fun Date.toHHMM(): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    return dateFormat.format(this)
}

fun Date.toHHMMA(): String {
    val dateFormat = SimpleDateFormat("HH:mm a", Locale.ENGLISH)
    return dateFormat.format(this)
}

fun Date.toYYYYMMDD(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    return dateFormat.format(this)
}

fun Long.toDDMMYYYY(): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    return dateFormat.format(Date(this))
}

fun String.toDateObjectfromDDMMYYYY(): Date? {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    return dateFormat.parse(this)
}

fun getCurrentDateDDMMMMYYYY(): String {
    val date = Date()
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
    return dateFormat.format(date)
}

fun getCurrentTimeHHMMSS(): String {
    val date = Date()
    val dateFormat = SimpleDateFormat("hh:mm:ss", Locale.ENGLISH)
    return dateFormat.format(date)
}

fun getCurrentTimeHHMM(): String {
    val date = Date()
    val dateFormat = SimpleDateFormat("hh:mm", Locale.ENGLISH)
    return dateFormat.format(date)
}

fun getCurrentDate(): String {
    val date = Date()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.ENGLISH)
    return dateFormat.format(date)
}

fun calculateDifferenceInDays(): Int {
    if (Prefs.getString(Constants.LAST_DATE_POP_WAS_SHOWN).isNullOrEmpty()) return -1
    val milliSeconds = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH).parse(Prefs.getString(Constants.LAST_DATE_POP_WAS_SHOWN)).time - Calendar.getInstance().timeInMillis
    return TimeUnit.MILLISECONDS.toDays(milliSeconds).toInt()
}

fun getCurrentDateDDMMYYYY(): String {
    val date = Date()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    return dateFormat.format(date)
}

fun Date.checkIfBelongsToCurrentWeek(): Boolean {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val monday = calendar.time
    val nextMonday = Date(monday.time + TimeUtils.ONE_WEEK)
    return this.after(monday) && this.before(nextMonday)
}

fun String?.isCurrentDate(): Boolean {
    if (this.isNullOrEmpty()) return false
    return this.equals(getCurrentDateDDMMYYYY(), true)
}

fun getTimeIn12Hours(inputTime: String): String {
    val timeCalendar = getCalendarFromString(inputTime,"HH:mm")
    val retailerCalendar = Calendar.getInstance()
    retailerCalendar.set(Calendar.HOUR_OF_DAY,timeCalendar.get(Calendar.HOUR_OF_DAY))
    retailerCalendar.set(Calendar.MINUTE,timeCalendar.get(Calendar.MINUTE))
    return SimpleDateFormat("hh:mm a").format(retailerCalendar.time)
}

fun getFormattedDateString(
    dateString: String,
    fromFormat: String = Constants.API_DATE_FORMAT,
    toFormat: String,
): String {
    if (dateString.isNullOrEmpty()) return ""
    val calendar = Calendar.getInstance()
    var sdf = SimpleDateFormat(fromFormat, Locale.ENGLISH)
    calendar.time = sdf.parse(dateString)!!
    sdf = SimpleDateFormat(toFormat, Locale.ENGLISH)
    return sdf.format(calendar.time)
}

fun checkIfCurrentTimeBeforeGivenTime(time: String): Boolean {
    val currentCalendar = Calendar.getInstance()
    val timeCalendar = getCalendarFromString(time,"HH:mm")
    val retailerCalendar = Calendar.getInstance()
    retailerCalendar.set(Calendar.HOUR_OF_DAY,timeCalendar.get(Calendar.HOUR_OF_DAY))
    retailerCalendar.set(Calendar.MINUTE,timeCalendar.get(Calendar.MINUTE))
    if (currentCalendar.time.before(retailerCalendar.time))
        return true
    return false
}

fun calculateTimeDiffInHours(inputTime: String): Int {
    val currentCalendar = Calendar.getInstance()
    val timeCalendar = getCalendarFromString(inputTime,"HH:mm")
    val retailerCalendar = Calendar.getInstance()
    retailerCalendar.set(Calendar.HOUR_OF_DAY,timeCalendar.get(Calendar.HOUR_OF_DAY))
    retailerCalendar.set(Calendar.MINUTE,timeCalendar.get(Calendar.MINUTE))
    val difference: Long = retailerCalendar.time.time - currentCalendar.time.time
    val days = (difference / (1000 * 60 * 60 * 24)).toInt()
    val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
    val min = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
    val sec = (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours - 1000 * 60 * min).toInt() / 1000
    Timber.e("======= minutes :: $hours")
    return hours
}

fun getDiffCurrentTimeAndGivenTime(time: String) {
    val simpleDateFormat = SimpleDateFormat("hh:mm")

}


fun getDateWithSuffix(dateString: String?, isDotRequired: Boolean = false, isCommaAfterMonth: Boolean = false, destFormat: String = ""): String {
    if(dateString.isNullOrEmpty()) return  ""
    val suffix = dateString.getDateSuffix()
    val toFormat = if (destFormat.isEmpty()) {
        if (isDotRequired) {
            if (isCommaAfterMonth) "dd'$suffix' MMM, yyyy'.'" else "dd'$suffix' MMM, yyyy'.'"
        } else {
            if (isCommaAfterMonth) "dd'$suffix' MMM, yyyy" else "dd'$suffix' MMM, yyyy"
        }
    } else {
        destFormat.format(suffix)
    }
    return getFormattedDateString(dateString,toFormat = toFormat)
}

fun getDateWithSuffixFullMonth(dateString: String?, isDotRequired: Boolean = false, isCommaAfterMonth: Boolean = false, destFormat: String = ""): String {
    if(dateString.isNullOrEmpty()) return  ""
    val suffix = dateString.getDateSuffix()
    val toFormat = if (destFormat.isEmpty()) {
        if (isDotRequired) {
            if (isCommaAfterMonth) "dd'$suffix' MMMM, yyyy'.'" else "dd'$suffix' MMMM, yyyy'.'"
        } else {
            if (isCommaAfterMonth) "dd'$suffix' MMMM, yyyy" else "dd'$suffix' MMMM, yyyy"
        }
    } else {
        destFormat.format(suffix)
    }
    return getFormattedDateString(dateString,toFormat = toFormat)
}
fun getDateWithSuffixHalfMonth(dateString: String?, isDotRequired: Boolean = false, isCommaAfterMonth: Boolean = false, destFormat: String = "", fromFormat: String = Constants.API_DATE_FORMAT): String {
    if(dateString.isNullOrEmpty()) return  ""
    val suffix = dateString.getDateSuffix(fromFormat)
    val toFormat = if (destFormat.isEmpty()) {
        if (isDotRequired) {
            if (isCommaAfterMonth) "dd'$suffix' MMM, yyyy'.'" else "dd'$suffix' MMM, yyyy'.'"
        } else {
            if (isCommaAfterMonth) "dd'$suffix' MMM, yyyy" else "dd'$suffix' MMM, yyyy"
        }
    } else {
        destFormat.format(suffix)
    }
    return getFormattedDateString(dateString,toFormat = toFormat, fromFormat = fromFormat)
}
fun getDateWithSuffixFullMonth2(dateString: String?, isDotRequired: Boolean = false, isCommaAfterMonth: Boolean = false, destFormat: String = ""): String {
    if(dateString.isNullOrEmpty()) return  ""
    val suffix = dateString.getDateSuffix()
    val toFormat = if (destFormat.isEmpty()) {
        if (isDotRequired) {
            if (isCommaAfterMonth) "dd'$suffix' MMMM, yyyy'.'" else "dd'$suffix' MMMM, yyyy'.'"
        } else {
            if (isCommaAfterMonth) "dd'$suffix' MMMM, yyyy" else "dd'$suffix' MMMM, yyyy"
        }
    } else {
        destFormat.format(suffix)
    }
    return getFormattedDateString(dateString, fromFormat = "yyyy-MM-dd", toFormat = toFormat)
}
fun iso8601ToIST(iso8601Date: String?, pattern: String = Constants.API_DATE_FORMAT): String {
    if (iso8601Date.isNullOrEmpty()) return ""
    // added try catch if invalid date has been sent
    return try {
        val iso8601Sdf =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        iso8601Sdf.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val date = iso8601Sdf.parse(iso8601Date) ?: Calendar.getInstance().time
        val calendar = Calendar.getInstance()
        calendar.time = date
        getDateOfFormat(calendar, pattern)
    } catch (e: Exception) {
        ""
    }
}

@SuppressLint("SimpleDateFormat")
fun getDateforCalendar(dateStr: String):String {
    try{
    val formatter: DateFormat = SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH)
    val date = formatter.parse(dateStr) as Date
    val cal = Calendar.getInstance()
    cal.time = date
    return getDateOfFormat(cal, Constants.API_DATE_FORMAT)
    } catch (e: Exception) {
        return ""
    }
}