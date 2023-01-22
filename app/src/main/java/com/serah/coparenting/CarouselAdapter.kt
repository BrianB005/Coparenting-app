package com.serah.coparenting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.serah.coparenting.databinding.CarouselBinding


class CarouselAdapter(private val itemsList: ArrayList<CarouselItem>) :
    RecyclerView.Adapter<CarouselAdapter.MyViewHolder>() {

    class MyViewHolder(binding: CarouselBinding) : RecyclerView.ViewHolder(binding.root){
        val title=binding.title
        val subtitle=binding.subtitle
        val image=binding.image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding=CarouselBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text=itemsList[position].title
        holder.subtitle.text=itemsList[position].subTitle
        holder.image.setImageResource(itemsList[position].image)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

}