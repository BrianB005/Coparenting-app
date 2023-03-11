package com.serah.coparenting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.serah.coparenting.databinding.CreateGalleryBinding
import com.serah.coparenting.databinding.FragmentGalleryBinding
import com.serah.coparenting.retrofit.*
import okhttp3.ResponseBody


class GalleryFragment : Fragment() {
    private lateinit var launcher:ActivityResultLauncher<Intent>
    private lateinit var image: String;
    private lateinit var popupView: CreateGalleryBinding;
    private lateinit var popupWindow: PopupWindow

    private lateinit var pusher: Pusher
    private lateinit var images:ArrayList<Uri>
    private lateinit var imagesAdapter:ImagesAdapter
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {

                val imagesToSave=ArrayList<String>()
                if (it.data?.hasExtra(ImagePicker.EXTRA_FILE_PATH)!!) {
                    val uri = it.data?.data!!
                    popupView.selectedImage.visibility=View.VISIBLE
                    popupView.recyclerView.visibility=View.GONE
                    popupView.selectedImage.setImageURI(uri)

                    popupView.create.setOnClickListener {

                        val location=popupView.inputLocation.text.toString()
                        val title=popupView.inputCaption.text.toString()
                        if(title.isEmpty()){
                            popupView.caption.error="Kindly fill this field!"
                        }else{
                            val fileName=System.currentTimeMillis()
                            val storageReference=FirebaseStorage.getInstance().getReference("images/$fileName")
                            popupView.create.text="Creating ..."
                            imagesToSave.add(fileName.toString())
                            val gallery=HashMap<String,Any>()
                            gallery["location"]=location
                            gallery["caption"]=title
                            gallery["images"]=imagesToSave
                            storageReference.putFile(uri).addOnCompleteListener {
                                RetrofitHandler.createGallery(requireContext(),gallery,object:ResponseBodyInterface{
                                    @SuppressLint("InflateParams")
                                    override fun success(response: ResponseBody) {
                                        popupView.create.text="Create"
                                        popupView.inputCaption.text?.clear()
                                        popupView.inputLocation.text?.clear()
                                        popupView.selectedImage.setImageURI(null)
                                        popupView.selectedImage.visibility=View.GONE
                                        val toast = Toast(context)
                                        val customToast: View = layoutInflater.inflate(
                                            R.layout.custom_toast,
                                            null,
                                            )
                                        customToast.findViewById<TextView>(R.id.text).text="Gallery items added successfully!"
                                        toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 40)

                                        toast.duration = Toast.LENGTH_SHORT
                                        toast.setView(customToast)
                                        toast.show()
                                    }

                                    override fun failure(throwable: Throwable) {
                                        popupView.create.text="Create"
                                        Toast.makeText(requireContext(),throwable.message,Toast.LENGTH_SHORT).show()
                                    }

                                    override fun errorExists(message: String) {
                                        popupView.create.text="Create"
                                        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
                                    }

                                })
                            }
                        }
                    }

                } else if (it.data?.hasExtra(ImagePicker.MULTIPLE_FILES_PATH)!!) {
                    val files = ImagePicker.getAllFile(it.data) as ArrayList<Uri>
                    popupView.selectedImage.visibility=View.GONE
                    popupView.recyclerView.visibility=View.VISIBLE
                    if (files.size > 0) {
                        images.addAll(files)
                        imagesAdapter.notifyDataSetChanged()
                        popupView.create.setOnClickListener {
                            val uploadTasks = mutableListOf<UploadTask>()

                            val location=popupView.inputLocation.text.toString()
                            val title=popupView.inputCaption.text.toString()
                            if(title.isEmpty()){
                                popupView.caption.error="Kindly fill this field!"
                            }else {
                                popupView.create.text="Creating ..."

                                imagesToSave.clear()
                                for (fileUri in files) {
                                    val fileName = System.currentTimeMillis()
                                    val storageReference =
                                        FirebaseStorage.getInstance()
                                            .getReference("images/$fileName")
                                    imagesToSave.add(fileName.toString())
                                    val uploadTask = storageReference.putFile(fileUri)
                                    uploadTasks.add(uploadTask)
                                }

                                Tasks.whenAllComplete(uploadTasks)
                                    .addOnSuccessListener {

                                        val gallery=HashMap<String,Any>()
                                        gallery["location"]=location
                                        gallery["caption"]=title
                                        gallery["images"]=imagesToSave
                                        Log.d(TAG, "All files uploaded successfully")
                                        RetrofitHandler.createGallery(requireContext(),gallery,object:ResponseBodyInterface{
                                            @SuppressLint("InflateParams")
                                            override fun success(response: ResponseBody) {
                                                popupView.create.text="Create"
                                                popupView.inputCaption.text?.clear()
                                                popupView.inputLocation.text?.clear()
                                                popupView.recyclerView.visibility=View.GONE
                                                images.clear()
                                                imagesAdapter.notifyDataSetChanged()
                                                val toast = Toast(context)
                                                val customToast: View = layoutInflater.inflate(
                                                    R.layout.custom_toast,
                                                    null,
                                                )
                                                customToast.findViewById<TextView>(R.id.text).text="Gallery items added!"
                                                toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 40)

                                                toast.duration = Toast.LENGTH_SHORT
                                                toast.setView(customToast)
                                                toast.show()
                                            }

                                            override fun failure(throwable: Throwable) {
                                                popupView.create.text="Create"
                                                Toast.makeText(requireContext(),throwable.message,Toast.LENGTH_SHORT).show()
                                            }

                                            override fun errorExists(message: String) {
                                                popupView.create.text="Create"
                                                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
                                            }

                                        })

                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e(TAG, "Error uploading files", exception)
                                        // handle failure here
                                    }
                            }
                        }
                    }
                } else {
                    parseError(it)
                }

            } else {
                parseError(it)
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        val binding=FragmentGalleryBinding.inflate(inflater,container,false);

        popupView=CreateGalleryBinding.inflate(inflater,container,false);
        popupWindow=PopupWindow(popupView.root,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,true);

        val recyclerView=binding.recyclerView
        val memories=ArrayList<Gallery>()

        val adapter = GalleryRecyclerViewAdapter( requireContext(),memories)

        val layoutManager = LinearLayoutManager(context)

        recyclerView.layoutManager = layoutManager
        val progressbar = binding.progressView

        recyclerView.adapter = adapter
        progressbar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        if(isInternetConnected()) {
            RetrofitHandler.getGallery(requireContext(), object : GalleryInterface {
                @SuppressLint("NotifyDataSetChanged")
                override fun success(galleryItems: List<Gallery>) {
                    if (galleryItems.isEmpty()) {
                        binding.noMemories.visibility = View.VISIBLE
                    } else {
                        memories.addAll(galleryItems)
                        adapter.notifyDataSetChanged()
                        binding.noMemories.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                    progressbar.visibility = View.GONE
                }

                override fun failure(throwable: Throwable) {
                    progressbar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    Toast.makeText(context, "T ${throwable.message}", Toast.LENGTH_LONG).show()
                }

                override fun errorExists(message: String) {
                    progressbar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }

            })
        }else{
            progressbar.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "No internet connection!Make sure that you are connected to the internet.", Toast.LENGTH_LONG).show()
        }
        binding.newMemory.setOnClickListener {
            val background = View(requireContext())
            background.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
            popupWindow.showAtLocation(it,Gravity.CENTER,0,0)
            val layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            binding.root.addView(background, layoutParams)

            popupView.cancel.setOnClickListener {
                popupWindow.dismiss()
            }

//            creating recycler_view code for showing selected_images
            val recyclerView1=popupView.recyclerView

            images=ArrayList()
            imagesAdapter=ImagesAdapter(requireContext(),images)
            val layoutManager1 = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerView1.adapter=imagesAdapter
            recyclerView1.layoutManager=layoutManager1

            popupView.shareImage.setOnClickListener {
                selectImage()
            }


        }
        popupWindow.setOnDismissListener {
            binding.root.removeViewAt(binding.root.childCount - 1)
        }
        //    /        configuring pusher
        val options = PusherOptions()
        options.setCluster("mt1")

        options.isUseTLS = true

        val userId = MyPreferences.getItemFromSP(requireContext(), "userId")

        pusher = Pusher("cf1a6d90710b2e4a1f85", options)
        if(isInternetConnected()) {
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

            channel1.bind("gallery") {
                RetrofitHandler.getGallery(requireContext(), object : GalleryInterface {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun success(galleryItems: List<Gallery>) {
                        if (galleryItems.isEmpty()) {
                            binding.noMemories.visibility = View.VISIBLE
                        } else {
                            memories.clear()
                            memories.addAll(galleryItems)
                            adapter.notifyDataSetChanged()
                            binding.noMemories.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                        }
                        progressbar.visibility = View.GONE
                    }

                    override fun failure(throwable: Throwable) {
                        progressbar.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        Toast.makeText(context, "T ${throwable.message}", Toast.LENGTH_LONG).show()
                    }

                    override fun errorExists(message: String) {
                        progressbar.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }

                })
            }

        }else{
            Toast.makeText(requireContext(), "No internet connection!Make sure that you are connected to the internet.", Toast.LENGTH_LONG).show()
        }
        return binding.root
    }
    private fun selectImage() {
        galleryLauncher.launch(
            ImagePicker.with(activity as Activity)
                .crop()
                .galleryOnly()
                .setMultipleAllowed(true)
                .cropFreeStyle()
                .galleryMimeTypes( // no gif images at all
                    mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                )
                .createIntent()
        )

    }


    private fun parseError(activityResult: ActivityResult) {
        if (activityResult.resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(context, ImagePicker.getError(activityResult.data), Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


    private fun isInternetConnected(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }






}