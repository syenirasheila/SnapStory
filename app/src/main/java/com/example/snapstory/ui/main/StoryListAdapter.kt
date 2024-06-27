package com.example.snapstory.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapstory.data.remote.model.StoryItem
import com.example.snapstory.databinding.StoryCardBinding
import com.example.snapstory.helper.formatToString

class StoryListAdapter: PagingDataAdapter<StoryItem, StoryListAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    var onItemClickCallback: OnItemClickCallback? = null


    inner class StoryViewHolder(private val binding: StoryCardBinding): RecyclerView.ViewHolder(binding.root) {
        fun storyData(data: StoryItem) {
            binding.apply {
                root.setOnClickListener {
                    onItemClickCallback?.onItemClicked(data)
                }
                Glide.with(itemView)
                    .load(data.photoUrl)
                    .into(ivStory)
                tvUserName.text = data.name
                tvStoryDate.text = data.createdAt?.formatToString()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = StoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.storyData(story)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryItem)
    }

    fun setOnItemCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem == newItem

            }
        }
    }
}
