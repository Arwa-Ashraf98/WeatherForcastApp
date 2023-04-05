package com.example.weatherforecastapp.ui.alerts.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.data.repository.Repo
import com.example.weatherforecastapp.data.source.local.LocalSource
import com.example.weatherforecastapp.data.source.local.sharedPreferences.MySharedPreferences
import com.example.weatherforecastapp.data.source.remote.RemoteSource
import com.example.weatherforecastapp.databinding.DialogFragmentAlertBinding
import com.example.weatherforecastapp.ui.alerts.alertServices.AlertOneTimeWorkManager
import com.example.weatherforecastapp.ui.alerts.alertServices.AlertPeriodicWorkManager
import com.example.weatherforecastapp.ui.alerts.alertUtils.AlertConverters
import com.example.weatherforecastapp.ui.alerts.viewModel.AlertViewModel
import com.example.weatherforecastapp.ui.alerts.viewModel.AlertViewModelFactory
import com.example.weatherforecastapp.utils.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("NAME_SHADOWING")
class AlertDialogFragment : DialogFragment() {

    private var binding: DialogFragmentAlertBinding? = null
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener
    private var lat = 0.0
    private var long = 0.0
    private var endTime: Long? = null
    private var startTime: Long? = null
    private var startDate: Long? = null
    private var endDate: Long? = null
    private var startYear: Int = 0
    private var startMonth: Int = 0
    private var sDay: Int = 0
    private var sHoure: Int = 0
    private var sMin: Int = 0
    private var eYear: Int = 0
    private var eMonth: Int = 0
    private var eDay: Int = 0
    private var eHoure: Int = 0
    private var eMin: Int = 0
    private var delay: Long = 0L


