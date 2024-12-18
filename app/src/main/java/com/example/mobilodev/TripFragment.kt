package com.example.mobilproje

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class TripFragment : Fragment() {

    private lateinit var databaseHelper: DatabaseHelper
    private val tripList = mutableListOf<Trip>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        databaseHelper = DatabaseHelper(requireContext())
        loadTripsFromDatabase()


        val tripListLayout: LinearLayout = view.findViewById(R.id.tripListLayout)


        for (trip in tripList) {
            val tripView = LayoutInflater.from(requireContext()).inflate(R.layout.item_trip, tripListLayout, false)


            val imageView: ImageView = tripView.findViewById(R.id.tripImage)
            val descriptionTextView: TextView = tripView.findViewById(R.id.tripDescription)
            val commentTextView: TextView = tripView.findViewById(R.id.tripComment)

            imageView.setImageBitmap(trip.image) // Set the trip image
            descriptionTextView.text = trip.description // Set the description
            commentTextView.text = trip.comment // Set the comment

            // Add the trip view to the LinearLayout
            tripListLayout.addView(tripView)
        }
    }

    private fun loadTripsFromDatabase() {
        val db = databaseHelper.readableDatabase
        val cursor = db.query("trips", null, null, null, null, null, "id DESC")

        while (cursor.moveToNext()) {
            val imageBase64 = cursor.getString(cursor.getColumnIndexOrThrow("image"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val comment = cursor.getString(cursor.getColumnIndexOrThrow("comment"))


            val imageBitmap = base64ToBitmap(imageBase64)


            tripList.add(Trip(imageBitmap, description, comment))
        }

        cursor.close()
    }

    private fun base64ToBitmap(base64String: String): Bitmap {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}
