package com.example.noteapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteapp.MainActivity
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentUpdateNoteBinding
import com.example.noteapp.room.Note
import com.example.noteapp.viewModel.NoteViewModel
import com.example.noteapp.fragments.UpdateNoteFragmentArgs
import com.example.noteapp.room.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateNoteFragment : Fragment(R.layout.fragment_update_note) {

    private var _binding : FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel : NoteViewModel

    private lateinit var currentNote : Note

    lateinit var priority: Priority
    // Since the Update Note Fragment contains arguments in nav_graph
    private val args: UpdateNoteFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateNoteBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note

        priority = currentNote.priority!!

        binding.updateSubject.setText(currentNote.noteTitle)
        binding.discription.setText(currentNote.noteBody)
        val radioGroup = binding.radioGroupUpdate

        when(currentNote.priority) {
            Priority.HIGH->{
                radioGroup.check(R.id.radioButtonHighU)
            }
            Priority.MEDIUM ->{
                radioGroup.check(R.id.radioButtonMediumU)
            }
            Priority.LOW ->{
                radioGroup.check(R.id.radioButtonLowU)
            }

            else -> {radioGroup.check(R.id.radioButtonMediumU)}
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonHighU -> {
                    priority = Priority.HIGH
                }
                R.id.radioButtonMediumU -> {
                    priority = Priority.MEDIUM
                }
                R.id.radioButtonLowU -> {
                    priority = Priority.LOW
                }
            }
        }
        // if the user update the note
        binding.floatingActionButton2.setOnClickListener{
            val title = binding.updateSubject.text.toString().trim()
            val body = binding.discription.text.toString().trim()
            if (title.isNotEmpty()){
                val note = Note(currentNote.id,title, body ,priority)
                lifecycleScope.launch {
                    withContext(Dispatchers.Default){
                        notesViewModel.updateNote(note)
                        Log.i("note","note updated! and the priorty = $priority")
                    }
                }
                view.findNavController().popBackStack()
            }else{
                Toast.makeText(
                    context,
                    "Please enter note Title",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteNote(){
        AlertDialog.Builder(activity).apply {

            setTitle("Delete Note")
            setMessage("You want to delete this Note?")
            setPositiveButton("Delete"){_,_ ->
                notesViewModel.deleteNote(currentNote)

                view?.findNavController()?.popBackStack()
            }
            setNegativeButton("Cancel", null)
        }.create().show()


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.update_note,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_menu -> {
                deleteNote()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}