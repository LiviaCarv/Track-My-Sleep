package com.project.sleeptracker.sleepquality

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.sleeptracker.R
import com.project.sleeptracker.database.SleepDatabase
import com.project.sleeptracker.databinding.FragmentSleepQualityBinding
import com.project.sleeptracker.sleeptracker.SleepTrackerViewModel
import com.project.sleeptracker.sleeptracker.SleepTrackerViewModelFactory

class SleepQualityFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentSleepQualityBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sleep_quality, container, false)

        val application = requireNotNull(this.activity).application

        val arguments = SleepQualityFragmentArgs.fromBundle(requireArguments())

        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        val viewModelFactory = SleepQualityViewModelFactory(arguments.sleepNightKey, dataSource)

        val sleepQualityViewModel = ViewModelProvider(this, viewModelFactory).get(SleepQualityViewModel::class.java)

        binding.sleepQualityViewModel = sleepQualityViewModel
        binding.lifecycleOwner = viewLifecycleOwner


        sleepQualityViewModel.navigateToSleepTracker.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController().navigate(R.id.action_sleepQualityFragment_to_sleepTrackerFragment)
                sleepQualityViewModel.doneNavigating()
            }
        })

        return binding.root
    }



}