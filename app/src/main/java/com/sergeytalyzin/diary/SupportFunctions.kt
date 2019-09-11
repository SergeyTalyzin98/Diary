package com.sergeytalyzin.diary

import android.content.Context
import java.util.*

fun getTodayDate() : String {

    val data = Calendar.getInstance()
    var dayOfMonth = ""
    var month = ""

    if(data.get(Calendar.DAY_OF_MONTH) < 10)
        dayOfMonth = "0"

    if(data.get(Calendar.MONTH) < 10)
        month = "0"

    return "$dayOfMonth${data.get(Calendar.DAY_OF_MONTH)}.$month${data.get(Calendar.MONTH)+1}.${data.get(Calendar.YEAR)-2000}"
}

fun getTodayDateForShow(context: Context, date: String) : String {

    val dayOfMonth = "${date[0]}${date[1]}"
    val year = "${date[6]}${date[7]}"
    var month = ""

    when("${date[3]}${date[4]}") {
        "01" -> month = context.resources.getText(R.string.textJanuary).toString()
        "02" -> month = context.resources.getText(R.string.textFebruary).toString()
        "03" -> month = context.resources.getText(R.string.textMarch).toString()
        "04" -> month = context.resources.getText(R.string.textApril).toString()
        "05" -> month = context.resources.getText(R.string.textMay).toString()
        "06" -> month = context.resources.getText(R.string.textJune).toString()
        "07" -> month = context.resources.getText(R.string.textJuly).toString()
        "08" -> month = context.resources.getText(R.string.textAugust).toString()
        "09" -> month = context.resources.getText(R.string.textSeptember).toString()
        "10" -> month = context.resources.getText(R.string.textOctober).toString()
        "11" -> month = context.resources.getText(R.string.textNovember).toString()
        "12" -> month = context.resources.getText(R.string.textDecember).toString()
    }

    return "$dayOfMonth $month $year"
}

fun getTodayDateAndTime() : String {
    val data = Calendar.getInstance()
    var dayOfMonth = ""
    var month = ""
    var minute = ""
    var second = ""

    if(data.get(Calendar.DAY_OF_MONTH) < 10)
        dayOfMonth = "0"

    if(data.get(Calendar.MONTH) < 10)
        month = "0"

    if(data.get(Calendar.MINUTE) < 10)
        minute = "0"

    if(data.get(Calendar.SECOND) < 10)
        second = "0"

    return "$dayOfMonth${data.get(Calendar.DAY_OF_MONTH)}" +
            "$month${data.get(Calendar.MONTH)+1}" +
            "${data.get(Calendar.YEAR)-2000}." +
            "${data.get(Calendar.HOUR_OF_DAY)}." +
            "$minute${data.get(Calendar.MINUTE)}." +
            "$second${data.get(Calendar.SECOND)}." +
            "${data.get(Calendar.MILLISECOND)}"
}

 fun getDayOfWeek(context: Context) : String {

    val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    var result = "false"

    when(day) {
        1 -> result = context.resources.getText(R.string.textSunday).toString()
        2 -> result = context.resources.getText(R.string.textMonday).toString()
        3 -> result = context.resources.getText(R.string.textTuesday).toString()
        4 -> result = context.resources.getText(R.string.textWednesday).toString()
        5 -> result = context.resources.getText(R.string.textThursday).toString()
        6 -> result = context.resources.getText(R.string.textFriday).toString()
        7 -> result = context.resources.getText(R.string.textSaturday).toString()
    }
    return result
}

fun getFayOfWeekForLanguage(context: Context, day: String) : String{

    var dayFinish = "false"

    when(day) {
        "Воскресенье","Sunday"-> dayFinish = context.resources.getText(R.string.textSunday).toString()
        "Понедельник","Monday"-> dayFinish = context.resources.getText(R.string.textMonday).toString()
        "Вторник","Tuesday"-> dayFinish = context.resources.getText(R.string.textTuesday).toString()
        "Среда","Wednesday"-> dayFinish = context.resources.getText(R.string.textWednesday).toString()
        "Четверг","Thursday"-> dayFinish = context.resources.getText(R.string.textThursday).toString()
        "Пятница","Friday"-> dayFinish = context.resources.getText(R.string.textFriday).toString()
        "Суббота","Saturday"-> dayFinish = context.resources.getText(R.string.textSaturday).toString()
    }

    return dayFinish
}