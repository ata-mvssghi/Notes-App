package com.example.noteapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.noteapp.MainActivity
import com.example.noteapp.R
import com.example.noteapp.adapter.RecyclerViewAdapter
import com.example.noteapp.databinding.FragmentNewNoteBinding
import com.example.noteapp.room.Note
import com.example.noteapp.viewModel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    private var _binding : FragmentNewNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel : NoteViewModel
    private lateinit var noteAdapter: RecyclerViewAdapter

    private lateinit var mView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel
        mView = view

    }


    @SuppressLint("SuspiciousIndentation")
    private fun savenote(view: View){
        val noteTitle = binding.subject.text.toString().trim()
        val noteBody = binding.discription.text.toString().trim()

        if (noteTitle.isNotEmpty()){
            val note = Note(0,noteTitle, noteBody)
            lifecycleScope.launch {
                // Dispatch the database operation to the Default dispatcher
                withContext(Dispatchers.Default) {
                    notesViewModel.insertNote(note)
                }
            }

            Toast.makeText(mView.context,
                "Note Saved Successfully",
                Toast.LENGTH_LONG).show()
            view.findNavController().navigate(R.id.action_newNoteFragment_to_homeFragment)
        }
        else{
            Toast.makeText(
                mView.context,
                "Please enter note Title",
                Toast.LENGTH_LONG).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
       // menu.clear()
        inflater.inflate(R.menu.new_note, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> {
                savenote(mView)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}