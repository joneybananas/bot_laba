import data.remote.RetrofitClient
import data.remote.repository.WeatherRepository

fun main(args: Array<String>) {

    val weatherApi = RetrofitClient().getWeatherApi(RetrofitClient().getRetrofit())
 val weatherRepository = WeatherRepository(weatherApi)
    val weatherBot =  WeatherBot (weatherRepository).createBot()
    weatherBot.startPolling()
}