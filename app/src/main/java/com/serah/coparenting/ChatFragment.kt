package com.serah.coparenting


import android.annotation.SuppressLint
import android.app.Activity
import android.content.*


import android.content.ContentValues.TAG
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.serah.coparenting.databinding.CreateMessageBinding
import com.serah.coparenting.databinding.FragmentChatBinding
import com.serah.coparenting.retrofit.*
import com.serah.coparenting.room.MessageRetrieved
import com.serah.coparenting.room.MessageToSave
import com.serah.coparenting.room.RoomViewModel

import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject

import java.io.File


class ChatFragment : Fragment() {
    @RequiresApi(Build.VERSION_CODES.M)
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var viewModel: MyViewModel
    private lateinit var pusher: Pusher

    private lateinit var file: File

    private lateinit var popupView: CreateMessageBinding

    private lateinit var roomViewModel: RoomViewModel

    private lateinit var storageReference: StorageReference

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri: Uri = data?.data!!
                popupView.selectedImage.visibility = View.VISIBLE
                popupView.selectedImage.setImageURI(uri)
                popupView.sendMsg.setOnClickListener {
                    val fileName = System.currentTimeMillis().toString()
                    popupView.sendMsg.text = "Sending ..."

                    storageReference =
                        FirebaseStorage.getInstance().getReference("images/$fileName")
                    storageReference.putFile(uri).addOnCompleteListener {
                        val message = HashMap<String, String>()
                        val title = popupView.subjectInput.text.toString()
                        val body = popupView.bodyInput.text.toString()
                        if (title.isEmpty()) {
                            popupView.subjectInput.error = "Kindly fill this field!"

                        }

                        popupView.sendMsg.setBackgroundColor(R.color.dark_gray)
                        message["body"] = body
                        message["title"] = title
                        message["image"] = fileName



                        RetrofitHandler.sendMessage(
                            requireContext(),
                            message,
                            object : MessageInterface {
                                @SuppressLint("InflateParams", "SetTextI18n")
                                override fun success(message: SentMessage) {
                                    val toast = Toast(context)
                                    val customToast: View = layoutInflater.inflate(
                                        R.layout.message_sent,
                                        null,

                                        )
                                    toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 40)

                                    toast.duration = Toast.LENGTH_SHORT
                                    toast.setView(customToast)
                                    toast.show()
                                    popupView.subjectInput.text.clear()
                                    popupView.bodyInput.text.clear()
                                    popupView.sendMsg.text = "Send"
                                    popupView.selectedImage.setImageURI(null)
                                    popupView.selectedImage.visibility = View.GONE


                                }

                                @SuppressLint("ResourceAsColor", "SetTextI18n")
                                override fun failure(throwable: Throwable) {
                                    Toast.makeText(
                                        context,
                                        "T ${throwable.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    popupView.sendMsg.text = "Send"
                                    popupView.sendMsg.setBackgroundColor(R.color.blue)

                                }

                                @SuppressLint("ResourceAsColor", "SetTextI18n")
                                override fun errorExists(message: String) {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    popupView.sendMsg.text = "Send"
                                    popupView.sendMsg.setBackgroundColor(R.color.blue)
                                }

                            })
                    }
                }
            }
            ImagePicker.RESULT_ERROR -> {
                //            Toast.makeText(this, ImagePicker.getError(data).toString(), Toast.LENGTH_SHORT).show()
                Log.d("Upload Error", ImagePicker.getError(data))
            }
            else -> {

//                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChatBinding.inflate(inflater, container, false)
        popupView = CreateMessageBinding.inflate(inflater, container, false)

////        firebase auth
//        firebaseAuth = FirebaseAuth.getInstance()

        val newChat = binding.newChat

        val messagesList = ArrayList<MessageRetrieved>()
        val adapter = ChatsRecyclerViewAdapter(messagesList, requireContext())
        val recyclerView = binding.recyclerView
        val layoutManager = LinearLayoutManager(context)

        recyclerView.layoutManager = layoutManager
        val progressbar = binding.progressView

        recyclerView.adapter = adapter
//        progressbar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        val popupWindow = PopupWindow(
            popupView.root,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true
        )


//        view Model for chats functionality i.e handling offline storage with room
        roomViewModel = ViewModelProvider(this)[RoomViewModel::class.java]