    private val alertViewModel by lazy {
        val alertViewModelFactory = AlertViewModelFactory(
            Repo.getRepoInstance(
                remoteSource = RemoteSource.getRemoteSourceInstance(),
                localSource = LocalSource.getLocalSourceInstance()
            )
        )
        ViewModelProvider(requireActivity(), alertViewModelFactory)[AlertViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentAlertBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClicks()
        lifecycleScope.launchWhenCreated {
            alertViewModel.insertStateFlow.collect {
                alertViewModel.getAllAlerts()
                Log.e("LIMIT", it.toString())
                setPeriodWorkManger(it)
            }
        }

    }

    private fun setPeriodWorkManger(id: Long) {
        val data = Data.Builder()
        data.putLong("id", id)
        val calenderStart = Calendar.getInstance()
        calenderStart.set(startYear, startMonth, sDay, sHoure, sMin)
        val calenderEnd = Calendar.getInstance()
        calenderStart.set(eYear, eMonth, eDay, eHoure, eMin)
        val delay = calenderStart.timeInMillis.div(1000L) - calenderEnd.timeInMillis.div(1000L)
        data.putLong("delay", delay)

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            AlertPeriodicWorkManager::class.java,
            24, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()


        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "$id",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun onClicks() {
        binding?.apply {

            editTextStartDate.setOnClickListener {
                showDatePicker(it as EditText, true)
            }

            editTextStartTime.setOnClickListener {
                showTimePicker(it as EditText, true)
            }

            editTextEndDate.setOnClickListener {
                showDatePicker(it as EditText, false)

            }

            editTextEndTime.setOnClickListener {
                showTimePicker(it as EditText, false)

            }
            gpsRadioButton.setOnClickListener {
                mapRadioButton.isChecked = false
                if (NetworkConnectivityObserver.isOnline(requireContext())) {
                    lat = MySharedPreferences.getLatitude() as Double
                    long = MySharedPreferences.getLongitude() as Double
                } else {
                    Extensions.showConfirmationDialog(requireContext(),
                        getString(R.string.slould_use_net),
                        // onYes
                        {
                            val intent = Intent(Settings.ACTION_SETTINGS)
                            startActivity(intent)
                        },
                        // on no
                        {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.try_later),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }

            btnCancel.setOnClickListener {
                Extensions.showConfirmationDialog(requireContext(),
                    getString(R.string.sure_cancel),
                    // on yes
                    {
                        dialog?.dismiss()
                    },
                    // on no
                    {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.continue_fill_data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
            btnSave.setOnClickListener {
                getData()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun showDatePicker(editText: EditText, isFrom: Boolean) {
        val calender = Calendar.getInstance()
        val month = calender.get(Calendar.MONTH)
        val year = calender.get(Calendar.YEAR)
        val day = calender.get(Calendar.DAY_OF_MONTH)
        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val date = AlertConverters.formatDate(year, month, dayOfMonth)
            val date2 = AlertConverters.convertDateToLong(date, requireContext())
            editText.setText(date)
            if (isFrom) {
                sDay = day
                startMonth = month
                startYear = year
                startDate = date2
            } else {
                eDay = day
                eMonth = month
                eYear = year
                endDate = date2
            }
        }
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialog,
            dateSetListener,
            year,
            month,
            day
        )
        datePickerDialog.setTitle(getString(R.string.choose_date))
        datePickerDialog.show()
    }

    private fun convertTimeToLong(minute: Int, hour: Int): Long {
        return TimeUnit.MINUTES.toSeconds(minute.toLong()) +
                TimeUnit.HOURS.toSeconds(hour.toLong()) - (3600L * 2)
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun showTimePicker(editText: EditText, isFrom: Boolean) {
        val calender = Calendar.getInstance()
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minute = calender.get(Calendar.MINUTE)
        timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->

            val time2 = convertTimeToLong(minute, hourOfDay)
            val time = AlertConverters.formatTime(hourOfDay, minute)
            editText.setText(time)
            if (isFrom) {
                sHoure = hourOfDay
                sMin = minute
                startTime = time2
            } else {
                eHoure = hourOfDay
                eMin = minute
                endTime = time2
            }
        }
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.CustomTimePickerDialog,
            timeSetListener,
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getData() {
        binding?.apply {
            val startDate = editTextStartDate.text.toString().trim()
            val startTime = editTextStartTime.text.toString().trim()
            val endDate = editTextEndDate.text.toString().trim()
            val endTime = editTextEndTime.text.toString().trim()


            val gpsIsChecked = binding?.gpsRadioButton?.isChecked
            val mapIsChecked = binding?.mapRadioButton?.isChecked
            validation(
                startDate = startDate,
                startTime = startTime,
                endDate = endDate,
                endTime = endTime,
                mapIsChecked = mapIsChecked as Boolean,
                gpsIsChecked = gpsIsChecked as Boolean
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun validation(
        startDate: String?,
        startTime: String?,
        endDate: String?,
        endTime: String?,
        gpsIsChecked: Boolean,
        mapIsChecked: Boolean
    ) {
        if (startDate.isNullOrEmpty()) {
            getString(R.string.required).also { binding?.editTextStartDate?.error = it }
        } else if (startTime.isNullOrEmpty()) {
            getString(R.string.required).also { binding?.editTextStartTime?.error = it }
        } else if (endDate.isNullOrEmpty()) {
            getString(R.string.required).also { binding?.editTextEndDate?.error = it }
        } else if (endTime.isNullOrEmpty()) {
            getString(R.string.required).also { binding?.editTextEndTime?.error = it }
        } else if (!gpsIsChecked && !mapIsChecked) {
            getString(R.string.you_have_to_select_one).also { binding?.gpsRadioButton?.error = it }
            getString(R.string.you_have_to_select_one).also { binding?.mapRadioButton?.error = it }
        } else {
            checkDateAndTime(
                startDateE = startDate.plus("-").plus(startTime),
                endDateE = endDate.plus("-").plus(endTime),
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkDateAndTime(
        startDateE: String,
        endDateE: String,
    ) {
        val formatterDate = SimpleDateFormat(Converters.WHOLE_DATE, Locale.getDefault())
        formatterDate.timeZone = TimeZone.getTimeZone("GMT+2")
        val startDate = formatterDate.parse(startDateE)
        val endDate = formatterDate.parse(endDateE)
        val unixDateStart = startDate?.time
        val unixDateEnd = endDate?.time

        if (unixDateStart != null && unixDateEnd != null) {

            if (unixDateStart >= unixDateEnd) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.end_date_larger_than_start_date),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                saveAlert()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun saveAlert() {
        if (binding?.mapRadioButton?.isChecked as Boolean) {
            // get from shared preference
        } else if (binding?.gpsRadioButton?.isChecked as Boolean) {

            val alertEntity = AlertEntity(
                latitude = lat,
                longitude = long,
                startTime = startTime,
                endTime = endTime,
                startDate = startDate,
                endDate = endDate,
                city = GeoCoderConverter.getAddress(
                    requireContext(),
                    lat = lat,
                    long = long
                )
            )
            Log.e("ALERT DIALOG", alertEntity.toString())
            val result = alertViewModel.insertAlert(alertEntity)
            Log.e("LIMIT", result.toString())
            dialog?.dismiss()
            Toast.makeText(requireContext(), getString(R.string.added), Toast.LENGTH_SHORT).show()

        }
    }

//    private fun setWorkManager(id: Long) {
//        val data = Data.Builder()
//        data.putLong("id", id)
//        val calenderStart = Calendar.getInstance()
//        calenderStart.set(startYear, startMonth, sDay, sHoure, sMin)
//        val calenderEnd = Calendar.getInstance()
//        calenderStart.set(eYear, eMonth, eDay, eHoure, eMin)
//        val delay = calenderStart.timeInMillis.div(1000L) - calenderEnd.timeInMillis.div(1000L)
//        val constraints = Constraints.Builder()
//            .setRequiresBatteryNotLow(true)
//            .build()
//
////        val periodicWorkRequest = PeriodicWorkRequest.Builder(
////            AlertPeriodicWorkManager::class.java,
////            24, TimeUnit.HOURS
////        )
////            .setConstraints(constraints)
////            .setInitialDelay(delay, TimeUnit.SECONDS)
////            .setInputData(data.build())
////            .build()
//
//        val periodicWorkRequest = OneTimeWorkRequest.Builder(
//            AlertOneTimeWorkManager::class.java,)
//            .setConstraints(constraints)
//            .setInitialDelay(delay, TimeUnit.SECONDS)
//            .setInputData(data.build())
//            .build()
//
////        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
////            "$id",
////            ExistingPeriodicWorkPolicy.REPLACE,
////            periodicWorkRequest
////        )
//        WorkManager.getInstance(requireContext()).enqueueUniqueWork(
//            "$id",
//            ExistingWorkPolicy.REPLACE,
//            periodicWorkRequest
//        )
//        Log.e("TAG", "setWorkManager: 00000000 ", )
//
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    /*
//    override fun requestPermission() {
//        requestPermissions(
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
//            ), Constants.PERMISSION_LOCATION_ID_MAP_FRAGMENT
//        )
//    }

//    override fun getLocationResult(locationResult: LocationResult?) {
//        lifecycleScope.launchWhenCreated {
//            job1 = launch {
//                lat = locationResult?.lastLocation?.latitude as Double
//                long = locationResult.lastLocation?.longitude as Double
//                Toast.makeText(
//                    requireContext(),
//                    lat.toString().plus(long.toString()),
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//                Log.e("ALERT DIALOG", lat.toString() + "LAAAAAAAAAAAAT FROOOOOM DIALOG FRAGMENT")
//                Log.e("ALERT DIALOG", long.toString() + "LONNNNNNG FROOOOOM DIALOG FRAGMENT")
//            }
//        }
//
//    }
*/
    /*
            mapRadioButton.setOnClickListener {
                gpsRadioButton.isChecked = false
                // here i should use shared preference with this part
                if (NetworkConnectivityObserver.isOnline(requireContext())) {
//                    val action =
//                        AlertDialogFragmentDirections.actionAlertDialogFragmentToMapFragment("alert")
//                    findNavController().navigate(action)
                    Toast.makeText(requireContext(), " MAP is clicked", Toast.LENGTH_SHORT).show()

                } else {
                    Extensions.showConfirmationDialog(requireContext(),
                        getString(R.string.slould_use_net),
                        // onYes
                        {
                            val intent = Intent(Settings.ACTION_SETTINGS)
                            startActivity(intent)
                        },
                        // on no
                        {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.try_later),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
*/

}