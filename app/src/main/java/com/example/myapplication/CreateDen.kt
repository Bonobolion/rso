package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_create_den.*
import java.util.*

class CreateDen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_den)

        start_den_button.setOnClickListener {
            if(validate())
            {
                createDen()
            }
        }
    }

    //validates that the text fields have been filled
    private fun validate() : Boolean {
        val denName = den_name_textField.text.toString()
        val denAddress = den_address_textField.text.toString()

        //if a text field is empty notify the user with a toast and return false
        //return true if all fields have been filled
        if(denName.isEmpty()){
            Toast.makeText(this, "Please enter a den name", Toast.LENGTH_SHORT).show()
            return false
        }
        if(denAddress.isEmpty()){
            Toast.makeText(this, "Please enter the den's address", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    //takes in a string and makes it lowercase
    private fun String.makeLowercase(): String {
        val charIterator = iterator() // CharIterator of string
        var output = ""
        for (char in charIterator) {
            output += char.toLowerCase()
        }
        return output
    }

    //this function takes in input from create den field and creates an entry in the database
    private fun createDen(){
        val database = FirebaseFirestore.getInstance()
        //generate random string for the den's unique identifier
        val denID = UUID.randomUUID().toString()
        //save values from text field
        val denName = den_name_textField.text.toString()
        //make den address lowercase to facilitate future querying
        val denAddress = den_address_textField.text.toString().makeLowercase()

        //Generate the den object to be created within the database
        val den = hashMapOf(
            "den_ID" to denID,
            "den_name" to denName,
            "den_address" to denAddress
        )

        //save created den to database
        database.collection("dens").document(denID).set(den)
            //if successful notify user with a toast and enter the home activity
            .addOnSuccessListener {
                Toast.makeText(this, "Den successfully created", Toast.LENGTH_SHORT).show()

                //This will add the newly created den's ID to the current user
                //grab the current user's uid with firebaseauth's get instance()
                val uid = FirebaseAuth.getInstance().uid.toString()
                //create a field for the user called denID and save the den's ID to it
                val denAssignment = hashMapOf("denID" to denID)
                //merge the new data into the existing user on the database
                database.collection("users").document(uid)
                    .set(denAssignment, SetOptions.merge())

                //after the den has been created send the user to the home page
                val intent = Intent(this, Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            //if data creation failed, output error message
            .addOnFailureListener {
            Toast.makeText(this, "Failed to create den: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
