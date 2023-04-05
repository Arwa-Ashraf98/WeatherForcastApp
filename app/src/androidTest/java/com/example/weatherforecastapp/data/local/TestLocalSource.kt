package com.example.weatherforecastapp.data.local

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.example.weatherforecastapp.data.models.*
import com.example.weatherforecastapp.data.source.local.LocalSource
import com.example.weatherforecastapp.data.source.local.LocalSourceInterface
import com.example.weatherforecastapp.data.source.local.MyRoomDataBase
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.core.Is
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@MediumTest
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class TestLocalSource {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val rule = MainCoroutineRule2()
    private lateinit var localSource: LocalSourceInterface
    private lateinit var database: MyRoomDataBase


    @Before
    fun setUp() {
        val application: Application = ApplicationProvider.getApplicationContext()
        database = Room
            .databaseBuilder(
                application.applicationContext,
                MyRoomDataBase::class.java,
                "Weather_DataBase"
            )
            .fallbackToDestructiveMigration()
            .build()

        localSource = LocalSource.getLocalSourceInstance()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllFavouritePlaces_nothing_listOfFavAddress() = rule.runBlockingTest {
        // Given local source already initializes


        // When : call get All fav places
        val list = localSource.getFavPlaces()

        // Given that list size != 0
        if (list.size <= 0) {
            Assert.assertThat(list.isEmpty(), Is.`is`(true))
        } else {
            Assert.assertThat(list.isNotEmpty(), Is.`is`(true))
        }

    }

    @Test
    fun deleteFavPlace_favPlaceObject_unit() = rule.runBlockingTest {
        // Given : favPlace object
        val favAddress3 = FavAddress(
            lat = 0.0,
            lon = 0.0,
            city = "city0",
            latLong = "latLong0"
        )

        // When : insert first then delete fav to database
        localSource.insertFavLocation(favAddress3)

        localSource.deleteFavPlace(favAddress3)

        // Given : assert that fakeAddresses is deleted in the database
        val list = localSource.getFavPlaces()


        Truth.assertThat(list).doesNotContain(favAddress3)
    }

    @Test
    fun insertFavLocation_favLocationObject_true() = rule.runBlockingTest {
        // Given : fav address
        val favAddress = FavAddress(
            lat = 0.0,
            lon = 0.0,
            city = "city12",
            latLong = "lat2Long"
        )

        // When : call function
        localSource.insertFavLocation(favAddress)
        val list = localSource.getFavPlaces()

        // Then : assert that list contain that object
        Truth.assertThat(list).contains(favAddress)
    }

    @Test
    fun deleteAlert_alertObject_unit() = rule.runBlockingTest {
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
        localSource.insertAlert(alertEntity)

        localSource.deleteAlert(alertEntity)

        // Given : assert that fakeAddresses is deleted in the database
        val list = localSource.getAllAlerts()

        Truth.assertThat(list).doesNotContain(alertEntity)
    }

    @Test
    fun getAllAlerts_nothing_listOfAlerts() = rule.runBlockingTest {
        // Given loca source


        // When : call get All alerts
        val list = localSource.getAllAlerts()


        // Given that list size != 0
        Assert.assertThat(list.isNotEmpty(), Is.`is`(true))
    }

    @Test
    fun insertAlert_alertObject_unit() = rule.runBlockingTest {
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
        val result = localSource.insertAlert(alert)

        // Then : assert that alert is inserted in the database
        val list = localSource.getAllAlerts()

        Assert.assertThat(list.contains(alert), Is.`is`(true))
        Assert.assertThat(result, CoreMatchers.notNullValue())
    }

    @Test
    fun getAlertById() = rule.runBlockingTest {
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
        val data = localSource.getAlertById(alert.id ?: 0)

        // Then : assert that alertModel is not null

        Assert.assertThat(data, CoreMatchers.notNullValue())

    }




}