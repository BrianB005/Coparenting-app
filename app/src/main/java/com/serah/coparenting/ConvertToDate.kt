package com.serah.coparenting

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import androidx.annotation.RequiresApi
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

class ConvertToDate {
    companion object {
        fun convertToDate(timestamp: String): Date? {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",Locale.getDefault());
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
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

            val date = convertToDate(mongoDBTimestamp!!)!!

            return dateFormat.format(date)
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
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SimpleDateFormat")
        fun getDateAgo(mongoDBTimestamp: String?): String? {
            val dateFormat = DateTimeFormatter.ofPattern("dd MMM yy")
            val date = getDate(mongoDBTimestamp!!)
            val localDate=LocalDate.parse(date.toString(),dateFormat)

            val today = LocalDate.now()
            val yesterday = today.minus(1, ChronoUnit.DAYS)
            return when (localDate) {
                today -> "Today"
                yesterday -> "Yesterday"
                else -> date.toString()
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