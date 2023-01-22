package com.serah.coparenting


import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import com.serah.coparenting.databinding.CreateMessageBinding

import com.serah.coparenting.databinding.FragmentChatBinding


class ChatFragment : Fragment() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding= FragmentChatBinding.inflate(inflater,container,false)
        val newChat=binding.newChat
        binding.root.foreground.alpha=0

        val popupView= CreateMessageBinding.inflate(inflater,container,false)
        val popupWindow=PopupWindow(popupView.root,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        newChat.setOnClickListener{
            binding.root.foreground.alpha=1

            popupWindow.showAtLocation(it,Gravity.CENTER,0,0)
        }
        popupWindow.setOnDismissListener {
            binding.root.foreground.alpha=0
        }

        return binding.root
    }



}