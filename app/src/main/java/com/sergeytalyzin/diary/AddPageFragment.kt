
package com.sergeytalyzin.diary

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.gms.ads.AdRequest
import com.sergeytalyzin.diary.adapters.AdapterColor
import com.sergeytalyzin.diary.adapters.AdapterColorSmile
import com.sergeytalyzin.diary.adapters.AdapterPhoto
import kotlinx.android.synthetic.main.fragment_add_page.*
import java.io.File
import java.io.FileOutputStream

class AddPageFragment : Fragment(), OnBackPressedListener {

    // поле для хранения фото (пути к файлу)
    private var pathToPhotoFail = ""
    // лист для хранения сделанных фото, которые еще не сохранены в бд
    private var newsPhoto = mutableListOf<String>()
    // лист для хранения сделанных фото, которые сохранены в дб
    private var readyPhotos = mutableListOf<String>()
    // лист для хранения всех сделанных фото
    private var allPhotos = mutableListOf<String>()
    // адаптер для ресайкл, который выводит фото в AddPageFragment
    private lateinit var adapter: AdapterPhoto
    // поле для хранения id, которое получается по ключу из аргументов
    private var idOfDay = 0
    // поле для хранения цвета
    private var color = "#ffffff"
    private var changeOrAdd = ""
    private var textInEditTextOnStart = ""
    private lateinit var v: View

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        (activity as MainActivity).title = resources.getText(R.string.textEditing)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_page, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.addpagefragment, menu)
        val itemDeleteDayAddPage = menu!!.findItem(R.id.deleteDayAddPage)

        if(changeOrAdd == "change") {
            itemDeleteDayAddPage.isVisible = true
        }

        itemDeleteDayAddPage.setOnMenuItemClickListener {

            val windowDelete = AlertDialog.Builder(context)
            windowDelete.setIcon(R.drawable.ic_delete_for_window)
            windowDelete.setTitle(resources.getText(R.string.textDeletion))
            windowDelete.setMessage(resources.getText(R.string.textDeleteRecord))
            windowDelete.setNeutralButton(resources.getText(R.string.textCancel)) { dialog, which -> }

            windowDelete.setPositiveButton(resources.getText(R.string.textOk)) {
                    dialog, which ->

                val db = DBHelper(context!!)

                for(p in readyPhotos) {
                    File(p).delete()
                }

                for(p in newsPhoto) {
                    File(p).delete()
                }

                db.deleteDay(idOfDay)
                db.close()

                Toast.makeText(context!!, resources.getText(R.string.textRecordDeleted), Toast.LENGTH_SHORT).show()

                Navigation.findNavController(v).navigate(R.id.action_addPageFragment_to_mainFragment)
            }

            windowDelete.show()
            true
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    fun addSmileInEditText(smile: String) {
        textChangeAndAdd.text.insert(textChangeAndAdd.selectionStart, smile)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*
        получение аргумента(строка) по ключу.
        "change" - день создан, редактирования дня
        "add" - день не создан, создания дня
        */

        val mAdView = adViewAddPage
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        v = view

        changeOrAdd = arguments!!.getString("changeOrAdd")!!

        val recyclerPhoto = recyclerViewForPhotos
        adapter = AdapterPhoto(activity!!)
        recyclerPhoto.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        recyclerPhoto.adapter = adapter

        val recyclerColor = recyclerViewForColors
        val adapterForColor = AdapterColor(activity!!)
        recyclerColor.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        recyclerColor.adapter = adapterForColor

        val recyclerSmile = recyclerViewForSmiles
        val adapterForSmile = AdapterColorSmile(activity!!)
        recyclerSmile.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
        recyclerSmile.adapter = adapterForSmile

        when(changeOrAdd) {
            // если день уже создан и нужно что-то изменить
            "change" -> {
                idOfDay = arguments!!.getInt("id")
                change()
            }

            // если день еще не создан
            "add" -> adding()
        }

        textChangeAndAdd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.toString() != textInEditTextOnStart) {
                    saveBtn.isEnabled = true
                }
            }
        })

        makePhoto.setOnClickListener {
            // если была нажата кнопка сделать фото
            dispatchTakePictureIntent()
        }

        addPhoto.setOnClickListener {
            // если была нажата кнопка добавить фото
            createIntentForAddPhoto()
        }

    }

    private fun saveFunction(changeOrAdd: String = "", id: Int = 0) {

        val db = DBHelper(activity!!)

        if (changeOrAdd == "add") {
            db.insertDataInPages(
                getDayOfWeek(context!!),
                getTodayDate(),
                textChangeAndAdd.text.toString(),
                this.color
            )

            if (newsPhoto.isNotEmpty()) {
                db.insertDataInPhotos(db.getLastDay(), newsPhoto)
                readyPhotos.addAll(newsPhoto)
                newsPhoto.clear()
                upgradeList(readyPhotos, newsPhoto)
            }

            db.close()

            Toast.makeText(context!!, resources.getText(R.string.textRecordSaved), Toast.LENGTH_SHORT).show()

            Navigation.findNavController(v).navigate(R.id.action_addPageFragment_to_mainFragment)

        } else if (changeOrAdd == "change") {

            db.update(
                id, textChangeAndAdd.text.toString(),
                this.color
            )

            if (newsPhoto.isNotEmpty()) {
                db.insertDataInPhotos(idOfDay, newsPhoto)
                newsPhoto.clear()
                readyPhotos.clear()
                val photosOfDay = db.getOnlyPhotoOfDay(idOfDay)
                // добавление фото в список из бд
                for (photo in photosOfDay) {
                    readyPhotos.add(photo[1])
                }
                // обновления ресайкла, который выводит фото в этом фрагменте
                upgradeList(readyPhotos, newsPhoto)
            }

            db.close()

            Toast.makeText(context!!, resources.getText(R.string.textRecordChanged), Toast.LENGTH_SHORT).show()

            Navigation.findNavController(v).navigate(R.id.action_addPageFragment_to_mainFragment)
        }
    }

    override fun onBackPressed() {

        val windowSave = AlertDialog.Builder(context)
        windowSave.setIcon(R.drawable.ic_outline)
        windowSave.setTitle(resources.getText(R.string.textExit))
        windowSave.setMessage(resources.getText(R.string.textChangesWillNotBeSaved))
        windowSave.setNeutralButton(resources.getText(R.string.textCancel)) {
                dialog, which ->

        }
        windowSave.setPositiveButton(resources.getText(R.string.textExitApplication)) {
                dialog, which ->

            for(photo in newsPhoto) {
                File(photo).delete()
            }

            (activity as MainActivity).myBackFun()

        }

        if( saveBtn.isEnabled ) {
            windowSave.show()
        }
        else {
            (activity as MainActivity).myBackFun()
        }
    }

    fun changeColor(color: String) {
        this.color = color
        wrapperOfAddPageFragment.setBackgroundColor(Color.parseColor(this.color))
        saveBtn.isEnabled = true
    }

    fun removalProcess(fail: String) {
        /*
        метод ищет фото в списках(newsPhoto, readyPhotos),
        после запускает диалог удаление.
        */
        val window = AlertDialog.Builder(context)
        window.setIcon(R.drawable.ic_delete_for_window)
        window.setTitle(resources.getText(R.string.textDeletion))
        window.setMessage(resources.getText(R.string.textImpossiblePhoto))
        window.setNeutralButton(resources.getText(R.string.textCancel)) {
                dialog, which -> dialog.cancel()
        }

        for((index, f) in newsPhoto.withIndex()) {
            if (f == fail) {
                window.setPositiveButton(resources.getText(R.string.textDelete)) {
                        dialog, which ->
                        newsPhoto.removeAt(index)
                        File(fail).delete()
                        upgradeList(readyPhotos, newsPhoto)
                }
                window.show()
            }
        }

        for((index, f) in readyPhotos.withIndex()) {
            if (f == fail) {
                window.setPositiveButton(resources.getText(R.string.textDelete)) {
                        dialog, which ->
                    readyPhotos.removeAt(index)

                    val db = DBHelper(context!!)
                    val photosOfDay = db.getOnlyPhotoOfDay(idOfDay)

                    for(idAndPhoto in photosOfDay) {
                        if(fail == idAndPhoto[1]) {
                            db.deletePhoto(idAndPhoto[0])
                            break
                        }
                    }
                    db.close()
                    File(fail).delete()
                    upgradeList(readyPhotos, newsPhoto)
                }
                window.show()
            }
        }
    }

    private fun adding() {
        /*
        Срабатывает если данные ключа "changeOrAdd" равны "add"
        */

        // заполнение View данными
        val myCustomDay = "${getDayOfWeek(context!!)},"
        val myCustomDate = " ${getTodayDateForShow(context!!, getTodayDate())}"

        dayChangeAndAdd.text = myCustomDay
        dateChangeAndAdd.text = myCustomDate

        upgradeList(readyPhotos, newsPhoto)

        saveBtn.setOnClickListener{
            // если была нажата кнопка сохранить выполняется этот код
            saveFunction(changeOrAdd)
        }
    }

    private fun change() {
        /* Срабатывает если данные ключа "changeOrAdd" равны "change" */

        // получения объкта класса Day, который содержит все данные дня
        val db = DBHelper(activity!!)

        val day = db.getData(idOfDay)

        db.close()

        this.color = day!!.color

        // заполнение View данными из бд
        val myCustomDay = "${getFayOfWeekForLanguage(context!!, day.day)},"
        val myCustomDate = " ${getTodayDateForShow(context!!, day.date)}"

        dayChangeAndAdd.text = myCustomDay
        dateChangeAndAdd.text = myCustomDate

        textChangeAndAdd.setText(day.text, TextView.BufferType.EDITABLE)
        textInEditTextOnStart = day.text
        wrapperOfAddPageFragment.setBackgroundColor(Color.parseColor(this.color))

        // добавление фото в список из бд
        for(photo in day.photos) {
            readyPhotos.add(photo[1])
        }

        upgradeList(readyPhotos, newsPhoto)

        saveBtn.setOnClickListener{
            // если была нажата кнопка сохранить выполняется этот код
            saveFunction(changeOrAdd, idOfDay)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        /*
        Выполняется при старте фрагмента,
        заполняет поля данными из savedInstanceState,
        если они там есть
        */
        super.onActivityCreated(savedInstanceState)

        try{
            val photos = savedInstanceState!!.getStringArrayList("newPhotos")

            pathToPhotoFail = savedInstanceState.getString("pathToPhoto")!!
            color = savedInstanceState.getString("colorOfDay")!!
            saveBtn.isEnabled = savedInstanceState.getBoolean("enabledBtn")

            newsPhoto.addAll(photos!!)

            upgradeList(readyPhotos, newsPhoto)
            wrapperOfAddPageFragment.setBackgroundColor(Color.parseColor(this.color))

        }
        catch (e: Exception) { }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        /*
        Сохраняет данные в outState, после чего данные достаются в методе onActivityCreated,
        если фрагментне не удален из памяти.
        Используется для сохранения данных при повороте экрана.
        */
        super.onSaveInstanceState(outState)

        val photos = ArrayList<String>()

        photos.addAll(newsPhoto)

        outState.putStringArrayList("newPhotos", photos)
        outState.putString("pathToPhoto", pathToPhotoFail)
        outState.putString("colorOfDay", this.color)
        outState.putBoolean("enabledBtn", saveBtn.isEnabled)
    }

    private fun upgradeList(readyPhotos: List<String>, newsPhoto: List<String>) {
        /* Обноаляет список фото */

        allPhotos.addAll(readyPhotos)
        allPhotos.addAll(newsPhoto)

        adapter.setListOfPhoto(allPhotos)

        val count = " ${allPhotos.size}"

        countPhoto.apply { text = count }

        allPhotos.clear()
    }

    private fun createImageFile(): File {
        /*
        Создание файла для картинки
        */
        val timeStamp: String = getTodayDateAndTime()
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun dispatchTakePictureIntent() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            try {

                takePictureIntent.resolveActivity(activity!!.packageManager)?.also {

                    val photoFile: File? = try {

                        createImageFile()

                    } catch (e: Exception) {
                        null
                    }

                    photoFile?.also {
                        pathToPhotoFail = photoFile.absolutePath

                        val uri = if(Build.VERSION.SDK_INT >= 24)
                            FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID, photoFile)
                        else
                            Uri.fromFile(photoFile)

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                        startActivityForResult(takePictureIntent, 1)
                    }
                }
            }catch (e: Exception) { }
        }
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

    private fun createIntentForAddPhoto() {
        val addPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(addPhotoIntent, 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // если фото было создано
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Добавляем картинку в список новых картинок
            newsPhoto.add(pathToPhotoFail)
            saveBtn.isEnabled = true
            pathToPhotoFail = ""
            upgradeList(readyPhotos, newsPhoto)
        }
        // если фото не было создано, удаляем ненужный файл
        else if(requestCode == 1 && resultCode != RESULT_OK)
            File(pathToPhotoFail).delete()

        /*--------------------------------------------------------------------------*/

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
//            val picturePath: String
//            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//
//            //val img = context!!.contentResolver.openOutputStream(data.data!!)
//            val cursor = context!!.contentResolver.query(
//                data.data!!,
//                filePathColumn, null, null, null
//            ) as Cursor
//
//            cursor.moveToFirst()
//            picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
//            cursor.close()

            val bm = BitmapFactory.decodeStream(context!!.contentResolver.openInputStream(data.data!!))
            val newImage = createImageFile()
            val out = FileOutputStream(newImage)
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
            newsPhoto.add(newImage.absolutePath)
            saveBtn.isEnabled = true
            upgradeList(readyPhotos, newsPhoto)
        }
    }
}