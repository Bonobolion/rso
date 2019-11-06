package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.home_page.*

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        //starts calendar activity on calendar button tap
        calendar_button.setOnClickListener {
            val intent = Intent(this, Calendar::class.java)
            startActivity(intent)
        }

        //TODO:REMOVE DATABASE TESTING ACTIVITY, this is meant to build the database
        todo_list_button.setOnClickListener {
            val intent = Intent(this, DatabaseTesting::class.java)
            startActivity(intent)
        }

        //signs out user when sign_out button is pressed
        sign_out_button.setOnClickListener {
           signOutUser()
        }
    }

    //this function signs the current user out
    private fun signOutUser(){
        //gets instance of the Firestore database
        val db = FirebaseFirestore.getInstance()
        //save the current user's uid, which will be used to grab the user's document from Firestore database
        val uid = FirebaseAuth.getInstance().uid.toString()
        //grab reference to the user's document from Firestore database
        val docRef = db.collection("users").document(uid)

        //if the reference is successfully grabbed then sign the user out and display their name
        docRef.get().addOnSuccessListener {
            //save the user's name from the document snapshot "it"
            val name = it.getString("firstName")
            //display a sign out message showing the user's name and then sign the user out
            //through the firebase authenticator
            Toast.makeText(this, "Signing out $name", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        //if the user fails to sign out, display error message to the user
        }.addOnFailureListener {
            Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }


}