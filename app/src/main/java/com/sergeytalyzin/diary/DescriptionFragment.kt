package com.sergeytalyzin.diary


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.fragment_description.*


class DescriptionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as MainActivity).title = resources.getText(R.string.descriptionOfApp)
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val mAdView = adViewDescription
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        textOfDescription.text = resources.getText(R.string.textForDescriptionPage)

    }
}
