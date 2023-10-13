package com.example.noteapp.adapter

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.noteapp.databinding.NoteLayoutBinding
import com.example.noteapp.fragments.HomeFragmentDirections
import com.example.noteapp.room.Note
import com.example.noteapp.room.Priority


class RecyclerViewAdapter:RecyclerView.Adapter<RecyclerViewAdapter.NoteViewHolder>() {
    companion object NoteUpdater{
        var updateListner : UpdateNoteInterface? =null
    }
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
       holder.binding.tvNoteBody.text=currentNote.noteBody
        Log.i("note","discription=${currentNote.noteBody}")
        holder.binding.tvNoteTitle.text=currentNote.noteTitle
        var color = 1
        if(currentNote.priority==Priority.HIGH)
            color = Color.argb(255, 255, 0, 0)
        else if(currentNote.priority==Priority.MEDIUM)
            color = Color.argb(255, 0, 255, 0)
        else if(currentNote.priority==Priority.LOW)
            color = Color.argb(255, 255, 255, 0)
        holder.binding.Done.isChecked = currentNote.isChecked ?: false
        if(currentNote.isChecked == true){
            holder.binding.tvNoteTitle.paintFlags = holder.binding.tvNoteTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.tvNoteBody.paintFlags = holder.binding.tvNoteBody.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        else{
            holder.binding.tvNoteTitle.paintFlags= holder.binding.tvNoteTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.binding.tvNoteBody.paintFlags= holder.binding.tvNoteBody.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        holder.binding.ibColor.setBackgroundColor(color)
        holder.binding.Done.setOnClickListener{
           if(holder.binding.Done.isChecked){
               currentNote.isChecked = true
               holder.binding.tvNoteTitle.paintFlags = holder.binding.tvNoteTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
               holder.binding.tvNoteBody.paintFlags = holder.binding.tvNoteBody.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
               updateListner?.checkOrUncheck(currentNote)
           }
            else{
                currentNote.isChecked = false
               holder.binding.tvNoteTitle.paintFlags= holder.binding.tvNoteTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
               holder.binding.tvNoteBody.paintFlags= holder.binding.tvNoteBody.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
               updateListner?.checkOrUncheck(currentNote)
           }
        }
        holder.itemView.setOnClickListener {
            val direction=HomeFragmentDirections.
            actionHomeFragmentToUpdateNoteFragment(currentNote)
            it.findNavController().navigate(direction)
        }
    }
}
interface UpdateNoteInterface{
    fun checkOrUncheck(note :Note)
}