package com.example.weatherforecastapp.data.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecastapp.MainCoroutineRule
import com.example.weatherforecastapp.data.local.FakeLocalDataSource
import com.example.weatherforecastapp.data.models.*
import com.example.weatherforecastapp.data.remote.FakeRemoteDataSource
import com.example.weatherforecastapp.data.repository.Repo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import com.google.common.truth.Truth.assertThat
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.*
import org.hamcrest.core.Is
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.matchers.Null
import org.robolectric.annotation.Config
import retrofit2.Response


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RepoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    private lateinit var fakeLocalDataSource: FakeLocalDataSource
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var repo: Repo
    private lateinit var modelRoot: ModelRoot
    private lateinit var favList: MutableList<FavAddress>
    private lateinit var alertList: MutableList<AlertEntity>
    private lateinit var weatherResponse: Response<ModelRoot>


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
        repo = Repo.getRepoInstance(
            remoteSource = fakeRemoteDataSource,
            localSource = fakeLocalDataSource
        )
    }

    @Test
    fun get_WeatherDataOnline_latAndLong_true() = mainCoroutineRule.runBlockingTest {
        val flow = repo.getWeatherDataOnline(1.1, 2.2)
        val data1 = Response.success(modelRoot).body()
        flow.collect {
            val data = it.body()
            assertThat(data).isEqualTo(data1)
        }
    }

    @Test
    fun saveWeatherDataLocally_modelRoot_nothing() = mainCoroutineRule.runBlockingTest {
        // Given : modelRoot
        val model = modelRoot

        // When: insert modelRoot to database
        repo.saveWeatherData(model)

        // Then : assert that model root is inserted to database
        val flow = repo.getWeatherDataLocally()
        flow.collect {
            val data = it
            assertThat(data, notNullValue())
        }
    }

    @Test
    fun getWeatherDataLocally_nothing_modelRoot() = mainCoroutineRule.runBlockingTest {
        // Given repo
        val repository = repo

        // When : call function that return flow of ModelRoot
        val flow = repository.getWeatherDataLocally()
        flow.collect {
            val data = it

            // Then : assert that data data not null
            assertThat(data, notNullValue())
        }

    }

    @Test
    fun insertFavPlace_favPlaceObject_unit() = mainCoroutineRule.runBlockingTest {
        // Given : favPlace object

        val favAddress3 = favList[0]

        // When : insert fav to database
        repo.insertFaveLocation(favAddress3)

        // Then : assert that fakeAddresses is inserted in the database
        val flow = repo.getFavPlaces()
        flow.collect {
            val list = it
            assertThat(list.contains(favAddress3), Is.`is`(true))
//            assertThat(list.contains(favAddress3))
        }


    }

    @Config(manifest = Config.NONE)
    @Test
    fun deleteFavPlace_favPlaceObject_unit() = mainCoroutineRule.runBlockingTest {
        // Given : favPlace object
        val favAddress3 = FavAddress(
            lat = 0.0,
            lon = 0.0,
            city = "city0",
            latLong = "latLong0"
        )

        // When : insert first then delete fav to database
        repo.insertFaveLocation(favAddress3)

        repo.deleteFavPlace(favAddress3)

        // Given : assert that fakeAddresses is deleted in the database
        val flow = repo.getFavPlaces()
        flow.collect {
            val list = it
            assertThat(list).doesNotContain(favAddress3)
//            assertThat(list.doesNotContain(favAddress3), Is.`is`(true))
        }
    }

    @Test
    fun getAllFavPlaces_nothing_listOfFavPlaces() = mainCoroutineRule.runBlockingTest {
        // Given repo
        val repository = repo

        // When : call get All fav places
        val flow = repository.getFavPlaces()
        flow.collect {
            val list = it

            // Given that list size != 0
            assertThat(list.isNotEmpty(), Is.`is`(true))
        }
    }

    @Test
    fun getAllAlerts_nothing_listOfAlerts() = mainCoroutineRule.runBlockingTest {
        // Given repo
        val repository = repo

        // When : call get All alerts
        val flow = repository.getAllAlerts()

        flow.collect {
            val list = it

            // Given that list size != 0
            assertThat(list.isNotEmpty(), Is.`is`(true))

        }

    }

    @Test
    fun insertAlert_alertObject_unit() = mainCoroutineRule.runBlockingTest {
        // Given : alert object

        val alert = alertList[0]

        // When : insert alert to database
        val result = repo.insertAlert(alert)

        // Then : assert that alert is inserted in the database
        val flow = repo.getAllAlerts()
        flow.collect {
            val list = it
            assertThat(list.contains(alert), Is.`is`(true))
            assertThat(result, notNullValue())
//            assertThat(list.contains(favAddress3))
        }


    }

    @Config(manifest = Config.NONE)
    @Test
    fun deleteAlert_alertObject_unit() = mainCoroutineRule.runBlockingTest {
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

    @Test
    fun getAlertById() = mainCoroutineRule.runBlockingTest {
        // Given : Alert item
        val alert = alertList[0]

        // When : call function
        val flow  = repo.getAlertById(alert.id)
        flow.collect{
            // Then : assert that alertModel is not null
            val data = it
            assertThat(data , notNullValue())
        }
    }

}