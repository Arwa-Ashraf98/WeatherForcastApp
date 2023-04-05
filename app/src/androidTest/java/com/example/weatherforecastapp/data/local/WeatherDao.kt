package com.example.weatherforecastapp.data.local

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherforecastapp.data.models.*
import com.example.weatherforecastapp.data.source.local.MyRoomDataBase
import com.example.weatherforecastapp.data.source.local.WeatherDao
import org.hamcrest.core.Is.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.core.Is
import org.junit.*
import org.junit.Assert
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDao {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dataBase: MyRoomDataBase
    private lateinit var myDao: WeatherDao
    private lateinit var modelRoot: ModelRoot

    @get:Rule
    @ExperimentalCoroutinesApi
    val main = MainCoroutineRule2()


    @Before
    fun setUp() {
        modelRoot = ModelRoot(
            1.1, 2.2, " ", " ",
            current = Current(
                clouds = 1,
                humidity = 2,
                pressure = 3,
                dt = 4,
                temp = 5.5,
                wind_speed = 6.6,
                weather = listOf(
                    Weather(
                        description = "des",
                        icon = "ico",
                        id = 1, main = "MAIN"
                    )
                ),
                dew_point = 2.2,
                sunrise = 2,
                sunset = 3,
                uvi = 5.1,
                visibility = 1,
                wind_deg = 5,
                wind_gust = 2.3,
                feels_like = 5.66
            ),
            daily = listOf(
                Daily(
                    dt = "1",
                    temp = Temp(
                        max = 1.1,
                        min = 2.2,
                        morn = 1.1,
                        day = 1.1,
                        eve = 12.2,
                        night = 21.2
                    ),
                    weather = listOf(
                        Weather(
                            description = "des",
                            icon = "ico",
                            id = 1,
                            main = "11"
                        )
                    ),
                    clouds = "1",
                    dew_poString = 1.1,
                    feels_like = FeelsLike(2.3, 1.2, 3.2, 5.2),
                    humidity = "1",
                    moon_phase = 2.2,
                    moonrise = "1",
                    moonset = "2",
                    pop = 1.2,
                    pressure = "0",
                    rain = 12.2,
                    sunset = "sun",
                    sunrise = "sunrise",
                    uvi = 1.1,
                    wind_gust = 1.2,
                    wind_deg = "2.5",
                    wind_speed = 2.2
                )
            ), hourly = listOf(
                Hourly(
                    pop = "1.2 ",
                    pressure = "0",
                    uvi = 1.1,
                    wind_gust = 1.2,
                    wind_deg = "2.5",
                    wind_speed = 2.2,
                    weather = listOf(
                        Weather(
                            description = "des",
                            icon = "ico",
                            id = 1,
                            main = "11"
                        )
                    ),
                    dew_poString = 1.1,
                    feels_like = 2.3,
                    humidity = "1",
                    clouds = "1",
                    dt = "1",
                    temp = 1.2,
                    visibility = "gone"
                )
            )
        )
        val application: Application = ApplicationProvider.getApplicationContext()
        dataBase = Room
            .databaseBuilder(
                application.applicationContext,
                MyRoomDataBase::class.java,
                "Weather_DataBase"
            )
            .fallbackToDestructiveMigration()
            .build()

        myDao = dataBase.getDao()
    }

    @After
    fun closeDataBase() = dataBase.close()

    // home
    @Test
    fun saveWeatherDataLocally_modelRoot_nothing() = main.runBlockingTest {
        // Given : modelRoot
       val model = modelRoot

        // When: insert modelRoot to database
        val data = myDao.insertWeatherResponse(model)
        val modelData = myDao.getWeatherResponseFromLocal()
        // Then : assert that model root is inserted to database
        Assert.assertThat(data, notNullValue())
        Assert.assertThat(modelData , notNullValue())
    }

    @Test
    fun insertAfterDelete() = main.runBlockingTest {
        val data = myDao.getWeatherResponseFromLocal()
        if (data != null){
            myDao.deleteWeatherResponse(data)
        }
        val long = myDao.insertWeatherResponse(data!!)
        Assert.assertThat(long , notNullValue())
    }

    // fav

    @Test
    fun insertFavLocation_favAddressObject_nothing() = main.runBlockingTest {
        // Given fav object
        val favAddress = FavAddress(
            lat = 0.0,
            lon = 0.0,
            city = "city12",
            latLong = "lat2Long"
        )

        // When : Call function from fake repo and then get list of fav places
        myDao.insertFavLocation(favAddress)
        val list = myDao.getAllFavouritePlaces()

        // Then : assert that fav places list contain object

        assertThat(list).contains(favAddress)
    }

    @Test
    fun getAllFavouritePlaces_nothing_listOfFavAddress() = main.runBlockingTest {
        // Given dao already initializes


        // When : call get All fav places
        val list = myDao.getAllFavouritePlaces()

        // Given that list size != 0
        Assert.assertThat(list.isNotEmpty(), `is`(true))
    }

    @Test
    fun deleteFavPlace_favPlaceObject_unit() = main.runBlockingTest {
        // Given : favPlace object
        val favAddress3 = FavAddress(
            lat = 0.0,
            lon = 0.0,
            city = "city0",
            latLong = "latLong0"
        )

        // When : insert first then delete fav to database
        myDao.insertFavLocation(favAddress3)

        myDao.deleteFavPlace(favAddress3)

        // Given : assert that fakeAddresses is deleted in the database
        val list = myDao.getAllFavouritePlaces()


        assertThat(list).doesNotContain(favAddress3)
//            assertThat(list.doesNotContain(favAddress3), Is.`is`(true))

    }

    // alert

    @Test
    fun deleteAlert_alertObject_unit() = main.runBlockingTest {
        // Given : alert object
        val alertEntity = AlertEntity(
            id = 9,
            endDate = 8562389652,
            startDate = 8562345623,
            startTime = 86535623,
            endTime = 854785,
            city = "dfs",
            longitude = 52.5856,
            latitude = 42.2445,
        )

        // When : insert first then delete fav to database
        myDao.insertAlert(alertEntity)

        myDao.deleteAlert(alertEntity)

        // Given : assert that fakeAddresses is deleted in the database
        val list = myDao.getAllAlerts()

        assertThat(list).doesNotContain(alertEntity)
    }

    @Test
    fun getAllAlerts_nothing_listOfAlerts() = main.runBlockingTest {
        // Given dao


        // When : call get All alerts
        val list = myDao.getAllAlerts()


        // Given that list size != 0
        Assert.assertThat(list.isNotEmpty(), `is`(true))


    }

    @Test
    fun insertAlert_alertObject_unit() = main.runBlockingTest {
        // Given : alert object
        val alert = AlertEntity(
            id = 3,
            endDate = 8562389652,
            startDate = 8562345623,
            startTime = 86535623,
            endTime = 854785,
            city = "dfs",
            longitude = 52.5856,
            latitude = 42.2445,
        )

        // When : insert alert to database
        val result = myDao.insertAlert(alert)

        // Then : assert that alert is inserted in the database
        val list = myDao.getAllAlerts()

        Assert.assertThat(list.contains(alert), `is`(true))
        Assert.assertThat(result, notNullValue())
    }

    @Test
    fun getAlertById() = main.runBlockingTest {
        // Given : Alert item
        val alert = AlertEntity(
            id = 3,
            endDate = 8562389652,
            startDate = 8562345623,
            startTime = 86535623,
            endTime = 854785,
            city = "dfs",
            longitude = 52.5856,
            latitude = 42.2445,
        )

        // When : call function
        val data = myDao.getAlertsById(alert.id ?: 0)

        // Then : assert that alertModel is not null

        Assert.assertThat(data, notNullValue())

    }


}