//        observing changes in allMessages
        roomViewModel.allMessages.observe(viewLifecycleOwner) { messages ->
            messagesList.clear()
            messagesList.addAll(messages)
            adapter.notifyDataSetChanged()

            recyclerView.visibility = View.VISIBLE
            layoutManager.scrollToPosition(adapter.itemCount - 1)
            if (messages.isEmpty()) {
                binding.noChats.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }







        newChat.setOnClickListener { it ->
            val background = View(requireContext())
            background.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
            val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            binding.root.addView(background, layoutParams)
            background.isFocusable = false
            background.isClickable = true

            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)

            popupView.shareImage.setOnClickListener {
                selectImage()

            }


            popupView.cancel.setOnClickListener {
                popupWindow.dismiss()
            }
            popupView.cancel.setOnClickListener {
                popupWindow.dismiss()
            }
            popupView.sendMsg.setOnClickListener {
                val title = popupView.subjectInput.text.toString()
                val body = popupView.bodyInput.text.toString()
                if (title.isEmpty()) {
                    popupView.subjectInput.error = "Kindly fill this field!"

                }
                if (body.isEmpty()) {
                    popupView.bodyInput.error = "Kindly fill this field!"
                }
                if (body.isNotEmpty() && title.isNotEmpty()) {
                    val message = HashMap<String, String>()
                    popupView.sendMsg.text = "Sending ..."
                    popupView.sendMsg.setBackgroundColor(R.color.dark_gray)
                    message["body"] = body
                    message["title"] = title
                    RetrofitHandler.sendMessage(
                        requireContext(),
                        message,
                        object : MessageInterface {
                            override fun success(message: SentMessage) {
                                val toast = Toast(context)
                                val customToast: View = layoutInflater.inflate(
                                    R.layout.message_sent,
                                    container,
                                    false
                                )
                                toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 40)

                                toast.duration = Toast.LENGTH_SHORT
                                toast.setView(customToast)
                                toast.show()
                                popupView.subjectInput.text.clear()
                                popupView.bodyInput.text.clear()
                                popupView.sendMsg.text = "Send"


                            }

                            @SuppressLint("ResourceAsColor", "SetTextI18n")
                            override fun failure(throwable: Throwable) {
                                Toast.makeText(context, "T ${throwable.message}", Toast.LENGTH_LONG)
                                    .show()
                                popupView.sendMsg.text = "Send"
                                popupView.sendMsg.setBackgroundColor(R.color.blue)
                            }

                            @SuppressLint("ResourceAsColor", "SetTextI18n")
                            override fun errorExists(message: String) {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                popupView.sendMsg.text = "Send"
                                popupView.sendMsg.setBackgroundColor(R.color.blue)
                            }

                        })
                }
            }


        }

        popupWindow.setOnDismissListener {
            binding.root.removeViewAt(binding.root.childCount - 1)
        }


