package com.example.weatherforecastapp.ui.fav

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecastapp.MainCoroutineRule
import com.example.weatherforecastapp.data.local.FakeLocalDataSource
import com.example.weatherforecastapp.data.models.*
import com.example.weatherforecastapp.data.remote.FakeRemoteDataSource
import com.example.weatherforecastapp.data.repo.FakeRepo
import com.example.weatherforecastapp.ui.favourite.viewModel.FavouriteViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@ExperimentalUnitApi
@RunWith(AndroidJUnit4::class)
class FavouriteViewModelTest {

    private lateinit var favViewModel : FavouriteViewModel
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var fakeLocalDataSource: FakeLocalDataSource
    private lateinit var repo: FakeRepo
    private lateinit var modelRoot: ModelRoot
    private lateinit var favList: MutableList<FavAddress>
    private lateinit var alertList: MutableList<AlertEntity>

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @get:Rule
    @ExperimentalCoroutinesApi
    val main = MainCoroutineRule()

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

        // ---------------------------------
        val alertEntity = AlertEntity(
            id = 1,
            endDate = 8562389652,
            startDate = 8562345623,
            startTime = 86535623,
            endTime = 854785,
            city = "dfs",
            longitude = 52.5856,
            latitude = 42.2445,
        )

        val alertEntity1 = AlertEntity(
            id = 2,
            endDate = 8562389652,
            startDate = 8562345623,
            startTime = 86535623,
            endTime = 854785,
            city = "dfs",
            longitude = 52.5856,
            latitude = 42.2445,
        )

        val alertEntity2 = AlertEntity(
            id = 3,
            endDate = 8562389652,
            startDate = 8562345623,
            startTime = 86535623,
            endTime = 854785,
            city = "dfs",
            longitude = 52.5856,
            latitude = 42.2445,
        )
        alertList = mutableListOf(alertEntity, alertEntity1, alertEntity2)
        // ---------------------------------

        val favAddress1 = FavAddress(
            lat = 30.45551495,
            lon = 31.1584258,
            city = "city",
            latLong = "latLong"
        )

        val favAddress2 = FavAddress(
            lat = 30.45551495,
            lon = 31.1584258,
            city = "city",
            latLong = "latLong"
        )

        val favAddress3 = FavAddress(
            lat = 30.45551495,
            lon = 31.1584258,
            city = "city",
            latLong = "latLong"
        )
        favList = mutableListOf(favAddress1, favAddress2, favAddress3)
        // -------------------------------------------------------------------
        fakeLocalDataSource = FakeLocalDataSource(modelRoot, favList, alertList)
        fakeRemoteDataSource = FakeRemoteDataSource()
        repo =
            FakeRepo(localDataSource = fakeLocalDataSource, remoteDataSource = fakeRemoteDataSource)
        favViewModel = FavouriteViewModel(repo)
    }

    @Test
    fun getAllFavouritePlaces_nothing_listOfFavAddress() = main.runBlockingTest{
        // Given repo
        val repository = repo

        // When : call get All fav places
        val flow = repository.getFavPlaces()
        flow.collect {
            val list = it

            // Given that list size != 0
            Assert.assertThat(list.isNotEmpty(), Is.`is`(true))
        }
    }

    @Config(manifest = Config.NONE)
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
        repo.insertAlert(alertEntity)

        repo.deleteAlert(alertEntity)

        // Given : assert that fakeAddresses is deleted in the database
        val flow = repo.getAllAlerts()
        flow.collect {
            val list = it
            assertThat(list).doesNotContain(alertEntity)
//            assertThat(list.doesNotContain(favAddress3), Is.`is`(true))
        }
    }


}