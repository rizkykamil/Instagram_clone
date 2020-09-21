package com.example.instagram.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagram.R
import com.example.instagram.model.Post

class MyImagesAdapter(private val mContext: Context,private val mPost : List<Post> ):
    RecyclerView.Adapter<MyImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyImageViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.images_list_item_layout,parent,false)
        return MyImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: MyImageViewHolder, position: Int) {
        var mPost = mPost[position]

        Glide.with(mContext).load(mPost.getPostImage()).into(holder.postImageGrid)
    }

}

class MyImageViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {

    var postImageGrid : ImageView= itemView.findViewById(R.id.post_image_grid_profile)
}
