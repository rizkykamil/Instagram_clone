package com.example.instagram

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        text_daftarakun.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        btn_login.setOnClickListener {
            loginUser()
        }

    }

    private fun loginUser() {

        val email = edt_email_login.text.toString()
        val password = edt_Password_Login.text.toString()

        when {
            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "fullname is requred",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "username is requred",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "password is requred",
                Toast.LENGTH_SHORT
            ).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("login")
                progressDialog.setMessage("Please Wait ......")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            progressDialog.dismiss()
                            val pergi = Intent(this,MainActivity::class.java)
                            startActivity(pergi)
                            finish()

                        } else {
                            val message = task.exception.toString()
                            Toast.makeText(this, "error: $message", Toast.LENGTH_SHORT)
                            FirebaseAuth.getInstance().signOut()
                            progressDialog.dismiss()
                        }
                    }
            }

        }

    }
    override fun onStart(){
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser !=null){
            val pergi = Intent(this,MainActivity::class.java)
            pergi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(pergi)
            finish()
        }

    }
}