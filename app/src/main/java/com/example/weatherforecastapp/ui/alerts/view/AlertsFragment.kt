package com.example.weatherforecastapp.ui.alerts.view

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.data.repository.Repo
import com.example.weatherforecastapp.data.source.local.LocalSource
import com.example.weatherforecastapp.data.source.remote.RemoteSource
import com.example.weatherforecastapp.databinding.FragmentAlertsBinding
import com.example.weatherforecastapp.ui.alerts.alertServices.AlertService
import com.example.weatherforecastapp.ui.alerts.viewModel.AlertViewModel
import com.example.weatherforecastapp.ui.alerts.viewModel.AlertViewModelFactory
import com.example.weatherforecastapp.ui.home.viewModel.HomeViewModel
import com.example.weatherforecastapp.ui.home.viewModel.HomeViewModelFactory
import com.example.weatherforecastapp.ui.main.MainActivity
import com.example.weatherforecastapp.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AlertsFragment : Fragment() {

    private var binding: FragmentAlertsBinding? = null
    private lateinit var adapter: AlertAdapter
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var alertViewModelFactory: AlertViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        adapter = AlertAdapter()
        alertViewModel.getAllAlerts()
        observeData()
        onClicks()


    }

    private fun startService(description: String) {
        val intent = Intent(requireContext(), AlertService::class.java).apply {
            putExtra("description", description)
        }
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    private fun initViewModel() {
        alertViewModelFactory = AlertViewModelFactory(
            Repo.getRepoInstance(
                RemoteSource.getRemoteSourceInstance(),
                LocalSource.getLocalSourceInstance()
            )
        )

        alertViewModel =
            ViewModelProvider(requireActivity(), alertViewModelFactory)[AlertViewModel::class.java]

    }

    private fun observeData() {
        lifecycleScope.launchWhenCreated {
            alertViewModel.stateFlowGetAllAlert.collect {
                adapter.setList(it)
                binding?.recyclerAlerts?.adapter = adapter
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun onClicks() {
        binding?.apply {
            adapter.setOnAlertItemClickListener(object : AlertAdapter.AlertClickListener {
                override fun onDeleteAlertClickListener(alertEntity: AlertEntity) {
                    alertViewModel.deleteAlert(alertEntity)
                    WorkManager.getInstance().cancelAllWorkByTag("${alertEntity.id}")
                }

            })

            swipeRefreshLayoutAlerts.setOnRefreshListener {
                alertViewModel.getAllAlerts()
                swipeRefreshLayoutAlerts.isRefreshing = false
            }

            btnAddAlerts.setOnClickListener {
//                if ((ContextCompat.checkSelfPermission(
//                        requireContext(), Manifest.permission.POST_NOTIFICATIONS
//                    ) == PackageManager.PERMISSION_GRANTED
//                            ) && (Settings.canDrawOverlays(requireContext()))
//                ) {
                    AlertDialogFragment().show(
                        requireActivity().supportFragmentManager,
                        Constants.ALERT_TAG
                    )
//                } else {
//                    Toast.makeText(requireContext(), "not granted", Toast.LENGTH_SHORT).show()
//                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}