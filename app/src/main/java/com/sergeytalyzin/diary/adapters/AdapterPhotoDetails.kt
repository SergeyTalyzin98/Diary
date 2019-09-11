package com.sergeytalyzin.diary.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.NavHostFragment
import com.sergeytalyzin.diary.DetailsOfDayFragment
import com.sergeytalyzin.diary.MainActivity
import com.sergeytalyzin.diary.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class ViewHolderPhotoDetails(val context: Context, view: View, private val activity: FragmentActivity) : RecyclerView.ViewHolder(view) {

    private val photo = view.findViewById<ImageView>(R.id.photo)

    fun bind(fail: String) {

        val image = File(fail)
        val finishImg = decodeFile(image)

        GlobalScope.launch(Dispatchers.Main) {
            photo.setImageBitmap(finishImg)
        }

        photo.setOnClickListener {
            val host: NavHostFragment = (activity as MainActivity).supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val fragment = host.childFragmentManager.fragments[0]
            val p = fragment as DetailsOfDayFragment
            p.viewPhoto(fail)
        }
    }

    private fun decodeFile(f: File): Bitmap? {
        try {
            // Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)

            // The new size we want to scale to
            val requiredSize = 50

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= requiredSize && o.outHeight / scale / 2 >= requiredSize) {
                scale *= 2
            }

            // Decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: FileNotFoundException) { }

        return null
    }
}

class AdapterPhotoDetails(val context: Context, private val activity: FragmentActivity) : RecyclerView.Adapter<ViewHolderPhotoDetails>() {

    private var listOfPhoto = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPhotoDetails {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)

        return ViewHolderPhotoDetails(context, view, activity)
    }

    override fun onBindViewHolder(holder: ViewHolderPhotoDetails, position: Int) {
        GlobalScope.launch {
            holder.bind(listOfPhoto[position])
        }
    }

    override fun getItemCount() = listOfPhoto.size

    fun setListOfPhoto(list: List<String>) {
        listOfPhoto.clear()
        listOfPhoto.addAll(list)
        notifyDataSetChanged()
    }
}