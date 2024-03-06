package com.project.sleeptracker.sleeptracker

import android.os.Bundle
import android.text.Layout.Directions
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.ViewModelFactoryDsl
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.project.sleeptracker.R
import com.project.sleeptracker.database.SleepDatabase
import com.project.sleeptracker.databinding.FragmentSleepTrackerBinding

class SleepTrackerFragment : Fragment() {

    private lateinit var viewModel: SleepTrackerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sleep_tracker, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

        val sleepTrackerViewModel = ViewModelProvider(this, viewModelFactory).get(SleepTrackerViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.sleepTrackerViewModel = sleepTrackerViewModel

        val manager = GridLayoutManager(activity, 3)
        binding.sleepList.layoutManager = manager

        val adapter = SleepNightAdapter(SleepNightListener {
            nightId -> sleepTrackerViewModel.onSleepNightClicked(nightId)
        })
        binding.sleepList.adapter = adapter

        sleepTrackerViewModel.navigateToSleepDataQuality.observe(viewLifecycleOwner, Observer { night ->
            night?.let {
                findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepDetail(night))
                sleepTrackerViewModel.onSleepDataQualityNavigated()
            }
        })

        sleepTrackerViewModel.nights.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner, Observer { night ->
            night?.let {
                this.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(night.nightLong))
                sleepTrackerViewModel.doneNavigating()
            }

        })

        sleepTrackerViewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                getString(R.string.cleared_message),
                Snackbar.LENGTH_SHORT // How long to display the message.
            ).show()
            sleepTrackerViewModel.doneShowingSnackbar()
            }
        })

        return binding.root
    }


}


