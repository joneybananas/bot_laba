import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.callbackQuery
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.handlers.CallbackQueryHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatAction
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.TelegramFile
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import data.remote.API_KEY
import data.remote.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.logging.Filter

private const val BOT_ANSWER_TIMEOUT = 30
private const val BOT_TOKEN = "6084750720:AAHEQ0si3xwHQV5Q24amcoarrvjEr6lyQis"

class WeatherBot(private val weatherRepository: WeatherRepository) {

    private var _chatId: ChatId? = null
    private val chatId: ChatId get() = requireNotNull(_chatId)
    private var country = ""

    fun createBot(): Bot {
        return bot {
            timeout = BOT_ANSWER_TIMEOUT
            token = BOT_TOKEN
            logLevel = com.github.kotlintelegrambot.logging.LogLevel.Error
            dispatch {
                setUpCommands()
                setUpCallbacks()
            }
        }

    }

    private fun Dispatcher.setUpCallbacks() {

        callbackQuery(callbackData = "enterManually", callbackAnswerShowAlert = true) {
            print("ZA4EM")

            bot.sendMessage(chatId = chatId, text = "Хорошо, введи свой город")
            message(com.github.kotlintelegrambot.extensions.filters.Filter.Text) {
                country = message.text.toString()
                bot.sendAnimation(
                    chatId = chatId,
                    animation = TelegramFile.ByUrl("http://www.smailikai.com/smailai/44/penguin_emoticons_015.gif")
                )
                bot.sendChatAction(chatId = chatId, action = ChatAction.TYPING)

                CoroutineScope(Dispatchers.IO).launch {
                    val currentWeather = weatherRepository.getCurrentWeather(
                        API_KEY,
                        country,
                        "no"
                    )
                    bot.sendMessage(
                        chatId = chatId,
                        """
                            ${currentWeather.location.name}
                             ☁ Облачность: ${currentWeather.current.cloud}
                            🌡 Температура (градусы): ${currentWeather.current.temp_c}
                            🙎 ‍Ощущается как: ${currentWeather.current.feelslike_c}
                            💧 Влажность: ${currentWeather.current.humidity}
                            🌪 Направление ветра: ${currentWeather.current.wind_dir}
                            🧭 Давление: ${currentWeather.current.precip_mm}
                            🌓 Сейчас день? ${if (currentWeather.current.is_day == 1) "Да" else "Нет"}
                        """.trimIndent()
                    )
                    country = ""


                }
            }

        }
    }

    private fun Dispatcher.setUpCommands() {
        command("start") {
            println("start")
            _chatId = ChatId.fromId(message.chat.id);
            bot.sendMessage(
                chatId = chatId,
                text = "Привет! Я бот, умеющий отображать погоду \n для запуска введи команлу /weather"

            )
        }

        command("weather") {
            val inlineKeybordMarcup = InlineKeyboardMarkup.create(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Ввести город",
                        callbackData = "enterManually",

                        )
                )
            )

            bot.sendMessage(
                chatId = chatId,
                text = "для того чтобы я смог отправить тебе погоду \n мне нужно знать твой город",
                replyMarkup = inlineKeybordMarcup
            )
        }


    }
}