package com.serah.coparenting

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.serah.coparenting.databinding.FragmentExpensesBinding
import com.serah.coparenting.databinding.SingleExpenseBinding


class ExpensesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        val binding= FragmentExpensesBinding.inflate(inflater,container,false)
        val newExpense=binding.newExpense

        val popupView= SingleExpenseBinding.inflate(inflater,container,false)
        newExpense.setOnClickListener{
            val popupWindow= PopupWindow(popupView.root,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
            popupWindow.showAtLocation(it, Gravity.CENTER,0,0)
        }
        return binding.root
    }
}