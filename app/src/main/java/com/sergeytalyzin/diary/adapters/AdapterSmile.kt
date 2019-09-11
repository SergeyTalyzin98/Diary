package com.sergeytalyzin.diary.adapters

import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import com.sergeytalyzin.diary.AddPageFragment
import com.sergeytalyzin.diary.MainActivity
import com.sergeytalyzin.diary.R

class ViewHolderSmile(view: View, private val activity: FragmentActivity) : RecyclerView.ViewHolder(view) {

    private val forSmile = view.findViewById<TextView>(R.id.forSmile)

    fun bind(smile: Int) {

        val str = String(Character.toChars(smile))

        forSmile.apply { text = str }

        forSmile.setOnClickListener {
            val host: NavHostFragment = (activity as MainActivity).supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val fragment = host.childFragmentManager.fragments[0]
            val p = fragment as AddPageFragment
            p.addSmileInEditText(str)
        }
    }
}

class AdapterColorSmile(private val activity: FragmentActivity) : RecyclerView.Adapter<ViewHolderSmile>() {

    private val smiles = arrayOf(0x1F601, 0x1F602, 0x1F603, 0x1F604, 0x1F605,
        0x1F606, 0x1F609, 0x1F60A, 0x1F60B, 0x1F60C, 0x1F60D, 0x1F60F, 0x1F612,
        0x1F613, 0x1F614, 0x1F616, 0x1F618, 0x1F61A, 0x1F61C, 0x1F61D, 0x1F61E,
        0x1F620, 0x1F621, 0x1F622, 0x1F623, 0x1F624, 0x1F625, 0x1F628, 0x1F629,
        0x1F62A, 0x1F62B, 0x1F62D, 0x1F630, 0x1F631, 0x1F632, 0x1F633, 0x1F634,
        0x1F635, 0x1F637, 0x1F638, 0x1F639, 0x1F63A, 0x1F63B, 0x1F63C, 0x1F63D,
        0x1F63E, 0x1F63F, 0x1F640, 0x1F645, 0x1F646, 0x1F647, 0x1F648, 0x1F649,
        0x1F64A, 0x1F64B, 0x1F64C, 0x1F64D, 0x1F64E, 0x1F64F)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSmile {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_smile, parent, false)

        return ViewHolderSmile(view, activity)
    }

    override fun onBindViewHolder(holder: ViewHolderSmile, position: Int) {
        holder.bind(smiles[position])
    }

    override fun getItemCount() = smiles.size


}