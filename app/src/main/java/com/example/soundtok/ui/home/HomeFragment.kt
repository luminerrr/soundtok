package com.example.soundtok.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soundtok.data.DatabaseHelper
import com.example.soundtok.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(requireContext())

        // Initialize ViewModel with DatabaseHelper
        val homeViewModel =
            ViewModelProvider(this, ViewModelFactory(databaseHelper)).get(HomeViewModel::class.java)

        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up RecyclerView
        val recycleView = binding.soundListRv
        recycleView.layoutManager = LinearLayoutManager(requireContext())

        // Observe sounds LiveData
        homeViewModel.sounds.observe(viewLifecycleOwner) { sounds ->
            val adapter = SoundAdapter(sounds, requireContext())
            recycleView.adapter = adapter
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ViewModelFactory(private val databaseHelper: DatabaseHelper) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(databaseHelper) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}



