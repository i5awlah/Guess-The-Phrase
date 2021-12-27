package com.example.guessthephrase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context,"phrases.db", null, 1) {
    private val sqLiteDatabase: SQLiteDatabase = writableDatabase

    override fun onCreate(db: SQLiteDatabase?) {
        if(db != null){
            db.execSQL("create table phrase (phrase text)")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun saveData(phrase: String) : Long {
        val contentValues = ContentValues()
        contentValues.put("phrase", phrase)
        return sqLiteDatabase.insert("phrase", null, contentValues)
    }

    fun readData() : ArrayList<String> {
        var phrases = arrayListOf<String>()
        val cursor: Cursor = sqLiteDatabase.rawQuery("SELECT * FROM phrase", null)

        if (cursor.count == 0) {
            println("No data found!")
        } else {
            while (cursor.moveToNext()) {
                val phrase = cursor.getString(0)
                phrases.add(phrase)
            }
        }
        return phrases
    }
}