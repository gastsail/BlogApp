package com.example.blogapp.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.R
import com.example.blogapp.core.BaseViewHolder
import com.example.blogapp.core.TimeUtils
import com.example.blogapp.data.model.Post
import com.example.blogapp.databinding.PostItemViewBinding

class HomeScreenAdapter(private val onPostClickListener: OnPostClickListener) :
    RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var postClickListener: OnPostClickListener? = null
    private var postList: List<Post> = emptyList()

    init {
        postClickListener = onPostClickListener
    }

    fun setPostData(postList: List<Post>) {
        this.postList = postList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = PostItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeScreenViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is HomeScreenViewHolder -> holder.bind(postList[position])
        }
    }

    override fun getItemCount(): Int = postList.size

    private inner class HomeScreenViewHolder(
        val binding: PostItemViewBinding,
        val context: Context
    ) : BaseViewHolder<Post>(binding.root) {
        override fun bind(item: Post) {
            setupProfileInfo(item)
            addPostTimeStamp(item)
            setupPostImage(item)
            setupPostDescription(item)
            tintHearthIcon(item)
            setupLikeCount(item)
            setLikeClickAction(item)
        }

        private fun setupProfileInfo(item: Post) {
            Glide.with(context).load(item.profile_picture).centerCrop().into(binding.profilePicture)
            binding.profileName.text = item.profile_name
        }

        private fun addPostTimeStamp(item: Post) {
            val createdAt = (item.created_at?.time?.div(1000L))?.let {
                TimeUtils.getTimeAgo(it.toInt())
            }
            binding.postTimestamp.text = createdAt
        }

        private fun setupPostImage(item: Post) {
            Glide.with(context).load(item.post_image).centerCrop().into(binding.postImage)
        }

        private fun setupPostDescription(item: Post) {
            if (item.post_description.isEmpty()) {
                binding.postDescription.visibility = View.GONE
            } else {
                binding.postDescription.text = item.post_description
            }
        }

        private fun tintHearthIcon(item: Post) {
            if(!item.liked) {
                binding.likeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_outline_heart))
                binding.likeBtn.setColorFilter(ContextCompat.getColor(context, R.color.black))
            } else {
                binding.likeBtn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_filled_heart))
                binding.likeBtn.setColorFilter(ContextCompat.getColor(context, R.color.red_like))
            }
        }

        private fun setupLikeCount(item: Post) {
            if(item.likes > 0) {
                binding.likeCount.visibility = View.VISIBLE
                binding.likeCount.text = "${item.likes} likes"
            } else {
                binding.likeCount.visibility = View.GONE
            }
        }

        private fun setLikeClickAction(item: Post) {
            binding.likeBtn.setOnClickListener {
                if(item.liked) item.apply { liked = false } else item.apply { liked = true }
                tintHearthIcon(item)
                postClickListener?.onLikeButtonClick(item, item.liked)
            }
        }
    }
}



interface OnPostClickListener {
    fun onLikeButtonClick(post: Post, liked: Boolean)
}