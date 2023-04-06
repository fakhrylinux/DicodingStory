package me.fakhry.dicodingstory.ui.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import me.fakhry.dicodingstory.databinding.StoryItemBinding
import me.fakhry.dicodingstory.network.model.ListStoryItem

class StoryAdapter(private val listStories: ArrayList<ListStoryItem>) :
    RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    class ViewHolder(var binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listStories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = listStories[position].name
        val createdAt = listStories[position].createdAt
        val photoUrl = listStories[position].photoUrl
        val description = listStories[position].description
        holder.binding.tvName.text = name
        holder.binding.tvCreatedAt.text = createdAt
        holder.binding.ivPost.load(photoUrl)
        holder.binding.tvCaption.text = description
    }

    fun setData(list: List<ListStoryItem>) {
        listStories.addAll(list)
        notifyDataSetChanged()
    }
}