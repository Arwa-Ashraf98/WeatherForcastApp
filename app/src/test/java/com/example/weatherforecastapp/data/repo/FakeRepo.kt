package com.example.weatherforecastapp.data.repo

import android.content.SharedPreferences
import com.example.weatherforecastapp.data.local.FakeLocalDataSource
import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.data.models.ModelRoot
import com.example.weatherforecastapp.data.remote.FakeRemoteDataSource
import com.example.weatherforecastapp.data.repository.RepoInterface
import com.example.weatherforecastapp.data.source.local.LocalSourceInterface
import com.example.weatherforecastapp.data.source.remote.RemoteSourceInterface
import com.example.weatherforecastapp.utils.Const
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.Locale

class FakeRepo(
    private val localDataSource: LocalSourceInterface = FakeLocalDataSource(),
    private val remoteDataSource: RemoteSourceInterface = FakeRemoteDataSource(),
    private val sharedPreference: SharedPreferences? = null
) : RepoInterface {


    override suspend fun getWeatherDataOnline(
        lat: Double,
        long: Double
    ): Flow<Response<ModelRoot>> = flow {
        emit(remoteDataSource.getWeatherDataOnline(lat = lat, long = long))
    }

    override suspend fun saveWeatherData(modelRoot: ModelRoot) {
        localDataSource.saveWeatherResponseAfterDelete(modelRoot)
    }

    override fun setLocalization(language: String) {
        sharedPreference?.edit()?.putString(Const.LANGUAGE_KEY, language)?.apply()
    }

    override fun setWindSpeed(windSpeedChoice: String) {
        sharedPreference?.edit()?.putString(Const.WIND_KEY, windSpeedChoice)?.apply()
    }

    override fun setTemp(tempChoice: String) {
        sharedPreference?.edit()?.putString(Const.TEMPERATURE_KEY, tempChoice)?.apply()
    }

    override fun setLocation(locationChoice: String) {
        sharedPreference?.edit()?.putString(Const.LOCATION_KEY, locationChoice)?.apply()
    }

    override fun setAlarm(alarm: String) {
        sharedPreference?.edit()?.putString(Const.ALARM_KEY, alarm)?.apply()
    }

    override fun getLocalization(): String? {
        return sharedPreference?.getString(Const.LANGUAGE_KEY, Locale.getDefault().language)
            ?: Locale.getDefault().language
    }

    override fun getTemp(): String? {
        return sharedPreference?.getString(Const.TEMPERATURE_KEY, Const.TEMP_KELVIN_VALUES) ?: Const.TEMP_KELVIN_VALUES
    }

    override fun getWindSpeed(): String? {
        return sharedPreference?.getString(Const.WIND_KEY, Const.WIND_SPEED_METER_SEC_VALUES) ?: Const.WIND_SPEED_METER_SEC_VALUES
    }

    override fun getLocation(): String? {
        return sharedPreference?.getString(Const.LOCATION_KEY, Const.GPS_LOCATION_VALUES) ?: Const.GPS_LOCATION_VALUES
    }

    override fun getAlarm(): String? {
        return sharedPreference?.getString(Const.ALARM_KEY, Const.NOTIFICATION) ?: Const.NOTIFICATION
    }

    override suspend fun insertFaveLocation(favLocation: FavAddress) {
        localDataSource.insertFavLocation(favLocation)
    }

    override suspend fun getFavPlaces(): Flow<List<FavAddress>> = flow {
        emit(localDataSource.getFavPlaces())
    }

    override suspend fun deleteFavPlace(favAddress: FavAddress) {
        localDataSource.deleteFavPlace(favAddress)
    }

    override suspend fun getWeatherDataLocally(): Flow<ModelRoot> = flow {
        emit(localDataSource.getWeatherDataLocally())
    }

    override suspend fun insertAlert(alertEntity: AlertEntity): Long =
        localDataSource.insertAlert(alertEntity)

    override suspend fun getAlertById(id: Int?): Flow<AlertEntity> = flow {
        emit(localDataSource.getAlertById(id))
    }

    override suspend fun deleteAlert(alertEntity: AlertEntity) {
        localDataSource.deleteAlert(alertEntity)
    }

    override suspend fun getAllAlerts(): Flow<List<AlertEntity>> = flow {
        emit(localDataSource.getAllAlerts())
    }
}