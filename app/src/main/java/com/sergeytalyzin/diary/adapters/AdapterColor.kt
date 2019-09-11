package com.sergeytalyzin.diary.adapters

import android.graphics.Color
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.sergeytalyzin.diary.AddPageFragment
import com.sergeytalyzin.diary.MainActivity
import com.sergeytalyzin.diary.R

class ViewHolderColor(view: View, private val activity: FragmentActivity) : RecyclerView.ViewHolder(view) {

    private val itemColorOfDay = view.findViewById<CardView>(R.id.itemColorOfDay)

    fun bind(color: String) {

        itemColorOfDay.setCardBackgroundColor(Color.parseColor(color))

        itemColorOfDay.setOnClickListener {
            val host: NavHostFragment = (activity as MainActivity).supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val fragment = host.childFragmentManager.fragments[0]
            val p = fragment as AddPageFragment
            p.changeColor(color)
        }
    }
}

class AdapterColor(private val activity: FragmentActivity) : RecyclerView.Adapter<ViewHolderColor>() {

    private val colors = arrayOf("#66b3ff", "#4dff4d", "#ff66cc", "#ffa64d", "#e066ff", "#db70b8", "#ffff66", "#ff66a3", "#66ffff",
        "#ff6666", "#b3b3ff", "#a64dff", "#ff8c66", "#3333ff", "#d9ff66", "#8c8cd9", "#66ff1a",
        "#ff33ff", "#00ffff", "#3385ff", "#ff3333", "#aaff80")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderColor {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)

        return ViewHolderColor(view, activity)
    }

    override fun onBindViewHolder(holder: ViewHolderColor, position: Int) {
        holder.bind(colors[position])
    }

    override fun getItemCount() = colors.size


}