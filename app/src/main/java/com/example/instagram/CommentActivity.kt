package com.example.instagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagram.adapter.CommentAdapter
import com.example.instagram.adapter.SearchUserAdapter
import com.example.instagram.model.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.list_post_layout.*

class CommentActivity : AppCompatActivity() {

    private var posId= ""
    private var publisherId= ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter : CommentAdapter? = null
    private var commentListdata:MutableList<Comment>? = null
    private var recyclerView: RecyclerView? = null

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val intent = intent
        posId = intent.getStringExtra("postId")
        publisherId= intent.getStringExtra("publisherId")
        firebaseUser = FirebaseAuth.getInstance().currentUser

        recyclerView = findViewById(R.id.recyclerview_comments)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout= true
        recyclerView?.layoutManager = linearLayoutManager

        commentAdapter= CommentAdapter(this,commentListdata as ArrayList<Comment>)
        commentListdata = ArrayList()
        recyclerView?.adapter = commentAdapter


        userInfo()
        getPostImageComment()
        readComment()


        txt_post_comment.setOnClickListener{
            if (edt_add_comment.text.toString()==""){
                Toast.makeText(this,"tolong di si",Toast.LENGTH_SHORT).show()
            }else{
                addcomment()
            }
        }
    }

    private fun getPostImageComment() {
        val postCommentRef = FirebaseDatabase.getInstance().reference
            .child("posts").child(posId).child("postimage")

        postCommentRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val image = snapshot.value.toString()

                    Glide.with(this@CommentActivity).load(image).into(post_image_comment)

                }

            }

        })
    }

    private fun readComment() {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("Comment").child(posId)

        commentRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    commentListdata!!.clear()

                    for (s in snapshot.children){
                        val comment = snapshot.getValue(Comment::class.java)

                        commentListdata!!.add(comment!!)
                    }
                    commentAdapter!!.notifyDataSetChanged()
                }
            }

        })
    }

    private fun addcomment() {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("Comment").child(posId)
        val commentsMap =
            HashMap<String,Any>()
        commentsMap["comment"] = txt_post_comment.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid

        commentRef.push().setValue(commentsMap)
//        txt_post_comment!!.text.clear()



    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("user").child(firebaseUser!!.uid)


        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){

                    //maggil gambar yang ada di comment

                    val user = snapshot.getValue(SearchUserAdapter::class.java)

                    Glide.with(this@CommentActivity).load(user).into(profile_image_comment)
                }
            }

        })
    }
}