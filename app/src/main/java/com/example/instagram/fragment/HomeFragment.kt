package com.example.instagram.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.R
import com.example.instagram.adapter.PostAdapter
import com.example.instagram.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var postAdapter : PostAdapter? = null
    private var postList : MutableList<Post>? = null
    private var followingList : MutableList<Post>? = null
    private var recyclerViewHome : RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewhome =  inflater.inflate(R.layout.fragment_home, container, false)
        var recyclerViewHome: RecyclerView? = null

        recyclerViewHome = viewhome.findViewById(R.id.recycler_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerViewHome?.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context.let { it?.let { it1 ->PostAdapter (it1 ,postList as ArrayList<Post>)} }
        recyclerViewHome.adapter = postAdapter

        cekFollowing()

        return viewhome
    }

    private fun cekFollowing() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("foloowubg")

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    (followingList as ArrayList<String>).clear()

                    for (s in p0.children){
                        s.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    getPost()
                }
            }

        })
    }

    private fun getPost() {
        val posts = FirebaseDatabase.getInstance().reference.child("Posts")

        posts.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList?.clear()
                for (s in snapshot.children) {
                    val post = s.getValue(Post::class.java)
                    for (id in (followingList as ArrayList<String>)) {
                        if (post!!.getPublisher() == id) {
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}