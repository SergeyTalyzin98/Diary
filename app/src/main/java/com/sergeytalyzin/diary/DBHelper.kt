package com.sergeytalyzin.diary

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, dbName: String = "dataBaseOfDiary", version: Int = 1) : SQLiteOpenHelper(context, dbName, null, version) {

    private val db = writableDatabase

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(
            "CREATE TABLE pages (id INTEGER PRIMARY KEY AUTOINCREMENT, day TEXT, date TEXT, text TEXT, color TEXT);")
        sqLiteDatabase.execSQL("CREATE TABLE photos (id INTEGER PRIMARY KEY AUTOINCREMENT, idOfDay TEXT, path TEXT);")
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, j: Int) {

    }

//    fun getCount() : Int {
//        val cur = this.db.rawQuery("select Count(*) as count from pages", null)
//        cur.moveToFirst()
//        val result = cur.getInt(cur.getColumnIndexOrThrow("count"))
//        cur.close()
//        return result
//    }

    fun insertDataInPages(day: String, date: String, text: String, color: String) {

        val values = ContentValues()
        values.put("day", day)
        values.put("date", date)
        values.put("text", text)
        values.put("color", color)
        this.db.insert("pages", null, values)
    }

    fun insertDataInPhotos(idOfDay: Int, path: List<String>) {

        val values = ContentValues()

        for(value in path) {
            values.put("idOfDay", idOfDay.toString())
            values.put("path", value)
            this.db.insert("photos", null, values)
        }
    }

    fun getData(position: Int) : Day? {

        val photosFinish = mutableListOf<Array<String>>()

        val data = this.db.query("pages", arrayOf("day", "date", "text", "color"), "id=?",
            arrayOf(position.toString()), null, null, null)
        val photos = this.db.query("photos", arrayOf("id", "path"), "idOfDay=?",
            arrayOf(position.toString()), null, null, null)

        data.moveToFirst()
        photos.moveToFirst()

        if (data.count > 0) {

            photosFinish.clear()

            while(!photos.isAfterLast) {
                photosFinish.add(arrayOf(
                    photos.getString( photos.getColumnIndexOrThrow("id") ),
                    photos.getString( photos.getColumnIndexOrThrow("path") )
                ))

                photos.moveToNext()
            }

            val day = Day(0, data.getString( data.getColumnIndexOrThrow("day") ),
                data.getString( data.getColumnIndexOrThrow("date") ),
                data.getString( data.getColumnIndexOrThrow("text") ),
                data.getString( data.getColumnIndexOrThrow("color") ),
                photosFinish)

            data.close()
            photos.close()
            return day
        }

        return null
    }

    fun getAllData() : List<Day> {

        val photosFinish = mutableListOf<Array<String>>()
        val days = mutableListOf<Day>()
        lateinit var id: String
        lateinit var day: String
        lateinit var date: String
        lateinit var text: String
        lateinit var color: String
        var countPhotos = 0

        val data = this.db.query("pages", null, null,
            null, null, null, null)

        data.moveToFirst()

        if (data.count > 0) {

            while (!data.isAfterLast) {

                id = data.getString( data.getColumnIndexOrThrow("id"))
                day = data.getString( data.getColumnIndexOrThrow("day"))
                date = data.getString( data.getColumnIndexOrThrow("date"))
                text = data.getString( data.getColumnIndexOrThrow("text"))
                color = data.getString( data.getColumnIndexOrThrow("color"))

                val photos = this.db.query("photos", arrayOf("id"), "idOfDay=?",
                    arrayOf(id), null, null, null)

                photos.moveToFirst()

                photosFinish.clear()

                while(!photos.isAfterLast) {

                    countPhotos++

                    photos.moveToNext()
                }

                photos.close()

                val dayObj = Day(id.toInt(), day, date, text, color, photosFinish, countPhotos)

                days.add(dayObj)

                countPhotos = 0

                data.moveToNext()
            }

            data.close()
            return days
        }

        return days
    }

    fun getOnlyPhotoOfDay(id: Int) : MutableList<Array<String>> {

        val photosFinish = mutableListOf<Array<String>>()

        val photos = this.db.query("photos", arrayOf("id", "path"), "idOfDay=?",
            arrayOf(id.toString()), null, null, null)

        photos.moveToFirst()

        while(!photos.isAfterLast) {
            photosFinish.add(arrayOf(
                photos.getString( photos.getColumnIndexOrThrow("id") ),
                photos.getString( photos.getColumnIndexOrThrow("path") )
            ))

            photos.moveToNext()
        }

        photos.close()
        return photosFinish
    }

    fun deleteDay(id: Int) {
        this.db.delete("pages", "id = $id", null)
        this.db.delete("photos", "idOfDay = $id", null)
    }

    fun getLastDay() : Int {
        val days = this.db.query("pages", null, null,
            null, null, null, null)

        days.moveToLast()

        val id = days.getString( days.getColumnIndexOrThrow("id"))

        days.close()

        return id.toInt()
    }

    fun deletePhoto(id: String) = this.db.delete("photos", "id = $id", null)

    fun update(id: Int, text: String, color: String) {
        val values = ContentValues()
        values.put("text", text)
        values.put("color", color)
        this.db.update("pages", values, "id = $id", null)
    }
}