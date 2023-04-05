package com.example.weatherforecastapp.data.remote

import com.example.weatherforecastapp.data.models.*
import com.example.weatherforecastapp.data.source.remote.RemoteSourceInterface
import retrofit2.Response

class FakeRemoteDataSource(private val modelRoot: Response<ModelRoot>? = null) :
    RemoteSourceInterface {

    override suspend fun getWeatherDataOnline(lat: Double, long: Double): Response<ModelRoot> {
        return modelRoot ?: Response.success(
            ModelRoot(
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
        )
    }
}