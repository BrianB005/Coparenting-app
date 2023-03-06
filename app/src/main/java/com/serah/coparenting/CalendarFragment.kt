package com.serah.coparenting

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.RequiresApi

import androidx.core.content.ContextCompat
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange

import com.serah.coparenting.databinding.CreateEventBinding
import com.serah.coparenting.databinding.FragmentCalendarBinding
import com.serah.coparenting.retrofit.*
import okhttp3.ResponseBody
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragment : Fragment() {

    private lateinit var pusher: Pusher
    private lateinit var popupView:CreateEventBinding
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


//        val viewModel:MyViewModel by viewModels()
        val binding=FragmentCalendarBinding.inflate(inflater,container,false)
        popupView=CreateEventBinding.inflate(inflater,container,false)

        val recyclerView=binding.recyclerView
        val allEvents=ArrayList<Event>()
        val adapter=EventsRecyclerViewAdapter(allEvents,requireContext(),binding)

        recyclerView.adapter=adapter
        val progressbar=binding.progressView


        progressbar.visibility=View.VISIBLE
        recyclerView.visibility=View.GONE

        binding.createEvent.setOnClickListener {
            val popupWindow= PopupWindow(popupView.root,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
            val background = View(requireContext())
            background.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
            val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            binding.root.addView(background, layoutParams)
            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
            popupWindow.setOnDismissListener {
                binding.root.removeViewAt(binding.root.childCount - 1)
//                viewModel.viewPagerEnabled.value = true
            }
            val eventSDatePicker=popupView.eventStartDate
            val eventEDatePicker=popupView.eventEndDate
            val eventSTimePicker=popupView.eventStartTime
            val eventETimePicker=popupView.eventEndTime

            eventSTimePicker.setIs24HourView(true)
            eventETimePicker.setIs24HourView(true)
            eventEDatePicker.minDate=System.currentTimeMillis()
            eventSDatePicker.minDate=System.currentTimeMillis()

            popupView.cancel.setOnClickListener {
                popupWindow.dismiss()
            }
            popupView.createEvent.setOnClickListener {
                val sYear=eventSDatePicker.year
                val eYear=eventEDatePicker.year
                val sMonth=eventSDatePicker.month
                val eMonth=eventEDatePicker.month
                val sDay=eventSDatePicker.dayOfMonth
                val eDay=eventEDatePicker.dayOfMonth

                val sHour=eventSTimePicker.hour
                val sMinute=eventSTimePicker.minute

                val eHour=eventETimePicker.hour
                val eMinute=eventETimePicker.minute
                val eventTitle=popupView.editTextEventTitle.text.toString()





//                val sDateTime = LocalDateTime.of(sYear, sMonth + 1, sDay, sHour, sMinute)
//                val eDateTime = LocalDateTime.of(eYear, eMonth + 1, eDay, eHour, eMinute)
                val sCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, sYear)
                    set(Calendar.MONTH, sMonth)
                    set(Calendar.DAY_OF_MONTH, sDay)
                    set(Calendar.HOUR_OF_DAY, sHour)
                    set(Calendar.MINUTE, sMinute)
                }
                val sDate = Date(sCalendar.timeInMillis)
                val eCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, eYear)
                    set(Calendar.MONTH, eMonth)
                    set(Calendar.DAY_OF_MONTH, eDay)
                    set(Calendar.HOUR_OF_DAY, eHour)
                    set(Calendar.MINUTE, eMinute)
                }
                val eDate = Date(eCalendar.timeInMillis)
                if(sDate>eDate){
                    Toast.makeText(
                        context,"Event end date must be greater than start date!",Toast.LENGTH_LONG
                    ).show()
                }else{
                    if(eventTitle.isEmpty()){
                        popupView.textInputLayoutEventTitle.error="This field is required"
                        Toast.makeText(
                            context,"Event title is required!",Toast.LENGTH_LONG
                        ).show()
                    }else{
                        popupView.createEvent.text = "Scheduling ...."
                        val event=CreatedEvent(sDate,eDate, eventTitle  )
                        RetrofitHandler.createEvent(requireContext(),event,object:CreateEventInterface{
                            @SuppressLint("SetTextI18n")
                            override fun success(event: ResponseBody) {

                                popupView.createEvent.text = "Schedule"
                                val toast = Toast(context)
                                val customToast: View = layoutInflater.inflate(
                                    R.layout.event_sent,
                                    container,
                                    false
                                )
                                toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 40)

                                toast.duration = Toast.LENGTH_SHORT
                                toast.setView(customToast)
                                toast.show()
                                popupWindow.dismiss()

                            }

                            @SuppressLint("SetTextI18n")
                            override fun failure(throwable: Throwable) {
                                popupView.createEvent.text = "Schedule"
                                Toast.makeText(context,"T ${throwable.message}",Toast.LENGTH_LONG).show()
                            }
                            override fun errorExists(message: String) {
                                Toast.makeText(context,message,Toast.LENGTH_LONG).show()
                                popupView.createEvent.text = "Schedule"
                            }

                        })
                    }
                }
            }


        }

        //    /        configuring pusher
        val options = PusherOptions()
        options.setCluster("mt1")

        options.isUseTLS = true

        val userId = MyPreferences.getItemFromSP(requireContext(), "userId")

        pusher = Pusher("cf1a6d90710b2e4a1f85", options)

        if(isInternetConnected()) {
            RetrofitHandler.getEvents(requireContext(),object:EventsInterface{
                @SuppressLint("NotifyDataSetChanged")
                override fun success(events: List<Event>) {
                    Log.d("Events",events.toString())
                    if(events.isEmpty()){
                        binding.noEvents.visibility=View.VISIBLE
                        recyclerView.visibility=View.GONE
                    }
                    allEvents.addAll(events)
                    adapter.notifyDataSetChanged()
                    progressbar.visibility=View.GONE
                    recyclerView.visibility=View.VISIBLE
                }

                override fun failure(throwable: Throwable) {

                    progressbar.visibility=View.GONE
                    recyclerView.visibility=View.VISIBLE
                    Toast.makeText(context,"T ${throwable.message}",Toast.LENGTH_LONG).show()
                }

                override fun errorExists(message: String) {
                    Toast.makeText(context,message,Toast.LENGTH_LONG).show()
                    progressbar.visibility=View.GONE
                    recyclerView.visibility=View.VISIBLE
                }

            })
            pusher.connect(object : ConnectionEventListener {
                override fun onConnectionStateChange(change: ConnectionStateChange) {
                    Log.i(
                        "Pusher calendar", "State changed from " + change.previousState +
                                " to " + change.currentState
                    )
                }

                override fun onError(message: String, code: String, e: Exception) {
                    Log.i(
                        "Pusher", "There was a problem connecting! code: $code message: $message"
                    )
                }
            }, ConnectionState.ALL)
            val channel1 = pusher.subscribe(userId)

            channel1.bind("event") { event ->
                RetrofitHandler.getEvents(requireContext(), object : EventsInterface {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun success(events: List<Event>) {
                        Log.d("Events", events.toString())
                        if (events.isEmpty()) {
                            binding.noEvents.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            binding.noEvents.visibility = View.GONE
                            allEvents.clear()
                            allEvents.addAll(events)
                            adapter.notifyDataSetChanged()

                            recyclerView.visibility = View.VISIBLE
                        }
                        progressbar.visibility = View.GONE
                    }

                    override fun failure(throwable: Throwable) {

                        progressbar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        Toast.makeText(context, "T ${throwable.message}", Toast.LENGTH_LONG).show()
                    }

                    override fun errorExists(message: String) {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        progressbar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }

                })
            }
        }
        else{
            Toast.makeText(requireContext(), "No internet connection!Make sure that you are connected to the internet.", Toast.LENGTH_LONG).show()
        }
        return binding.root
    }
    private fun isInternetConnected(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}