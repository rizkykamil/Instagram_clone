package com.example.instagram

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_register.setOnClickListener {
            createAkun()
        }
        text_loginakun.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))

        }
    }

    private fun createAkun() {

        val fullname = edt_fullName_register.text.toString()
        val username = edt_username_register.text.toString()
        val email = edt_email_register.text.toString()
        val password = edt_password_register.text.toString()

        when {
            TextUtils.isEmpty(fullname) -> Toast.makeText(
                this,
                "fullname is requred",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(username) -> Toast.makeText(
                this,
                "username is requred",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "email is requred", Toast.LENGTH_SHORT)
                .show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "password is requred",
                Toast.LENGTH_SHORT
            ).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Register")
                progressDialog.setMessage("Please Wait ......")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            saveUserInfo(fullname, username, email, progressDialog)
                        }else{
                            val message = task.exception!!.toString()
                            Toast.makeText(this,"Error: $message",Toast.LENGTH_SHORT).show()

                            mAuth.signOut()
                            progressDialog.dismiss()
                        }


                        Log.d("test" ,"cek")
                    }
            }
        }
    }

    private fun saveUserInfo(fullname: String, username: String, email: String, progressDialog: ProgressDialog
    ) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
        val userMap = HashMap<String, Any>()

        userMap["uid"] = currentUserID
        userMap["fullbneme"] = fullname.toLowerCase()
        userMap["username"] = username.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "HAY IM GO                     `OD"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/instagram-f5f6f.appspot.com/o/defultImage%2FJepretan%20Layar%202020-08-23%20pukul%2019.11.28.png?alt=media&token=5ca28d69-4f32-4ea2-9ad6-85553ca2d1b1"
        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "akun anda sudah di buat", Toast.LENGTH_SHORT).show()

                    val goToMain = Intent(this, MainActivity::class.java)
                    goToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(goToMain)
                    finish()
                } else {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "error$message", Toast.LENGTH_SHORT)
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }

            }
    }
}