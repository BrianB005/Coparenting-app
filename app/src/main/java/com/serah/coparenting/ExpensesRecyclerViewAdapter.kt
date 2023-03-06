package com.serah.coparenting

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.FirebaseStorage
import com.serah.coparenting.databinding.FragmentExpensesBinding
import com.serah.coparenting.databinding.RejectionMsgBinding
import com.serah.coparenting.retrofit.*
import okhttp3.ResponseBody

class ExpensesRecyclerViewAdapter(private val expenses:List<Expense>,val context:Context,val binding: FragmentExpensesBinding) :RecyclerView.Adapter<ExpensesRecyclerViewAdapter.MyViewHolder>(){

    private lateinit var popupView:RejectionMsgBinding
    private lateinit var customToast:View
    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        popupView=RejectionMsgBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        customToast=LayoutInflater.from(parent.context).inflate(R.layout.updated_event,parent,false)
        return when(viewType){
            0->MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_my_expense,parent,false))
            1->MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_expense,parent,false))
            else->throw IllegalArgumentException("Invalid View type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        val userId = MyPreferences.getItemFromSP(context, "userId")
        val viewType = if (expenses[position].user.userId == userId) {
            0
        }else {
            1

        }
        return viewType

    }

    @SuppressLint("SetTextI18n", "CutPasteId")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val expense=expenses[position]
        val sharedAmount:Int=expense.amount / 2
        holder.itemView.findViewById<TextView>(R.id.expense_title).text=expense.title
        holder.itemView.findViewById<TextView>(R.id.category).text=expense.category
        holder.itemView.findViewById<TextView>(R.id.expense_amount).text="Kshs. ${expense.amount.toString()}"
        holder.itemView.findViewById<TextView>(R.id.my_amount).text="Kshs. $sharedAmount"
        holder.itemView.findViewById<TextView>(R.id.co_amount).text="Kshs. $sharedAmount"
        holder.itemView.findViewById<TextView>(R.id.time).text="${ConvertToDate.getDate(expense.createdAt)} ${ConvertToDate.getTime(expense.createdAt)}"

        val storageReference = FirebaseStorage.getInstance()
            .getReference("images/${expense.user.profilePic}")

        val uriTask1: Task<Uri> = storageReference.downloadUrl

        uriTask1.addOnSuccessListener { uri1: Uri? ->
            Glide.with(context).load(uri1).into(holder.itemView.findViewById(R.id.profile_pic))
        }

        when (holder.itemViewType){

            0->{

                if( expense.isAccepted){
                    holder.itemView.findViewById<TextView>(R.id.accepted).visibility=View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.rejected).visibility=View.GONE
                }
                else if( expense.rejectionMessage==null ){
                    holder.itemView.findViewById<TextView>(R.id.rejected).visibility=View.GONE
                    holder.itemView.findViewById<TextView>(R.id.awaiting).visibility=View.VISIBLE
                }else{
                    holder.itemView.findViewById<TextView>(R.id.rejected).visibility=View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.awaiting).visibility=View.GONE
                    holder.itemView.findViewById<TextView>(R.id.rejection_msg).visibility=View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.rejection_msg).text="Reason : ${expense.rejectionMessage}"
                }


            }
            1->{
                holder.itemView.findViewById<TextView>(R.id.name).text="${expense.user.firstName} ${expense.user.lastName}"

                if( expense.isAccepted){
                    holder.itemView.findViewById<TextView>(R.id.accepted).visibility=View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.rejected).visibility=View.GONE
                    holder.itemView.findViewById<LinearLayout>(R.id.buttons).visibility=View.GONE
                    holder.itemView.findViewById<LinearLayout>(R.id.status).visibility=View.VISIBLE
                }

                else if( expense.rejectionMessage==null ){
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
                                val expenseUpdate=HashMap<String,Any>()
                                expenseUpdate["rejectionMessage"]=rejectionMessage
                                expenseUpdate["isAccepted"]=false
                                holder.itemView.findViewById<MaterialButton>(R.id.reject).text="Updating ..."
                                RetrofitHandler.updateExpense(context,expense.id,expenseUpdate,object:ExpenseInterface{
                                    override fun success(expense: ResponseBody) {
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
                        val expenseUpdate=HashMap<String,Any>()
                        expenseUpdate["isAccepted"]=true
                        holder.itemView.findViewById<MaterialButton>(R.id.accept).text="Updating ..."
                        RetrofitHandler.updateExpense(context,expense.id,expenseUpdate,object:ExpenseInterface{
                            override fun success(expense: ResponseBody) {
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
                    holder.itemView.findViewById<TextView>(R.id.rejection_msg).text="Reason : ${expense.rejectionMessage}"

                    holder.itemView.findViewById<TextView>(R.id.accepted).visibility=View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return expenses.size
    }
}