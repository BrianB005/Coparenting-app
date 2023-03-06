package com.serah.coparenting

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.gms.tasks.Task

import com.google.firebase.storage.FirebaseStorage

import com.serah.coparenting.retrofit.Gallery
import com.bumptech.glide.request.target.Target

class GalleryRecyclerViewAdapter (val context:Context, private val galleryItems:ArrayList<Gallery>):RecyclerView.Adapter<GalleryRecyclerViewAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val gridLayout: GridLayout = itemView.findViewById(R.id.gallery_item_grid)
        val title:TextView=itemView.findViewById(R.id.title)
        val date:TextView=itemView.findViewById(R.id.date)
        val time:TextView=itemView.findViewById(R.id.time)
        val name:TextView=itemView.findViewById(R.id.name)
        val profilePic:ImageView=itemView.findViewById(R.id.profile_pic)
        @JvmField
        val imageViews: Array<ImageView> = arrayOf(
            itemView.findViewById(R.id.gallery_image_1),
            itemView.findViewById(R.id.gallery_image_2),
            itemView.findViewById(R.id.gallery_image_3),
            itemView.findViewById(R.id.gallery_image_4)
        )


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_gallery_item, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val galleryItem = galleryItems[position]
        holder.title.text=galleryItem.caption
        holder.date.text=ConvertToDate.getDateAgo(galleryItem.createdAt)
        holder.time.text=ConvertToDate.getTime(galleryItem.createdAt)
        val userId=MyPreferences.getItemFromSP(context,"userId")
        val name=if(userId==galleryItem.user.userId) { "You"} else {"${galleryItem.user.firstName} ${galleryItem.user.lastName}"}
        holder.name.text= name
        val storageReference = FirebaseStorage.getInstance()
            .getReference("images/${galleryItem.user.profilePic}")

        val downloadTasks = mutableListOf<Task<Uri>>()
        val uriTask1: Task<Uri> = storageReference.downloadUrl

        uriTask1.addOnSuccessListener { uri1: Uri? ->
            Glide.with(context).load(uri1).into(holder.profilePic)
        }



        galleryItem.images.forEachIndexed { index, imageUrl ->
//            while(index<=holder.imageViews.size) {
                holder.imageViews[index].visibility=View.VISIBLE
                val storageReference1 = FirebaseStorage.getInstance()
                    .getReference("images/${imageUrl}")
                val uriTask: Task<Uri> = storageReference1.downloadUrl
                uriTask.addOnSuccessListener {
                    Glide.with(context)
                            .load(it)
                            .into(holder.imageViews[index])
                }


//            }
        }
    }

    override fun getItemCount(): Int = galleryItems.size


}