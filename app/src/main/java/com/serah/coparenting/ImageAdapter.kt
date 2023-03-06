package com.serah.coparenting

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.serah.coparenting.databinding.CreateMessageBinding
import com.serah.coparenting.databinding.FragmentGalleryBinding

class ImageAdapter(private val context: Context, private val images: List<String>,private val binding:FragmentGalleryBinding,private val popupView: CreateMessageBinding,private val title:String,private val body:String) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        holder.imageView.setImageURI(Uri.parse(image))

        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        val margin = dpToPx(context, 10)
        val halfMargin = margin / 2

        if (position % 3 == 0) {
            layoutParams.setMargins(margin, margin, halfMargin, margin)
        } else if (position % 3 == 1) {
            layoutParams.setMargins(halfMargin, margin, halfMargin, margin)
        } else {
            layoutParams.setMargins(halfMargin, margin, margin, margin)
        }
        val popupWindow= PopupWindow(popupView.root,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true)
        holder.imageView.setOnClickListener{
            val background = View(context)
            background.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            binding.root.addView(background, params)

            background.isFocusable=false
            background.isClickable=true

            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)
        }
        popupWindow.setOnDismissListener {
            binding.root.removeViewAt(binding.root.childCount - 1)

        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}