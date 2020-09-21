package com.example.instagram

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlin.coroutines.Continuation


class AddPostActivity : AppCompatActivity() {
        private var storagePicture : StorageReference? = null
        private var myUrl = ""
        private var imageUri : Uri? = null


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_add_post)

            storagePicture = FirebaseStorage.getInstance().reference.child("picture Posts")

            cancel_ADDPOST.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
            }

            btn_save_ADDPOST.setOnClickListener {
                CropImage.activity()
                    .setAspectRatio(2, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this)
                uploadImagePost()
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
                val result = CropImage.getActivityResult(data)
                imageUri = result.uri
                img_ADDPOST.setImageURI(imageUri)
            }
        }

        private fun uploadImagePost() {
            when {
                imageUri == null -> Toast.makeText(this,"must be filled",Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(description_ADDPOST.text.toString()) -> Toast.makeText(this, "must be filled",Toast.LENGTH_SHORT).show()

                else -> {
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("posting")
                    progressDialog.setMessage("please wait")
                    progressDialog.show()

                    val fileRef = storagePicture!!.child(System.currentTimeMillis().toString()+ ".jpg")

                    val uploadImage : StorageTask<*>
                    uploadImage = fileRef.putFile(imageUri!!)

                    uploadImage.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful){
                            task.exception.let {
//                            throw it!!
                            }
                        }
                        return@Continuation fileRef.downloadUrl
                    }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful){
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
                            val postId = postRef.push().key
                            val postMap = HashMap<String,Any>()

                            postMap["description"] = description_ADDPOST.text.toString()
                            postMap["postId"]    = postId!!
                            postMap["publisher"]    = FirebaseAuth.getInstance().currentUser!!.uid
                            postMap["postImage"]    = myUrl

                            postRef.child(postId).updateChildren(postMap)

                            Toast.makeText(this,"Info has been updated", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                            progressDialog.dismiss()
                        }
                    })
                }
            }

        }
        }
    }