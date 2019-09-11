package com.sergeytalyzin.diary

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.sergeytalyzin.diary.adapters.MyAdapter
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment: Fragment() {

    private var openWrapperPassword = false
    private var openWrapperPasswordDel = false
    private var errorOfPass = ""
    private var errorOfPassDel = ""
    private lateinit var itemPassword : MenuItem
    private var myHavePassword = false

    private lateinit var v: View

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        (activity as MainActivity).title = resources.getText(R.string.app_name)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        (activity as MainActivity).supportFragmentManager.popBackStack()

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        inflater?.inflate(R.menu.main, menu)
        val itemDescription = menu!!.findItem(R.id.description)
        itemPassword = menu.findItem(R.id.password)

        if(myHavePassword)
            itemPassword.setIcon(R.drawable.lock_close_for_menu)
        if(!myHavePassword)
            itemPassword.setIcon(R.drawable.lock_open_for_menu)

        itemPassword.setOnMenuItemClickListener {

            if(myHavePassword) {
                wrapperDeletePassword.visibility = View.VISIBLE
                openWrapperPasswordDel = true
            }
            else if(!myHavePassword) {
                wrapperPassword.visibility = View.VISIBLE
                openWrapperPassword = true
            }
            true
        }

        itemDescription.setOnMenuItemClickListener {

            Navigation.findNavController(v).navigate(R.id.descriptionFragment)
            true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        v = view

        val myList = mainRecyclerView
        val listAdapter = MyAdapter(context!!)

        val db = DBHelper(context!!)

        listAdapter.setList(db.getAllData())

        if(Password(context!!).getPassFromSharedPreferences() != "")
            myHavePassword = true

        db.close()

        myList.layoutManager = LinearLayoutManager(context!!)
        myList.adapter = listAdapter

        // PASSWORD //

        closeWrapPasswordDel.setOnClickListener {
            openWrapperPasswordDel = false
            wrapperDeletePassword.visibility = View.GONE
            errorsOfPasswordDel.visibility = View.GONE
            checkPassForDelete.setText("", TextView.BufferType.EDITABLE)
            errorOfPassDel = ""
        }

        closeWrapPassword.setOnClickListener {
            openWrapperPassword = false
            pass1.setText("", TextView.BufferType.EDITABLE)
            pass2.setText("", TextView.BufferType.EDITABLE)
            errorOfPass = ""
            errorsOfPassword.visibility = View.GONE
            wrapperPassword.visibility = View.GONE
        }

        savePassword.setOnClickListener {

            val myPass1 = pass1.text.toString()
            val myPass2 = pass2.text.toString()

            if ( checkPassword(myPass1, myPass2) ) {
                itemPassword.setIcon(R.drawable.lock_close_for_menu)
                wrapperPassword.visibility = View.GONE
                pass1.setText("", TextView.BufferType.EDITABLE)
                pass2.setText("", TextView.BufferType.EDITABLE)
                openWrapperPassword = false
                myHavePassword = true
                Password(context!!).savePassInSharedPreferences(myPass1)
                Toast.makeText(context, resources.getText(R.string.textPasswordAdded), Toast.LENGTH_SHORT).show()
            }
        }

        delPassBtn.setOnClickListener {

            if(Password(context!!).getPassFromSharedPreferences() == checkPassForDelete.text.toString()) {
                Password(context!!).deletePassFromSharedPreferences()
                itemPassword.setIcon(R.drawable.lock_open_for_menu)
                wrapperDeletePassword.visibility = View.GONE
                checkPassForDelete.setText("", TextView.BufferType.EDITABLE)
                myHavePassword = false
                openWrapperPasswordDel = false
                errorOfPassDel = ""
                errorsOfPasswordDel.visibility = View.GONE
                Toast.makeText(context, resources.getText(R.string.textPasswordRemoved), Toast.LENGTH_SHORT).show()

            }
            else {
                errorOfPassDel = resources.getText(R.string.textForWrongPassword).toString()
                errorsOfPasswordDel.text = errorOfPassDel
                errorsOfPasswordDel.visibility = View.VISIBLE
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        try{

            openWrapperPassword = savedInstanceState!!.getBoolean("openWrapperPassword")
            openWrapperPasswordDel = savedInstanceState.getBoolean("openWrapperPasswordDel")
            myHavePassword = savedInstanceState.getBoolean("myHavePassword")

            errorOfPass = savedInstanceState.getString("errorOfPass")!!
            errorOfPassDel = savedInstanceState.getString("errorOfPassDel")!!

            if(openWrapperPassword) {
                wrapperPassword.visibility = View.VISIBLE
                if(errorOfPass != "") {
                    errorsOfPassword.visibility = View.VISIBLE
                    errorsOfPassword.text = errorOfPass
                }
            }
            else if (openWrapperPasswordDel) {
                wrapperDeletePassword.visibility = View.VISIBLE
                if(errorOfPassDel != "") {
                    errorsOfPasswordDel.visibility = View.VISIBLE
                    errorsOfPasswordDel.text = errorOfPassDel
                }
            }
        }
        catch (e: Exception) { }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)


        outState.putBoolean("openWrapperPassword", openWrapperPassword)
        outState.putBoolean("openWrapperPasswordDel", openWrapperPasswordDel)
        outState.putBoolean("myHavePassword", myHavePassword)

        outState.putString("errorOfPass", errorOfPass)
        outState.putString("errorOfPassDel", errorOfPassDel)

    }

    private fun checkPassword(firstPassword: String, secondaryPassword: String) : Boolean {

        if(firstPassword.length > 3) {
            for(i in firstPassword) {
                if(i == ' ') {
                    errorOfPass = resources.getText(R.string.textUsingSpace).toString()
                    errorsOfPassword.apply { text = errorOfPass }
                    errorsOfPassword.visibility = View.VISIBLE
                    return false
                }
            }
            if (firstPassword != secondaryPassword ) {
                errorOfPass = resources.getText(R.string.textPasswordsDoNotMatch).toString()
                errorsOfPassword.apply { text = errorOfPass }
                errorsOfPassword.visibility = View.VISIBLE
                return false
            }
        }
        else {
            errorOfPass = resources.getText(R.string.textPasswordNotShorterThanFourCharacters).toString()
            errorsOfPassword.apply { text = errorOfPass }
            errorsOfPassword.visibility = View.VISIBLE
            return false
        }

        errorOfPass = ""
        errorsOfPassword.visibility = View.GONE
        return true
    }

}
