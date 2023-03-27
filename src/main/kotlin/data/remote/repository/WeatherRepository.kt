package data.remote.repository

import data.remote.api.WeatherApi
import data.remote.models.CurrentWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(private  val  weatherApi: WeatherApi) {

    suspend  fun  getCurrentWeather(apiKey:String, countryName:String, airQualityData: String) : CurrentWeather{
   return withContext(Dispatchers.IO){
        weatherApi.getCurrentWeather(apiKey,countryName,airQualityData)
    }.await()
    }

}