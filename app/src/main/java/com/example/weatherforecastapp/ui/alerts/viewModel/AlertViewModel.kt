package com.example.weatherforecastapp.ui.alerts.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.data.repository.RepoInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertViewModel(val repo: RepoInterface) : ViewModel() {

    init {
        getAllAlerts()
    }

    private val mutableStateFlowGetAllAlert = MutableStateFlow<List<AlertEntity>>(emptyList())
    val stateFlowGetAllAlert: StateFlow<List<AlertEntity>>
        get() = mutableStateFlowGetAllAlert

    private val insertMutableStateFlow = MutableStateFlow<Long>(0)
    val insertStateFlow: StateFlow<Long>
        get() = insertMutableStateFlow

    private val singleAlertMutableStateFlow = MutableStateFlow<AlertEntity>(AlertEntity())
    val singleAlertStateFlow: StateFlow<AlertEntity>
        get() = singleAlertMutableStateFlow


    fun getAllAlerts() {
        viewModelScope.launch {
            val flow = repo.getAllAlerts()
            flow.collect {
                mutableStateFlowGetAllAlert.value = it
            }
        }
    }

    fun insertAlert(alert: AlertEntity) : Long{
        var id : Long = 0L
        viewModelScope.launch {
            id = repo.insertAlert(alert)
            insertMutableStateFlow.value = id
        }
        getAllAlerts()
        return id
    }

    fun deleteAlert(alert: AlertEntity) {
        viewModelScope.launch {
            repo.deleteAlert(alert)
        }
        getAllAlerts()
    }

    fun getAlertById(id: Int) {
        viewModelScope.launch {
            val flow = repo.getAlertById(id)
            flow.collect {
                singleAlertMutableStateFlow.value = it
            }
        }
    }


}