package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.registration_page.*
import com.google.firebase.auth.FirebaseAuthException


//This activity handles a first time user registering with the system and creating an account
class Registration : AppCompatActivity() {

    //creating variable to hold the Firebase authentication object
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_page)

        //initializing the Firebase instance
        auth = FirebaseAuth.getInstance()

        //on registration button tap start registration process via createUser function call
        registration_accept_reg_button.setOnClickListener {
            createUser()
        }
    }

    private fun createUser(){
        //check to see if the fields are full, if they are not display and error message,
        //and prompt the user to fill in the fields
        if(validate()) {
            //if the text fields have been filled, save the email and password to be sent from
            //the view components
            val sentEmail = registration_email.text.toString()
            val sentPassword = registration_password1.text.toString()

            //create user with saved email and password strings
            auth.createUserWithEmailAndPassword(sentEmail, sentPassword)
                .addOnCompleteListener(this) { task ->
                    //if the registration is successful display a success message and the user is sent
                    //to the join a den activity
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT)
                            .show()
                        val user = auth.currentUser
                        val intent = Intent(this, JoinDen::class.java)
                        startActivity(intent)
                    }
                    //if the registration fails display the reason why and remain on the
                    //registration page
                    else {
                        //grab the exception message from the task and display the error to the user
                        val e = task.exception as FirebaseAuthException
                        Toast.makeText(
                            this,
                            "Failed Registration: " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }



    //this function checks to see if all registration fields have been filled
    private fun validate(): Boolean {
        //val name = registration_name.text.toString()
        val email = registration_email.text.toString()
        val password = registration_password1.text.toString()

        //if the email field is empty, prompt user to fill in and return false
        if (email.isEmpty()) {
            Toast.makeText(this, "Please type in a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }
        //else if the password field is empty, prompt user to fill in and return false
        else if (password.isEmpty()) {
            Toast.makeText(this, "Please fill in your password", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}

