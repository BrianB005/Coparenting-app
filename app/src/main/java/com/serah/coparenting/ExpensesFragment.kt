package com.serah.coparenting

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.serah.coparenting.databinding.CreateExpenseBinding
import com.serah.coparenting.databinding.FragmentExpensesBinding
import com.serah.coparenting.databinding.SingleExpenseBinding
import com.serah.coparenting.retrofit.Expense
import com.serah.coparenting.retrofit.ExpenseInterface
import com.serah.coparenting.retrofit.ExpensesInterface
import com.serah.coparenting.retrofit.RetrofitHandler
import okhttp3.ResponseBody


class ExpensesFragment : Fragment() {

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private lateinit var pusher: Pusher
    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        val binding= FragmentExpensesBinding.inflate(inflater,container,false)
        val newExpense=binding.newExpense

        val popupView= CreateExpenseBinding.inflate(inflater,container,false)

        val recyclerView=binding.recyclerView
        val progressBar=binding.progressView
        recyclerView.visibility=View.GONE
        val allExpenses=ArrayList<Expense>()

        val adapter=ExpensesRecyclerViewAdapter(allExpenses,requireContext(),binding)
        recyclerView.adapter=adapter


        newExpense.setOnClickListener {
            val background = View(requireContext())
            background.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
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


            popupView.create.setOnClickListener{
                val title=popupView.expenseTitle.text.toString()
                val category=popupView.expenseCategory.text.toString()
                val amount=popupView.amount.text.toString()
                if(title.isEmpty()){
                    popupView.titleLayout.error="Kindly fill this field!"
                }
                if(category.isEmpty()){
                    popupView.categoryLayout.error="Kindly fill this field!"
                }
                if(amount.isEmpty()){
                    popupView.amountLayout.error="Kindly fill this field!"
                }
                if(amount.isNotEmpty() &&title.isNotEmpty() && category.isNotEmpty()){
                    val expense=HashMap<String,Any>()
                    expense["amount"]=amount
                    expense["category"]=category
                    expense["title"]=title

                    popupView.create.text="Creating ..."
                    popupView.create.setBackgroundColor(R.color.dark_gray)
                    RetrofitHandler.createExpense(requireContext(),expense,object:ExpenseInterface{
                        @SuppressLint("SetTextI18n")
                        override fun success(expense: ResponseBody) {
                            val toast = Toast(context)
                            val customToast: View = layoutInflater.inflate(
                                R.layout.custom_toast,
                                container,
                                false
                            )
                            customToast.findViewById<TextView>(R.id.text).text="Expense created!"
                            toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 40)

                            toast.duration = Toast.LENGTH_SHORT
                            toast.setView(customToast)
                            toast.show()
                            popupView.expenseTitle.text?.clear()
                            popupView.expenseCategory.text?.clear()
                            popupView.amount.text?.clear()
                            popupView.create.text="Create"
                            popupView.create.setBackgroundColor(R.color.blue)
                        }

                        @SuppressLint("ResourceAsColor", "SetTextI18n")
                        override fun failure(throwable: Throwable) {
                            Toast.makeText(context,throwable.message, Toast.LENGTH_LONG).show()
                            popupView.create.text="Create"
                            popupView.create.setBackgroundColor(R.color.blue)
                        }

                        @SuppressLint("ResourceAsColor", "SetTextI18n")
                        override fun errorExists(message: String) {
                            Toast.makeText(context,message,Toast.LENGTH_LONG).show()
                            popupView.create.text="Create"
                            popupView.create.setBackgroundColor(R.color.blue)
                        }

                    })
                }
            }
            popupWindow.setOnDismissListener {
                binding.root.removeViewAt(binding.root.childCount - 1)
            }
        }


        //    /        configuring pusher
        val options = PusherOptions()
        options.setCluster("mt1")

        options.isUseTLS = true

        val userId = MyPreferences.getItemFromSP(requireContext(), "userId")

        pusher = Pusher("cf1a6d90710b2e4a1f85", options)
        if(isInternetConnected()){
            loadData(binding,progressBar,recyclerView,allExpenses,adapter)
            try {
                pusher.connect(object : ConnectionEventListener {
                    override fun onConnectionStateChange(change: ConnectionStateChange) {
                        Log.i(
                            "Pusher expenses", "State changed from " + change.previousState +
                                    " to " + change.currentState
                        )
                    }

                    override fun onError(message: String, code: String, e: Exception) {
                        Log.i(
                            "Pusher",
                            "There was a problem connecting! code: $code message: $message"
                        )
                    }
                }, ConnectionState.ALL)
                val channel1 = pusher.subscribe(userId)

                channel1.bind("expense") {
                    loadData(binding,progressBar,recyclerView,allExpenses,adapter)
                }
            } catch (e: Exception) {
                // Show an error message to the user
                Toast.makeText(requireContext(), "Internet connectivity lost!", Toast.LENGTH_SHORT).show()
//
//
//                Handler().postDelayed({
//                    pusher.connect(object : ConnectionEventListener {
//                        override fun onConnectionStateChange(change: ConnectionStateChange) {
//                            Log.i(
//                                "Pusher expenses", "State changed from " + change.previousState +
//                                        " to " + change.currentState
//                            )
//                        }
//
//                        override fun onError(message: String, code: String, e: Exception) {
//                            Log.i(
//                                "Pusher",
//                                "There was a problem connecting! code: $code message: $message"
//                            )
//                        }
//                    }, ConnectionState.ALL)
//                    val channel1 = pusher.subscribe(userId)
//
//                    channel1.bind("expense") {
//                        loadData(binding,progressBar,recyclerView,allExpenses,adapter)
//                    }
//                }, 5000) // Retry after 5 seconds
            }
        }
        else{
            binding.noInternet.visibility=View.VISIBLE
            binding.reloadBtn.setOnClickListener {
                loadData(binding,progressBar,recyclerView,allExpenses,adapter)
            }
            Toast.makeText(requireContext(), "No internet connection!Make sure that you are connected to the internet.", Toast.LENGTH_LONG).show()
        }
        return binding.root
    }
    private fun isInternetConnected(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun loadData(binding: FragmentExpensesBinding,progressBar:View,recyclerView:RecyclerView,allExpenses:ArrayList<Expense>,adapter:ExpensesRecyclerViewAdapter){
        if(isInternetConnected()) {
            progressBar.visibility = View.VISIBLE
            RetrofitHandler.getExpenses(requireContext(), object : ExpensesInterface {
                @SuppressLint("NotifyDataSetChanged")
                override fun success(expenses: List<Expense>) {
                    progressBar.visibility = View.GONE
                    if (expenses.isEmpty()) {
                        binding.noExpenses.visibility = View.VISIBLE
                    } else {
                        binding.noExpenses.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        allExpenses.clear()
                        allExpenses.addAll(expenses)
                        adapter.notifyDataSetChanged()
                    }
                }
                override fun failure(throwable: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), throwable.message, Toast.LENGTH_SHORT)
                        .show()
                }

                override fun errorExists(message: String) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }

            })

        }else{
            binding.noInternet.visibility=View.VISIBLE
            binding.reloadBtn.setOnClickListener {
                loadData(binding,progressBar,recyclerView,allExpenses,adapter)
            }
            Toast.makeText(requireContext(), "No internet connection!Make sure that you are connected to the internet.", Toast.LENGTH_LONG).show()
        }
    }
}