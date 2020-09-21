package com.example.instagram.adapter

import android.content.Context
import android.content.Intent
import android.view.ActionProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.CommentActivity
import com.example.instagram.MainActivity
import com.example.instagram.R
import com.example.instagram.model.Post
import com.example.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val mContext: Context, private val mPost: List<Post>): RecyclerView.Adapter<MyViewHolder>() {
    private var firebaseUser : FirebaseUser? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_post_layout,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val myPost = mPost[position]
        Picasso.get()
            .load(myPost.getPostImage())
            .into(holder.imagePost)
        if (myPost.getDescription().equals("")) {
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = myPost.getDescription()
        }

        publisherInfo(
            holder.profileImage,
            holder.username,
            holder.publisher_name,
            myPost.getPublisher()
        )

        numberOfLike(holder.sum_Likes,myPost.getPostId())
        ngeLike(myPost.getPostId(), holder.like)
        getTotalcComment(holder.sum_Comment,myPost.getPostId())
        holder.comments.setOnClickListener {
            val intentComment =  Intent(mContext,CommentActivity::class.java)
            intentComment.putExtra("postId",myPost.getPostId())
            intentComment.putExtra("poblisherId",myPost.getPublisher())
            mContext.startActivity(intentComment)
        }
        holder.like.setOnClickListener {
            if (holder.like.tag == "Like") {
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(myPost.getPostId()).child(firebaseUser!!.uid)
                    .setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference
                    .child("likes").child(myPost.getPostId()).child(firebaseUser!!.uid)
                    .removeValue()
                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }
    }

    private fun getTotalcComment(comment: TextView, postid: String) {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("comments")

        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    comment.text = snapshot.childrenCount.toString()+ " Comment"
                }
            }

        })

    }

    private fun numberOfLike(likes: TextView, postid: String) {
        val likeRef = FirebaseDatabase.getInstance().reference
            .child("likes").child(postid)

        likeRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    likes.text = snapshot.childrenCount.toString() + " Likes"
                }
            }

        })

    }

    private fun ngeLike(postid: String, likeButton: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likeRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        likeRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()) {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag = "Liked"
                }else{
                    likeButton.setImageResource(R.drawable.heart)
                    likeButton.tag = "like"
                }
            }

        })
    }

    private fun publisherInfo(profileImage: CircleImageView, username: TextView, publisherName: TextView, publisher: String){
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(publisher)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>(User::class.java)

                Picasso.get()
                    .load(user?.getImage())
                    .into(profileImage)
                username.text = user?.getUsername()
                publisherName.text = user?.getFullname()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun getItemCount(): Int {
        return mPost.size
    }
}

class MyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView) {
    //    profile
    var profileImage : CircleImageView = itemView.findViewById(R.id.profile_home)

    // Image
    var username : TextView = itemView.findViewById(R.id.username_home)
    var imagePost : ImageView = itemView.findViewById(R.id.imageView4)

    //yang di bawah image
    var like : ImageView = itemView.findViewById(R.id.img_heart_home)
    var comments : ImageView = itemView.findViewById(R.id.img_comment_home)
    var share : ImageView = itemView.findViewById(R.id.img_send_home)
    var save : ImageView = itemView.findViewById(R.id.img_save_home)

    //    comments
    var sum_Likes : TextView = itemView.findViewById(R.id.jumlah_likes_home)
    var publisher_name : TextView = itemView.findViewById(R.id.publisher_home)
    var description : TextView = itemView.findViewById(R.id.description_home)
    var sum_Comment : TextView = itemView.findViewById(R.id.jumlah_comments_home)
}
