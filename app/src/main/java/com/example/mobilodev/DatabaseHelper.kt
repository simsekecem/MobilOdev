package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "mobilyazilimfinal.db"
        const val DATABASE_VERSION = 1

        // Users Table
        const val TABLE_USERS = "Users"
        const val COLUMN_USER_ID = "userID"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_NAME = "name"
        const val COLUMN_PROFILE_PHOTO = "profilePhoto"

        // Places Table
        const val TABLE_PLACES = "Places"
        const val COLUMN_PLACE_ID = "placeID"
        const val COLUMN_PLACE_NAME = "name"
        const val COLUMN_PLACE_DESCRIPTION = "description"
        const val COLUMN_PLACE_IMAGE = "imagePath"
        const val COLUMN_PLACE_USER_ID = "userID"

        // Ratings Table
        const val TABLE_RATINGS = "Ratings"
        const val COLUMN_RATING_ID = "ratingID"
        const val COLUMN_PLACE_ID_FK = "placeID"
        const val COLUMN_USER_ID_FK = "userID"
        const val COLUMN_RATING = "rating"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Users Table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_USERS (" +
                    "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_USERNAME TEXT UNIQUE, " +
                    "$COLUMN_PASSWORD TEXT, " +
                    "$COLUMN_NAME TEXT, " +
                    "$COLUMN_PROFILE_PHOTO TEXT)"
        )

        // Places Table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_PLACES (" +
                    "$COLUMN_PLACE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_PLACE_NAME TEXT UNIQUE, " +
                    "$COLUMN_PLACE_DESCRIPTION TEXT, " +
                    "$COLUMN_PLACE_IMAGE TEXT, " +
                    "$COLUMN_PLACE_USER_ID INTEGER, " +
                    "FOREIGN KEY ($COLUMN_PLACE_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID))"
        )

        // Ratings Table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_RATINGS (" +
                    "$COLUMN_RATING_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_USER_ID_FK INTEGER, " +
                    "$COLUMN_PLACE_ID_FK INTEGER, " +
                    "$COLUMN_RATING INTEGER, " +
                    "FOREIGN KEY ($COLUMN_USER_ID_FK) REFERENCES $TABLE_USERS($COLUMN_USER_ID), " +
                    "FOREIGN KEY ($COLUMN_PLACE_ID_FK) REFERENCES $TABLE_PLACES($COLUMN_PLACE_ID))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLACES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RATINGS")
        onCreate(db)
    }

    // Register User
    fun registerUser(username: String, password: String, name: String, profilePhoto: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_NAME, name)
            put(COLUMN_PROFILE_PHOTO, profilePhoto)
        }
        val result = db.insert(TABLE_USERS, null, contentValues)
        db.close()
        return result != -1L
    }

    // Fetch User Profile
    fun getUserProfile(userID: Int): Map<String, String> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_ID = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(userID.toString()))
        val userProfile = if (cursor.moveToFirst()) {
            mapOf(
                "name" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                "username" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                "password" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                "profilePhoto" to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_PHOTO))
            )
        } else {
            emptyMap()
        }
        cursor.close()
        db.close()
        return userProfile
    }

    // Update User Profile
    fun updateUserProfile(username: String,userID: Int, name: String, password: String, profilePhoto: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_PROFILE_PHOTO, profilePhoto)
        }
        val result = db.update(TABLE_USERS, contentValues, "$COLUMN_USER_ID = ?", arrayOf(userID.toString()))
        db.close()
        return result > 0
    }

    // User Login
    fun loginUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password)
        )
        val isValid = cursor.count > 0
        cursor.close()
        db.close()
        return isValid
    }














}
