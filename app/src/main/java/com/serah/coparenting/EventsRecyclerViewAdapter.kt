package com.serah.coparenting

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.FirebaseStorage
import com.google.type.Date

import com.serah.coparenting.databinding.FragmentCalendarBinding
import com.serah.coparenting.databinding.RejectionMsgBinding
import com.serah.coparenting.databinding.SingleExpenseBinding
import com.serah.coparenting.databinding.SingleOutgoingMessageBinding
import com.serah.coparenting.databinding.UpdatedEventBinding
import com.serah.coparenting.retrofit.CreateEventInterface
import com.serah.coparenting.retrofit.Event
import com.serah.coparenting.retrofit.Message
import com.serah.coparenting.retrofit.RetrofitHandler
import okhttp3.ResponseBody
import java.util.Calendar

class EventsRecyclerViewAdapter(private val events:List<Event>,val context:Context,val binding: FragmentCalendarBinding) :RecyclerView.Adapter<EventsRecyclerViewAdapter.MyViewHolder>(){

    private lateinit var popupView:RejectionMsgBinding
    private lateinit var customToast:View
    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        popupView=RejectionMsgBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        customToast=LayoutInflater.from(parent.context).inflate(R.layout.updated_event,parent,false)
        return when(viewType){
            0->MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_my_event,parent,false))
            1->MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_co_event,parent,false))
            else->throw IllegalArgumentException("Invalid View type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        val userId = MyPreferences.getItemFromSP(context, "userId")
        val viewType = if (events[position].user.userId == userId) {
            0
        }else {
            1

        }
        return viewType

    }

    @SuppressLint("SetTextI18n", "CutPasteId")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event=events[position]
        holder.itemView.findViewById<TextView>(R.id.title).text=event.title
        holder.itemView.findViewById<TextView>(R.id.startDate).text="${ConvertToDate.getDate(event.startDate)} ${ConvertToDate.getTime(event.startDate)}"
        holder.itemView.findViewById<TextView>(R.id.endDate).text="${ConvertToDate.getDate(event.endDate)} ${ConvertToDate.getTime(event.endDate)}"
        holder.itemView.findViewById<TextView>(R.id.time).text="${ConvertToDate.getDate(event.createdAt)} ${ConvertToDate.getTime(event.createdAt)}"

        val storageReference = FirebaseStorage.getInstance()
            .getReference("images/${event.user.profilePic}")

        val uriTask1: Task<Uri> = storageReference.downloadUrl

        uriTask1.addOnSuccessListener { uri1: Uri? ->
            Glide.with(context).load(uri1).into(holder.itemView.findViewById(R.id.profile_pic))
        }

        when (holder.itemViewType){

            0->{

                if( event.isAccepted){
                    holder.itemView.findViewById<TextView>(R.id.accepted).visibility=View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.rejected).visibility=View.GONE
                }
                else if( event.rejectionMessage==null ){
                    holder.itemView.findViewById<TextView>(R.id.rejected).visibility=View.GONE
                    holder.itemView.findViewById<TextView>(R.id.awaiting).visibility=View.VISIBLE
                }else{
                    holder.itemView.findViewById<TextView>(R.id.rejected).visibility=View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.awaiting).visibility=View.GONE
                    holder.itemView.findViewById<TextView>(R.id.rejection_msg).visibility=View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.rejection_msg).text="Reason : ${event.rejectionMessage}"
                }


            }
            1->{
                holder.itemView.findViewById<TextView>(R.id.name).text="${event.user.firstName} ${event.user.lastName}"
                val today=Calendar.getInstance().time

                if( event.isAccepted){
                    holder.itemView.findViewById<TextView>(R.id.accepted).visibility=View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.rejected).visibility=View.GONE
                    holder.itemView.findViewById<LinearLayout>(R.id.buttons).visibility=View.GONE
                    holder.itemView.findViewById<LinearLayout>(R.id.status).visibility=View.VISIBLE
                }
                else if(ConvertToDate.convertToDate(event.endDate)!!<=today){
                    holder.itemView.findViewById<LinearLayout>(R.id.buttons).visibility=View.GONE
                }
                else if( event.rejectionMessage==null ){
                    holder.itemView.findViewById<TextView>(R.id.rejected).visibility=View.GONE
                    holder.itemView.findViewById<LinearLayout>(R.id.buttons).visibility=View.VISIBLE
                    holder.itemView.findViewById<LinearLayout>(R.id.status).visibility=View.GONE

                    holder.itemView.findViewById<MaterialButton>(R.id.reject).setOnClickListener{
                        val background = View(context)
                        background.setBackgroundColor(ContextCompat.getColor(context, R.color.black))

                        val layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        binding.root.addView(background, layoutParams)
                        val popupWindow = PopupWindow(
                            popupView.root,
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
                        );
                        popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
                        popupView.cancel.setOnClickListener {
                            popupWindow.dismiss()
                        }
                        popupView.create.setOnClickListener {
                            val rejectionMessage=popupView.inputMessage.text.toString()
                            if(rejectionMessage.isEmpty()){
                                popupView.rejectionLayout.error="This field is required!"
                            }else{
                                val eventUpdate=HashMap<String,Any>()
                                eventUpdate["rejectionMessage"]=rejectionMessage
                                eventUpdate["isAccepted"]=false
                                holder.itemView.findViewById<MaterialButton>(R.id.reject).text="Updating ..."
                                RetrofitHandler.updateEvent(context,event.id,eventUpdate,object:CreateEventInterface{
                                    override fun success(event: ResponseBody) {
                                        val toast = Toast(context)

                                        toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 40)

                                        toast.duration = Toast.LENGTH_SHORT
                                        toast.setView(customToast)
                                        toast.show()
                                        holder.itemView.findViewById<MaterialButton>(R.id.reject).text="Reject"

                                    }

                                    override fun failure(throwable: Throwable) {
                                        Toast.makeText(context,"T ${throwable.message}",Toast.LENGTH_LONG).show()
                                        holder.itemView.findViewById<MaterialButton>(R.id.reject).text="Reject"
                                    }

                                    override fun errorExists(message: String) {
                                        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
                                        holder.itemView.findViewById<MaterialButton>(R.id.reject).text="Reject"

                                    }

                                })
                                popupWindow.dismiss()

                            }
                        }

                        popupWindow.setOnDismissListener {
                            binding.root.removeViewAt(binding.root.childCount - 1)
                        }
                    }
                    holder.itemView.findViewById<MaterialButton>(R.id.accept).setOnClickListener{
                            val eventUpdate=HashMap<String,Any>()
                            eventUpdate["isAccepted"]=true
                            holder.itemView.findViewById<MaterialButton>(R.id.accept).text="Updating ..."
                            RetrofitHandler.updateEvent(context,event.id,eventUpdate,object:CreateEventInterface{
                                override fun success(event: ResponseBody) {
                                    val toast = Toast(context)
                                    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 40)
                                    toast.duration = Toast.LENGTH_SHORT
                                    toast.setView(customToast)
                                    toast.show()
                                    holder.itemView.findViewById<MaterialButton>(R.id.accept).text="Accept"
                                }
                                override fun failure(throwable: Throwable) {
                                    Toast.makeText(context,"T ${throwable.message}",Toast.LENGTH_LONG).show()
                                    holder.itemView.findViewById<MaterialButton>(R.id.accept).text="Accept"
                                }

                                override fun errorExists(message: String) {
                                    Toast.makeText(context,message,Toast.LENGTH_LONG).show()
                                    holder.itemView.findViewById<MaterialButton>(R.id.accept).text="Accept"

                                }

                            })


                    }
                }else{
                    holder.itemView.findViewById<LinearLayout>(R.id.status).visibility=View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.rejected).visibility=View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.rejection_msg).visibility=View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.rejection_msg).text="Reason : ${event.rejectionMessage}"

                    holder.itemView.findViewById<LinearLayout>(R.id.buttons).visibility=View.GONE
                    holder.itemView.findViewById<TextView>(R.id.accepted).visibility=View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}