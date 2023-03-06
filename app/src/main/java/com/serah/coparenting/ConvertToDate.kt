package com.serah.coparenting

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ConvertToDate {
    companion object {
        fun convertToDate(timestamp: String): Date? {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",Locale.getDefault());
//            simpleDateFormat.timeZone = TimeZone.getTimeZone("Africa/Nairobi")
            var parsedDate: Date? = null;
            try {
                parsedDate = simpleDateFormat.parse(timestamp)!!
            } catch (e: ParseException) {
                Log.d("Exception", e.message!!)
            }
            return parsedDate

        }


        fun getTime(mongoDBTimestamp: String?): String? {
             val dateFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT,Locale.getDefault(),)

            val date = convertToDate(mongoDBTimestamp!!)
            val calendar = Calendar.getInstance()
            calendar.time = date!!
            calendar.timeZone = TimeZone.getTimeZone("Africa/Nairobi")
            return dateFormat.format(calendar.time)
        }

        fun getDate(mongoDBTimestamp: String?): String? {
            @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("dd MMM yy")

            val date = convertToDate(mongoDBTimestamp!!)
            return dateFormat.format(date!!)
        }

        fun getTimeAgo(mongoDBTimestamp: String?): String? {
            val date = convertToDate(mongoDBTimestamp!!)
            return DateUtils.getRelativeTimeSpanString(
                date!!.time,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
            ).toString()
//        return DateUtils.getRelativeDateTimeString(context,date.getTime(),DateUtils.MINUTE_IN_MILLIS,DateUtils.WEEK_IN_MILLIS,1).toString();
        }

        @SuppressLint("SimpleDateFormat")
        fun getDateAgo(mongoDBTimestamp: String?): String? {
            val dateFormat = SimpleDateFormat("dd MMM yy")
            val date = convertToDate(mongoDBTimestamp!!)

            // get the current date in the UTC time zone
            val currentDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

            currentDate.set(Calendar.HOUR_OF_DAY, 0)
            currentDate.set(Calendar.MINUTE, 0)
            currentDate.set(Calendar.SECOND, 0)
            currentDate.set(Calendar.MILLISECOND, 0)

            return when (TimeUnit.MILLISECONDS.toDays(currentDate.timeInMillis - date!!.time)) {
                0L -> "Today"
                1L -> "Yesterday"
                else -> dateFormat.format(date)
            }
        }



        fun getFullMonthFormat(mongoDBTimestamp: String?): String? {
            @SuppressLint("SimpleDateFormat") val dateFormat =
                SimpleDateFormat("MMMM dd,yyyy", Locale.ENGLISH)
            val date = convertToDate(mongoDBTimestamp!!)
            return dateFormat.format(date!!)
        }
    }
}