//        configuring pusher
        val options = PusherOptions()
        options.setCluster("mt1")

        options.isUseTLS = true

        val userId = MyPreferences.getItemFromSP(requireContext(), "userId")

        pusher = Pusher("cf1a6d90710b2e4a1f85", options)


        newChat.visibility = View.GONE
        if (isInternetConnected()) {
            newChat.visibility = View.VISIBLE
            pusher = Pusher("cf1a6d90710b2e4a1f85", options)
            RetrofitHandler.getCurrentUser(requireContext(), object : UserInterface {
                override fun success(user: User) {
                    if (user.coparent == null) {
                        progressbar.visibility = View.GONE
                        recyclerView.visibility = View.GONE

                        binding.noCoparent.visibility = View.VISIBLE
                        binding.sendEmail.setOnClickListener {
                            val recipient = binding.inputEmail.text.toString()
                            if (recipient.isEmpty()) {
                                binding.inputEmailLayout.error = "Kindly fill this field"
                            } else {
                                binding.sendEmail.text = "Sending ..."
                                RetrofitHandler.sendEmail(requireContext(), recipient, object :
                                    ResponseBodyInterface {
                                    override fun success(response: ResponseBody) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Email sent successfully!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        binding.inputEmail.text!!.clear()
                                        binding.sendEmail.text = "Send"
                                    }

                                    override fun failure(throwable: Throwable) {
                                        Toast.makeText(
                                            requireContext(),
                                            throwable.message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                        binding.sendEmail.text = "Send"
                                    }

                                    override fun errorExists(message: String) {
                                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                                            .show()
                                        binding.sendEmail.text = "Send"
                                    }
                                })
                            }
                        }
                        binding.userId.text = user.userId
                        binding.copyToClipBoard.setOnClickListener {
                            val activity = activity as AppCompatActivity
                            val clipboardManager =
                                activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = ClipData.newPlainText("text", user.userId)
                            clipboardManager.setPrimaryClip(clipData)
                            Toast.makeText(
                                requireContext(),
                                "Text copied to clipboard",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        newChat.visibility = View.VISIBLE
                        popupView.recipientName.text = "${user.firstName} ${user.lastName}"
                        roomViewModel.addUser(
                            User2(
                                user.userId,
                                user.firstName,
                                user.lastName,
                                user.email,
                                user.phone,
                                user.coparent.userId,
                                user.profilePic,
                                user.createdAt
                            )
                        )
                        roomViewModel.addUser(
                            User2(
                                user.coparent.userId,
                                user.coparent.firstName,
                                user.coparent.lastName,
                                user.coparent.email,
                                user.coparent.phone,
                                user.coparent.coparent,
                                user.coparent.profilePic,
                                user.coparent.createdAt
                            )
                        )
                    }
                }

                override fun failure(throwable: Throwable) {
                    Toast.makeText(requireContext(), throwable.message, Toast.LENGTH_SHORT).show()
                }

                override fun errorExists(message: String) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }

            })
            pusher.connect(object : ConnectionEventListener {
                override fun onConnectionStateChange(change: ConnectionStateChange) {
                    Log.i(
                        "Pusher", "State changed from " + change.previousState +
                                " to " + change.currentState
                    )
                }

                override fun onError(message: String, code: String, e: Exception) {
                    Log.i(
                        "Pusher", "There was a problem connecting! code: $code message: $message"
                    )
                }
            }, ConnectionState.ALL)
            val channel1 = pusher.subscribe(userId)
            channel1.bind("message") { event ->
                Log.i("Pusher", "Received event with data: $event")
                try {
                    val jsonObject = JSONObject(event.data)
                    val gson = Gson()
                    val msgObject = jsonObject.getJSONObject("message")
                    val senderJson = msgObject.getJSONObject("sender")
                    val recipientJson = msgObject.getJSONObject("recipient")
                    val message = gson.fromJson(jsonObject.toString(), MessageToSave::class.java)
                    message.senderId = senderJson.getString("_id")
                    message.recipientId = recipientJson.getString("_id")
                    message.createdAt = msgObject.getString("createdAt")
                    message.title = msgObject.getString("title")
                    message.body = msgObject.getString("body")
                    message.id = msgObject.getString("_id")
                    if (msgObject.has("image")) {
                        message.image = msgObject.getString("image")
                    }
                    roomViewModel.addMessage(message)
//                messagesList.add(message)

//                adapter.notifyDataSetChanged()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            RetrofitHandler.getMessages(requireContext(), object : MessagesInterface {
                override fun success(messages: List<Message>) {
                    val roomMessages: List<MessageRetrieved> =
                        roomViewModel.allMessages.value ?: ArrayList()
                    val notSaved =
                        messages.filterNot { it1 -> it1._id in roomMessages.map { it.message.id } }

                    val messagesToSave = ArrayList<MessageToSave>()
                    notSaved.forEach { message ->
                        val messageToSave = MessageToSave(
                            message._id,
                            message.title,
                            message.body,
                            message.image,
                            message.sender.userId,
                            message.recipient.userId,
                            message.createdAt
                        )
                        messagesToSave.add(messageToSave)
                    }
                    roomViewModel.addMessages(*messagesToSave.toTypedArray())
                }

                override fun failure(throwable: Throwable) {

                }

                override fun errorExists(message: String) {

                }

            })
        } else {
            Toast.makeText(
                requireContext(),
                "No internet connection!Make sure that you are connected to the internet.",
                Toast.LENGTH_LONG
            ).show()
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        pusher.disconnect()
    }

    private fun selectImage() {

        ImagePicker.with(this)
            .crop()
            .start()

    }

    private fun isInternetConnected(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}

