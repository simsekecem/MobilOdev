package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Veri Modelleri
data class Place(
    val id: Int,
    val name: String,
    val comment: String?,
    val photoPath: String,
    val userId: Int,
    val rating: Int
)

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val name: String?,
    val profilePhoto: String?
)

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
        const val COLUMN_PLACE_ID = "id"
        const val COLUMN_PLACE_NAME = "name"
        const val COLUMN_PLACE_COMMENT = "comment"
        const val COLUMN_PLACE_PHOTO_PATH = "photoPath"
        const val COLUMN_PLACE_USER_ID = "userID"
        const val COLUMN_PLACE_RATING = "rating"
        var currentUsername: String? = null
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Users Table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_USERS (" +
                    "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_USERNAME TEXT UNIQUE, " +
                    "$COLUMN_PASSWORD TEXT NOT NULL, " +
                    "$COLUMN_NAME TEXT, " +
                    "$COLUMN_PROFILE_PHOTO TEXT)"
        )

        // Places Table
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_PLACES (" +
                    "$COLUMN_PLACE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_PLACE_NAME TEXT NOT NULL, " +
                    "$COLUMN_PLACE_COMMENT TEXT, " +
                    "$COLUMN_PLACE_PHOTO_PATH TEXT NOT NULL, " +
                    "$COLUMN_PLACE_USER_ID INTEGER NOT NULL, " +
                    "$COLUMN_PLACE_RATING INTEGER DEFAULT 0, " +
                    "FOREIGN KEY ($COLUMN_PLACE_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLACES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun setCurrentUser(username: String) {
        currentUsername = username
    }

    // Kullanıcı Kaydı
    fun registerUser(username: String, password: String, name: String?, profilePhoto: String?): Boolean {
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

    // Kullanıcı Girişi
    fun loginUser(username: String, password: String): User? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(username, password))
        val user = if (cursor.moveToFirst()) {
            User(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_PHOTO))
            )
        } else null

        cursor.close()
        db.close()
        return user
    }

    // Kullanıcının Tüm Bilgilerini Getirme
    fun getUserDetails(userID: Int): User? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_ID = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(userID.toString()))
        val user = if (cursor.moveToFirst()) {
            User(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE_PHOTO))
            )
        } else null

        cursor.close()
        db.close()
        return user
    }

    // Kullanıcı Bilgilerini Güncelleme
    fun updateUserDetails(userID: Int, username: String?, password: String?, name: String?, profilePhoto: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            username?.let { put(COLUMN_USERNAME, it) }
            password?.let { put(COLUMN_PASSWORD, it) }
            name?.let { put(COLUMN_NAME, it) }
            profilePhoto?.let { put(COLUMN_PROFILE_PHOTO, it) }
        }
        val result = db.update(TABLE_USERS, contentValues, "$COLUMN_USER_ID = ?", arrayOf(userID.toString()))
        db.close()
        return result > 0
    }

    // Yeni Yer Ekleme
    fun insertPlace(name: String, comment: String?, photoPath: String, userID: Int, rating: Int = 0): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_PLACE_NAME, name)
            put(COLUMN_PLACE_COMMENT, comment)
            put(COLUMN_PLACE_PHOTO_PATH, photoPath)
            put(COLUMN_PLACE_USER_ID, userID)
            put(COLUMN_PLACE_RATING, rating)
        }
        val result = db.insert(TABLE_PLACES, null, contentValues)
        db.close()
        return result != -1L
    }

    // Tüm Yerleri Getirme
    fun getAllTrips(): List<Place> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_PLACES"
        val cursor: Cursor = db.rawQuery(query, null)
        val places = mutableListOf<Place>()

        if (cursor.moveToFirst()) {
            do {
                val place = Place(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLACE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_COMMENT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_PHOTO_PATH)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLACE_USER_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLACE_RATING))
                )
                places.add(place)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return places
    }

    // Yer Arama (Place Name'e Göre)
    fun searchTrips(queryText: String): List<Place> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_PLACES WHERE $COLUMN_PLACE_NAME LIKE ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf("%$queryText%"))
        val places = mutableListOf<Place>()

        if (cursor.moveToFirst()) {
            do {
                val place = Place(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLACE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_COMMENT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_PHOTO_PATH)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLACE_USER_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLACE_RATING))
                )
                places.add(place)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return places
    }

    // Kullanıcıya Ait Yerleri Getirme
    fun getUserTrips(userID: Int): List<Place> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_PLACES WHERE $COLUMN_PLACE_USER_ID = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(userID.toString()))
        val places = mutableListOf<Place>()

        if (cursor.moveToFirst()) {
            do {
                val place = Place(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLACE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_COMMENT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_PHOTO_PATH)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLACE_USER_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PLACE_RATING))
                )
                places.add(place)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return places
    }

    // Place Name'e Göre Ortalama Rating Puanlarını Hesaplayıp Sıralama
    fun getTopRatedTrips(): List<Place> {
        val db = this.readableDatabase
        val query = """
        SELECT $COLUMN_PLACE_NAME, AVG($COLUMN_PLACE_RATING) AS avg_rating
        FROM $TABLE_PLACES
        GROUP BY $COLUMN_PLACE_NAME
        ORDER BY avg_rating DESC
    """
        val cursor: Cursor = db.rawQuery(query, null)
        val places = mutableListOf<Place>()

        if (cursor.moveToFirst()) {
            do {
                // Her bir yerin ortalama rating'i ile birlikte yer bilgilerini almak için
                val placeName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_NAME))
                val avgRating = cursor.getDouble(cursor.getColumnIndexOrThrow("avg_rating"))

                // Bu isme sahip olan yerin tüm verilerini almak
                val placeQuery = "SELECT * FROM $TABLE_PLACES WHERE $COLUMN_PLACE_NAME = ?"
                val placeCursor: Cursor = db.rawQuery(placeQuery, arrayOf(placeName))

                if (placeCursor.moveToFirst()) {
                    do {
                        val place = Place(
                            placeCursor.getInt(placeCursor.getColumnIndexOrThrow(COLUMN_PLACE_ID)),
                            placeCursor.getString(placeCursor.getColumnIndexOrThrow(COLUMN_PLACE_NAME)),
                            placeCursor.getString(placeCursor.getColumnIndexOrThrow(COLUMN_PLACE_COMMENT)),
                            placeCursor.getString(placeCursor.getColumnIndexOrThrow(COLUMN_PLACE_PHOTO_PATH)),
                            placeCursor.getInt(placeCursor.getColumnIndexOrThrow(COLUMN_PLACE_USER_ID)),
                            avgRating.toInt() // Ortalama rating'i buraya ekliyoruz
                        )
                        places.add(place)
                    } while (placeCursor.moveToNext())
                }
                placeCursor.close()

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return places
    }

}
