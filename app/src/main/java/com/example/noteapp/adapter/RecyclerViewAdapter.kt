package com.example.noteapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.databinding.NoteLayoutBinding
import com.example.noteapp.room.Note
import com.example.noteapp.fragments.HomeFragmentDirections
import kotlin.random.Random

class RecyclerViewAdapter:RecyclerView.Adapter<RecyclerViewAdapter.NoteViewHolder>() {

    inner class NoteViewHolder( val binding:NoteLayoutBinding):RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.noteTitle == newItem.noteTitle &&
                    oldItem.noteBody == newItem.noteBody
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteLayoutBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
      return  differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote=differ.currentList[position]
       holder.binding.tvNoteBody.text=currentNote.noteTitle
        holder.binding.tvNoteTitle.text=currentNote.noteBody
        val random= Random.Default
        val color= Color.argb(255,random.nextInt(256),random.nextInt(256),random.nextInt(256))
        holder.binding.ibColor.setBackgroundColor(color)
        holder.itemView.setOnClickListener {
            val direction=HomeFragmentDirections.
            actionHomeFragmentToUpdateNoteFragment(currentNote)
            it.findNavController().navigate(direction)
        }
    }
}