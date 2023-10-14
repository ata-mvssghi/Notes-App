package com.example.noteapp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
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
    lateinit var sp :SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentHomeBinding.inflate(inflater,container,false)
         sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
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
        val orderInPreference  = sp.getString("order","By Date")
        Log.i("note","setup Recyceler and the order is : $orderInPreference")
        activity?.let {
            notesViewModel.getALlNotes().observe(viewLifecycleOwner
            ) { notes ->
                var newList : List<Note> = notes
                if(orderInPreference.equals("By Date")){
                    newList = notes
                }
                else if(orderInPreference.equals("Ascending Importance")){
                     newList = notes.sortedBy { it.priority?.value }
                }
                else{
                    newList = notes.sortedByDescending { it.priority?.value }
                }
                    noteAdapter.differ.submitList(newList)
                    updateUi(newList)

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
            //this false makes the search view disable and gone
            false
        }
        mMenuSearch.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_all -> {
                // Handle delete all item click
                showDeleteConfirmationDialog()
                return true
            }
            else -> return true
        }
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
    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete All")
            .setMessage("Are you sure you want to delete all notes?")
            .setPositiveButton("Delete") { _, _ ->
                onDeleteAllClicked()
            }
            .setNegativeButton("Cancel") { _, _ ->
                Log.i("note","Got refused huh?")
            }
            .show()
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
    private fun onDeleteAllClicked(){
        CoroutineScope(Dispatchers.IO).launch {
            notesViewModel.deleteAllNotes()
        }
    }

    override fun checkOrUncheck(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            notesViewModel.updateNote(note)
        }
    }
}