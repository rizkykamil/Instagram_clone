package com.example.instagram.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.EditProfileActivity
import com.example.instagram.R
import com.example.instagram.adapter.MyImagesAdapter
import com.example.instagram.adapter.SearchUserAdapter
import com.example.instagram.model.Post
import com.example.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.list_item_search.*

class ProfileFragment : Fragment() {

    private lateinit var profileId:String
    private lateinit var firebaseUser:FirebaseUser

    private var postListGrid : MutableList<Post>? = null
    private var myImagesAdapter : MyImagesAdapter? = null
    private var myRecyclerImage : RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val viewProfile = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        myRecyclerImage = viewProfile.findViewById(R.id.recycler_upload)
        myRecyclerImage!!.setHasFixedSize(true)
        val linearLayoutManager = GridLayoutManager(context,3)
        myRecyclerImage!!.layoutManager =linearLayoutManager

        postListGrid = ArrayList()
        myImagesAdapter = context?.let { MyImagesAdapter(it,postListGrid as ArrayList<Post>) }
        myRecyclerImage!!.adapter = myImagesAdapter

        val pref = context?.getSharedPreferences("Press",Context.MODE_PRIVATE)
        if (pref!= null){
            this.profileId = pref.getString("profileId","none")!!
        }
        if (profileId == firebaseUser.uid){
            view?.btn_edit_profile?.text = "EditProfile"
        }
        else if (profileId == firebaseUser.uid){
            cekFollowandFollowingButtonStatus()

        }

        viewProfile.btn_edit_profile.setOnClickListener {
            startActivity(Intent(context,EditProfileActivity::class.java))
        }
        getFollowers()
        getFollowing()
        userInfo()
        myPost()
        return viewProfile

    }

    private fun myPost() {
        val postRef = FirebaseDatabase.getInstance().reference
            .child("Posts")

        postRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    (postListGrid as ArrayList<Post>).clear()

                    for (snapshot in p0.children){
                        val post = snapshot.getValue(Post::class.java)
                        if (post!!.getPublisher() == profileId)
                        {
                            postListGrid!!.reverse()
                            myImagesAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }

        })
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("users").child(profileId)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user?.getImage()).into(profile_pic)
                    view?.Fullname_Profile?.text = user?.getFullname()
                    view?.Username_profile?.text = user?.getFullname()
                    view ?.Bio_profile?.text = user?. getBio()

                }
            }

        })
    }
    private fun getFollowing() {
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("follow").child(profileId)
            .child("following")
        followingRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    view?.total_following?.text = snapshot.childrenCount.toString()
                }
            }

        })


    }private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("follow").child(profileId)
            .child("followers")

        followersRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    view?.total_followers?.text = snapshot.childrenCount.toString()
                }
            }

        })
    }


    private fun cekFollowandFollowingButtonStatus(){

        val followingRef = firebaseUser.uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }
        if (followingRef != null){
            followingRef.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        view?.btn_edit_profile?.text = "following"
                    } else{
                        view?.btn_edit_profile?.text = "follow"
                    }
                }

            })

        }
    }


    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

}



