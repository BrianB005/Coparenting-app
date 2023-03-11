package com.serah.coparenting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.storage.FirebaseStorage
import com.serah.coparenting.databinding.ActivityEditProfileBinding
import com.serah.coparenting.retrofit.ResponseBodyInterface
import com.serah.coparenting.retrofit.RetrofitHandler
import com.serah.coparenting.retrofit.User
import com.serah.coparenting.retrofit.UserInterface
import okhttp3.ResponseBody
class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var currentUser: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolBar
//        toolbar.getChildAt(0).setOnClickListener {  super.onBackPressed() }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        RetrofitHandler.getCurrentUser(applicationContext,object:UserInterface{
            @SuppressLint("SetTextI18n")
            override fun success(user: User) {
                binding.editFName.setText(user.firstName)
                binding.editLName.setText(user.lastName)
                binding.editPhone.setText(user.phone)
                binding.email.text=user.email

                currentUser=user

                val storageReference=FirebaseStorage.getInstance().getReference("images/${user.profilePic}")
                val uriTask=storageReference.downloadUrl
                uriTask.addOnSuccessListener {

                    Glide.with(applicationContext).load(it).into(binding.profilePic)
                }
                binding.changeProfilePic.setOnClickListener {

                    selectImage()
                }
                binding.updateMyDetails.setOnClickListener {
                    val firstName=binding.editFName.text.toString()
                    val lastName=binding.editLName.text.toString()
                    val phone=binding.editPhone.text.toString()
                    if(firstName == user.firstName && lastName == user.lastName && phone==user.phone){
                        Toast.makeText(applicationContext,"There are no changes to update!",Toast.LENGTH_LONG).show()
                    }else if(firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty())
                        Toast.makeText(applicationContext,"Kindly fill all the fields!",Toast.LENGTH_LONG).show()
                    else{
                        val update=HashMap<String,String>()
                        update["firstName"]=firstName
                        update["lastName"]=lastName
                        update["phone"]=phone
                        binding.updateMyDetails.text="Updating ..."
                        RetrofitHandler.updateUser(applicationContext,update,object:ResponseBodyInterface{
                            @SuppressLint("InflateParams")
                            override fun success(response: ResponseBody) {
                                val toast = Toast(applicationContext)
                                val customToast: View = layoutInflater.inflate(
                                    R.layout.details_updated,
                                    null)
//                                customToast.findViewById<TextView>()
                                toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 40)

                                toast.duration = Toast.LENGTH_SHORT
                                toast.setView(customToast)
                                toast.show()
                                binding.updateMyDetails.text="Update"
                            }

                            override fun failure(throwable: Throwable) {
                                binding.updateMyDetails.text="Update"
                                Toast.makeText(applicationContext,throwable.message,Toast.LENGTH_LONG).show()
                            }

                            override fun errorExists(message: String) {
                                binding.updateMyDetails.text="Update"
                                Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
                            }

                        })
                    }
                }
                if(user.coparent==null){
                    binding.noCoparent.visibility=View.VISIBLE
                    binding.coParentDetails.visibility=View.GONE

                    binding.userId.text=user.userId
                    binding.copyToClipBoard.setOnClickListener {

                        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                        val clipData = ClipData.newPlainText("text", user.userId)
                        clipboardManager.setPrimaryClip(clipData)

                        Toast.makeText(applicationContext, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
                    }

                    binding.sendEmail.setOnClickListener {
                        val recipient=binding.inputEmail.text.toString()
                        if(recipient.isEmpty()){
                            binding.inputEmailLayout.error="Kindly fill this field"
                        }
                        else{
                            binding.sendEmail.text="Sending ..."
                            RetrofitHandler.sendEmail(applicationContext,recipient,object:
                                ResponseBodyInterface {
                                override fun success(response: ResponseBody) {
                                    binding.sendEmail.text="Send"
                                    Toast.makeText(applicationContext,"Email sent successfully!",Toast.LENGTH_LONG).show()
                                }
                                override fun failure(throwable: Throwable) {
                                    binding.sendEmail.text="Send"
                                    Toast.makeText(applicationContext,throwable.message,Toast.LENGTH_LONG).show()
                                }
                                override fun errorExists(message: String) {
                                    binding.sendEmail.text="Send"
                                    Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
                                }
                            })
                        }
                    }
                }else{
                    binding.noCoparent.visibility=View.GONE
                    binding.coFName.text=user.coparent.firstName
                    binding.coLName.text=user.coparent.firstName
                    binding.coPhone.text=user.coparent.phone


                }
            }

            override fun failure(throwable: Throwable) {
                Toast.makeText(applicationContext,throwable.message, Toast.LENGTH_SHORT).show()
            }

            override fun errorExists(message: String) {
                Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri: Uri = data?.data!!
                binding.profilePic.setImageURI(uri)
                val fileName=System.currentTimeMillis()
                val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
                storageReference.putFile(uri).addOnCompleteListener {
                    val update=HashMap<String,String>()
                    update["profilePic"]=fileName.toString()
                    RetrofitHandler.updateUser(applicationContext,update,object :ResponseBodyInterface{
                        override fun success(response: ResponseBody) {
                            Toast.makeText(applicationContext,"Profile pic updated successfully!",Toast.LENGTH_SHORT).show()
                        }
                        override fun failure(throwable: Throwable) {
                            Log.d("Failure",throwable.message!!)
                        }

                        override fun errorExists(message: String) {
                            Log.d("Failed",message)
                        }

                    })

                }


            }
            ImagePicker.RESULT_ERROR -> {
                //            Toast.makeText(this, ImagePicker.getError(data).toString(), Toast.LENGTH_SHORT).show()
                Log.d("Upload Error", ImagePicker.getError(data))
            }
            else -> {

            }
        }
    }
    private fun selectImage() {
        ImagePicker.with(this)

            .start()

    }
}