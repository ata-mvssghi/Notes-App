package com.example.noteapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.MainActivity
import com.example.noteapp.R
import com.example.noteapp.adapter.RecyclerViewAdapter
import com.example.noteapp.adapter.UpdateNoteInterface
import com.example.noteapp.databinding.FragmentHomeBinding
import com.example.noteapp.room.Note
import com.example.noteapp.viewModel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() ,SearchView.OnQueryTextListener,UpdateNoteInterface{
    var _binding:FragmentHomeBinding?=null
    val binding get()=_binding!!
    lateinit var notesViewModel: NoteViewModel
    lateinit var noteAdapter: RecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentHomeBinding.inflate(inflater,container,false)
        RecyclerViewAdapter.NoteUpdater.updateListner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel
        setUpRecyclerView()
        setHasOptionsMenu(true)
        binding.add.setOnClickListener{
            it.findNavController()
                .navigate(R.id.action_homeFragment_to_newNoteFragment)
        }
        binding.settings.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    fun setUpRecyclerView(){
        noteAdapter= RecyclerViewAdapter()
        binding.recyclerView.layoutManager=StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter=noteAdapter
        activity?.let {
            notesViewModel.getALlNotes().observe(viewLifecycleOwner
            ) { notes ->
                noteAdapter.differ.submitList(notes)
                updateUi(notes)
            }
        }
    }

    private fun updateUi(list:List<Note>) {
        if(list.isEmpty()){
            binding.cardView.visibility=View.VISIBLE
            binding.recyclerView.visibility=View.GONE
        }
        else {
            binding.cardView.visibility=View.GONE
            binding.recyclerView.visibility=View.VISIBLE
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.clear()
        inflater.inflate(R.menu.home_menu, menu)

        val mMenuSearch = menu.findItem(R.id.search).actionView as SearchView
        mMenuSearch.isSubmitButtonEnabled = false
        mMenuSearch.setOnCloseListener {
            Log.i("note","close")
            //this false make the search view disables and gone
            false
        }
        mMenuSearch.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query!=null) {
            searchNote(query)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            searchNote(newText)
        }
        return true
    }
    @SuppressLint("SuspiciousIndentation")
    private fun searchNote(query: String?){
        Log.i("note","the query=$query")
        if(query!=null) {
            val searchQuery = "%$query"
            notesViewModel.searchNote(searchQuery).observe(
                this
            ) { list -> noteAdapter.differ.submitList(list.reversed()) }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun checkOrUncheck(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            notesViewModel.updateNote(note)
        }
    }
}