package com.serah.coparenting

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage

import com.serah.coparenting.retrofit.Message
import com.serah.coparenting.room.MessageRetrieved
import java.util.Calendar

class ChatsRecyclerViewAdapter(private val chats:List<MessageRetrieved>,val context:Context) :RecyclerView.Adapter<ChatsRecyclerViewAdapter.MyViewHolder>(){

    var isDifferentDay=false
    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return when(viewType){
            0->MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_outgoing_message,parent,false))
            1->MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_incoming_message,parent,false))
            2->MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.out_going_msg_with_date,parent,false))
            3->MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.incoming_msg_with_date,parent,false))

            else->throw IllegalArgumentException("Invalid View type")
        }
    }

    override fun getItemViewType(position: Int): Int {

        val userId = MyPreferences.getItemFromSP(context, "userId")

        var viewType =0
        val currentDay=ConvertToDate.convertToDate(chats[position].message.createdAt)
        val previous=if(position ==0) ConvertToDate.convertToDate(chats[position].message.createdAt) else ConvertToDate.convertToDate(chats[position-1].message.createdAt)
        val cal1=Calendar.getInstance()
        val cal2=Calendar.getInstance()
        cal1.time=currentDay!!
        cal2.time=previous!!
        isDifferentDay = if(position==0){
            true

        }else cal1.get(Calendar.DAY_OF_MONTH)!=cal2.get(Calendar.DAY_OF_MONTH)

        viewType = if(isDifferentDay){
            if (chats[position].sender.userId == userId) {
                2
            } else {
                3
            }



        }else {
            if (chats[position].sender.userId == userId) {
                0
            } else {
                1
            }
        }







        return viewType

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val chat=chats[position]



        val time=ConvertToDate.getTime(chat.message.createdAt)
        holder.itemView.findViewById<TextView>(R.id.time).text=time
        holder.itemView.findViewById<TextView>(R.id.messageTitle).text=chat.message.title
        holder.itemView.findViewById<TextView>(R.id.messageBody).text=chat.message.body

        if(!chat.message.image.isNullOrEmpty()){
            holder.itemView.findViewById<CardView>(R.id.cardView).visibility=View.VISIBLE
            val storageReference2 = FirebaseStorage.getInstance()
                .getReference("images/${chat.message.image}")
            val uriTask: Task<Uri> = storageReference2.downloadUrl
            uriTask.addOnSuccessListener { uri1: Uri? ->
                Glide.with(context).load(uri1).into(holder.itemView.findViewById(R.id.shared_image))
            }
        }
        val storageReference = FirebaseStorage.getInstance()
            .getReference("images/${chat.sender.profilePic}")

        val uriTask1: Task<Uri> = storageReference.downloadUrl

        uriTask1.addOnSuccessListener { uri1: Uri? ->
            Glide.with(context).load(uri1).into(holder.itemView.findViewById(R.id.profile_pic))
        }
        when (holder.itemViewType){


            0->{

            }
            1->{

                holder.itemView.findViewById<TextView>(R.id.name).text=chat.sender.firstName

            }
            2,3->{
                holder.itemView.findViewById<TextView>(R.id.date).text=ConvertToDate.getDateAgo(chats[position].message.createdAt)
            }
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }
}