package com.example.instagram

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.instagram.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private var cekInfoProfile = ""
    private var myUrl = ""
    private var imageUri : Uri? = null
    private var storageProfilePictureRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePictureRef = FirebaseStorage.getInstance().reference.child("profile picture")


        logout_btn_setProfile.setOnClickListener {

            FirebaseAuth.getInstance().signOut()

            val pergi = Intent(this@EditProfileActivity, LoginActivity::class.java)
            pergi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(pergi)
            finish()
        }

        button_ganti_gambar.setOnClickListener {
            cekInfoProfile = "clicked"

            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@EditProfileActivity)
        }

        confirm_change.setOnClickListener {
            if (cekInfoProfile == "clicked"){
                uploadImageAndUpdateInfo()
            }else{
                updateUserInfoOnly()
            }
        }
        userInfo()
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().getReference()
            .child("users").child(firebaseUser.uid)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profile_edit)
                    set_username_profile_edit.setText(user.getUsername())
                    set_fullname_profile_edit.setText(user.getFullname())
                    edt_change_bio.setText(user.getBio())
                }
            }

        })
    }

    private fun updateUserInfoOnly() {
    }

    private fun uploadImageAndUpdateInfo() {
        when {
            imageUri == null -> Toast.makeText(this, "please select image", Toast.LENGTH_LONG)
                .show()

            TextUtils.isEmpty(set_fullname_profile_edit.text.toString()) -> {
                Toast.makeText(this, "Please Dont be empty...", Toast.LENGTH_LONG).show()
            }
            set_username_profile_edit.text.toString() == "" -> {
                Toast.makeText(this, "Please Dont be empty...", Toast.LENGTH_LONG).show()
            }
            edt_change_bio.text.toString() == "" -> {
                Toast.makeText(this, "Please Dont be empty...", Toast.LENGTH_LONG).show()
            }
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Accont Setting")
                progressDialog.setMessage("tunggu bentar napa,kira lagi apdetnih kalo lama ya maap internet lu jelek berarti..")
                progressDialog.show()

                val fileRef = storageProfilePictureRef!!.child(firebaseUser!!.uid + ".jpg")

                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception.let {
                            throw it!!
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()


                        val userRef = FirebaseDatabase.getInstance().reference
                            .child("users")
                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = set_fullname_profile_edit.text.toString().toLowerCase()
                        userMap["username"] = set_username_profile_edit.text.toString().toLowerCase()
                        userMap["bio"] = edt_change_bio.text.toString().toLowerCase()

                        userRef.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, "Info Profile has been update", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }else{
                        progressDialog.dismiss()
                    }

                }

            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!= null){

            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_edit.setImageURI(imageUri)
        }
    }

}