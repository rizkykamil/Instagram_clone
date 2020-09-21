package com.example.instagram.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.model.Comment
import com.example.instagram.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter (private val mContent : Context,private val mcomment: MutableList<Comment>):
    RecyclerView.Adapter<CommentsViewHolder>() {

    private var firebaseUser: FirebaseUser? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val view = LayoutInflater.from(mContent).inflate(R.layout.list_comment_layout,parent,false)

        return CommentsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mcomment.size
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {

        val comment = mcomment[position]

        holder.comment_nan.text = comment.getCommnet()

        getUserInfo(holder.ImageProfileComment,holder.userNameComment,comment.getPublisher())


    }
    private fun getUserInfo(
        imageProfileComment: CircleImageView,
        userNameComment:TextView,
        publisher:String){
        val usserRef = FirebaseDatabase.getInstance().reference
            .child("user").child(publisher)

        usserRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                 if (snapshot.exists()){
                     val user = snapshot.getValue(User::class.java)

                     Picasso.get().load(user!!.getImage()).into(imageProfileComment)
                     userNameComment.text = user?.getUsername()
                 }

            }

        })
    }
}

class CommentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    var ImageProfileComment : CircleImageView = itemView.findViewById(R.id.gbr_profile_comment)
    var userNameComment : TextView = itemView.findViewById(R.id.username_comment)
    var comment_nan  : TextView = itemView.findViewById(R.id.comment_comment)

}
