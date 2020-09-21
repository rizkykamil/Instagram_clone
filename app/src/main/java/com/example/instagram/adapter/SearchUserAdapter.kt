package com.example.instagram.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.fragment.ProfileFragment
import com.example.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class SearchUserAdapter(private var mContext: Context, private val mUser:List<User>, private var isFragment: Boolean = false ) :
    RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder>(){
    private val firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_item_search,parent,false)

        return SearchUserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {

        val user = mUser[position]
        holder.username.text =  mUser.get(position).getUsername()
        holder.fullname.text = mUser.get(position).getFullname()

        Picasso.get()
            .load(user.getImage())
            .into(holder.image)

        cekfollowstatus(user.getUID(),holder.buttonFollow)

        holder.itemView.setOnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId",user.getUID())
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.search_fragment_icon, ProfileFragment()).commit()
        }

        holder.buttonFollow.setOnClickListener {
            if (holder.buttonFollow.text.toString() == "follow"){
                firebaseUser?.uid.let{it ->
                    FirebaseDatabase.getInstance().reference
                        .child("follow").child(it.toString())
                        .child("following").child(user.getUID())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                firebaseUser?.uid.let {it ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("follow").child(user.getUID())
                                        .child("followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener {
                                            if (task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            } else{
                firebaseUser?.uid.let { it ->
                    FirebaseDatabase.getInstance().reference
                        .child("follow").child(it.toString())
                        .child("following").child(user.getUID())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                firebaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("follow").child(user.getUID())
                                        .child("followers").child(it.toString())
                                        .removeValue().addOnCompleteListener { task->
                                            if (task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            }

        }
    }

    private fun cekfollowstatus(uid: String, buttonFollow: Button) {
        val followingref = firebaseUser?.uid.let {it->

            FirebaseDatabase.getInstance().reference
                .child("follow").child(it.toString())
                .child("following")
        }
        followingref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(uid).exists()){

                    buttonFollow.text = "following"
                }else{
                    buttonFollow.text = "follow"
                }
            }

        })
    }

    inner class SearchUserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val username : TextView = itemView.findViewById(R.id.username_User_Item_Search)
        val fullname : TextView = itemView.findViewById(R.id.set_Fullname_Search)
        val buttonFollow : Button = itemView.findViewById(R.id.button_List_Search)
        val image : ImageView = itemView.findViewById(R.id.img_search_profile)
    }

}