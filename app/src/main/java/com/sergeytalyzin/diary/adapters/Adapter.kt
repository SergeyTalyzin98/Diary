package com.sergeytalyzin.diary.adapters

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import com.sergeytalyzin.diary.*

class MyViewHolder(private val context: Context, private val view: View) : RecyclerView.ViewHolder(view) {

    private val arguments = Bundle()
    private val dayOfItem = view.findViewById<TextView>(R.id.day_of_item)
    private val dateOfItem = view.findViewById<TextView>(R.id.date_of_item)
    private val editOrView = view.findViewById<ImageView>(R.id.editOrView)
    private val wrapperCard = view.findViewById<CardView>(R.id.wrapper_card)
    private val textOfDayInMainItem = view.findViewById<TextView>(R.id.textOfDayInMainItem)
    private val countPhotoItem = view.findViewById<TextView>(R.id.countPhotoItem)
    private val dayTodayInAddItem = view.findViewById<TextView>(R.id.dayTodayInAddItem)
    private val dateTodayInAddItem = view.findViewById<TextView>(R.id.dateTodayInAddItem)

    private fun changeText(text: String) : String {

        var newText = ""

        if(text == "")
            return context.resources.getText(R.string.textNoRecords).toString()

        for((index, c) in text.withIndex()) {

            if(index > 35) {
                newText += "..."
                break
            }

            newText += c
        }

        return newText
    }

    fun bind(day: Day?, viewType: Int) {

        // если карточка для добавления записи
        if(viewType == 0) {

            val textToday = "${context.resources.getText(R.string.textToday)} ${getDayOfWeek(
                context
            )}"

            dayTodayInAddItem.apply { text =  textToday }
            dateTodayInAddItem.apply { text =
                getTodayDateForShow(context, getTodayDate())
            }

            arguments.putString("changeOrAdd", "add")
            view.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.addPageFragment, arguments))
        }

        // если карточка с созданным днем
        else if(viewType == 1) {
            wrapperCard.setCardBackgroundColor(Color.parseColor(day!!.color))

            val myCustomDate = " ${getTodayDateForShow(context, day.date)}"
            dateOfItem.apply { text = myCustomDate }

            textOfDayInMainItem.text = changeText(day.text)

            countPhotoItem.text = day.countPhotos.toString()

            // если запись создана сегодня, которую еще можно редактировать
            if(getTodayDate() == day.date) {
                val myCustomDay = "${context.resources.getText(R.string.textToday)},"
                dayOfItem.apply { text = myCustomDay }
                editOrView.setImageResource(R.drawable.mode_edit)

                arguments.putString("changeOrAdd", "change")
                arguments.putInt("id", day.id)

                view.setOnClickListener{
                    Navigation.findNavController(it).navigate(R.id.addPageFragment, arguments)
                }

            }
            // если запись создана в прошлые дни
            else {
                val myCustomDayOld = "${getFayOfWeekForLanguage(context, day.day)},"
                dayOfItem.apply { text = myCustomDayOld }
                editOrView.setImageResource(R.drawable.eye)

                arguments.putInt("idOfDay", day.id)

                view.setOnClickListener{
                    Navigation.findNavController(it).navigate(R.id.detailsOfDayFragment, arguments)
                }
            }
        }
    }
}

class MyAdapter(private val context: Context) : RecyclerView.Adapter<MyViewHolder>() {

    private val listOfDays = mutableListOf<Day>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view: View

        if(viewType == 0) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_for_add, parent, false)
        }
        else{
            view = LayoutInflater.from( parent.context ).inflate(R.layout.item, parent, false)
        }

        return MyViewHolder(context, view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (getItemViewType(position) == 0) holder.bind(day = null, viewType = 0)

        else {
            // получаем поочередно позицию записи с конца
            val pos = (listOfDays.size - 1) - (position - ifDoneLastPageToday(listOfDays))

            // получаем данные с таюлицы по айди
            val day = listOfDays[pos]

            // передаем все необходимые данные в мотод, который вставит все необходимое в item
            holder.bind(day, 1)
        }
    }

    override fun getItemCount() = listOfDays.size + ifDoneLastPageToday(listOfDays)

    override fun getItemViewType(position: Int): Int {

        if(position == 0) {
            // Сегодня записи нет
            if(ifDoneLastPageToday(listOfDays) == 1) return 0
        }
        // Сегодня запись есть
        return 1
    }

    fun setList(list: List<Day>) {
        listOfDays.clear()
        listOfDays.addAll(list)
        notifyDataSetChanged()
    }

    private fun ifDoneLastPageToday(list: List<Day>) : Int {

        try{
            // Получение даты последней записи
            val size = list.size

            if (size == 0) return 1

            val dateOfLastDay = list[size-1].date

            // Сегодня запись создана
            if(dateOfLastDay == getTodayDate()) return 0

        }catch (e: Exception) {
            return 1
        }
        // Запись сегодня не создана
        return 1
    }
}