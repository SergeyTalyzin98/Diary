package com.sergeytalyzin.diary


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.gms.ads.AdRequest
import com.sergeytalyzin.diary.adapters.AdapterPhotoDetails
import kotlinx.android.synthetic.main.fragment_details_of_day.*
import java.io.File

class DetailsOfDayFragment : Fragment() {

    private var photos = mutableListOf<String>()
    private var idOfDay: Int = 0
    private lateinit var v: View

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        (activity as MainActivity).title = resources.getText(R.string.textViewMode)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_details_of_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val mAdView = adViewDetails
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        v = view

        idOfDay = arguments!!.getInt("idOfDay")

        val db = DBHelper(context!!)

        val day = db.getData(idOfDay)!!

        db.close()

        wrapperDetailsFr.setBackgroundColor(Color.parseColor(day.color))

        for(photo in day.photos) {
            photos.add(photo[1])
        }

        val recyclerViewForPhotosDetails = recyclerViewForPhotosDetails
        val adapterForPhotoDetails = AdapterPhotoDetails(context!!, activity!!)
        recyclerViewForPhotosDetails.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewForPhotosDetails.adapter = adapterForPhotoDetails

        val myCustomDay = "${getFayOfWeekForLanguage(context!!, day.day)},"
        val myCustomDate = " ${getTodayDateForShow(context!!, day.date)}"

        dayDetails.text = myCustomDay
        dateDetails.text = myCustomDate

        if(photos.size > 0) {
            adapterForPhotoDetails.setListOfPhoto(photos)
            val countPhotos = " ${photos.size}"
            countPhotoDetails.visibility = View.VISIBLE
            countPhotoDetails.apply { text = countPhotos }
        }
        else{
            descriptionOfPhotoDetails.apply{ text = resources.getText(R.string.textNoPhotos) }
        }

        if(day.text == "") {
            descriptionOfTextDetails.apply { text = resources.getText(R.string.textNoRecords) }
            textDetails.visibility = View.GONE
        }
        else {
            textDetails.text = day.text
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.detailsofdayfragment, menu)
        val itemDeleteDayDetails = menu!!.findItem(R.id.deleteDayDetails)

        itemDeleteDayDetails.setOnMenuItemClickListener {

            val windowDelete = AlertDialog.Builder(context)
            windowDelete.setIcon(R.drawable.ic_delete_for_window)
            windowDelete.setTitle(resources.getText(R.string.textDeletion))
            windowDelete.setMessage(resources.getText(R.string.textDeleteRecord))
            windowDelete.setNeutralButton(resources.getText(R.string.textCancel)) {
                    dialog, which ->

            }
            windowDelete.setPositiveButton(resources.getText(R.string.textOk)) {
                    dialog, which ->

                val db = DBHelper(context!!)

                for(p in photos) {
                    File(p).delete()
                }

                db.deleteDay(idOfDay)
                db.close()

                Toast.makeText(context!!, resources.getText(R.string.textRecordDeleted), Toast.LENGTH_SHORT).show()

                Navigation.findNavController(v).navigate(R.id.action_detailsOfDayFragment_to_mainFragment)
            }

            windowDelete.show()

            true }
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun viewPhoto(fail: String) {

        val file = File(fail)

        val vPhoto = Intent(Intent.ACTION_VIEW)
        vPhoto.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val uri = FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID, file)

        vPhoto.data = uri

        val pm = activity!!.packageManager
        if (vPhoto.resolveActivity(pm) != null) {
            startActivity(vPhoto)
        }
    }
}
