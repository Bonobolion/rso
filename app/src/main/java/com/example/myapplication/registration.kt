package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.registration_page.*
import com.google.firebase.auth.FirebaseAuthException



//This activity handles a first time user registering with the system and creating an account
class Registration : AppCompatActivity() {

    //creating the Firebase authentication object
    private lateinit var auth: FirebaseAuth

    //creating the activity view objects
    private lateinit var regName : TextView
    private lateinit var regEmail : TextView
    private lateinit var regPassword : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_page)

        //initializing the Firebase instance
        auth = FirebaseAuth.getInstance()
        //initialize all ui elements
        setUI()

        //on registration button tap
        registration_accept_reg_button.setOnClickListener {
            //check to see if the fields are full, if they are not display and error message,
            //and prompt the user to fill in the fields
            if(validate()) {
                //if the text fields have been filled, save the email and password to be sent from
                //the view components
                val sentEmail = regEmail.text.toString()
                val sentPassword = regPassword.text.toString()

                //create user with saved email and password strings
                auth.createUserWithEmailAndPassword(sentEmail, sentPassword)
                    .addOnCompleteListener(this) { task ->
                        //if the registration is successful display a success message and sign the
                        //user into the main activity
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT)
                                .show()
                            val user = auth.currentUser
                            val intent = Intent(this, Home::class.java)
                            startActivity(intent)
                        }
                        //if the registration fails display the reason why and remain on the
                        //registration page
                        else {
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
    }

    //this function initializes all the user
    private fun setUI(){
        regName = findViewById(R.id.registration_name)
        regEmail = findViewById(R.id.registration_email)
        regPassword = findViewById(R.id.registration_password1)
    }

    //this function checks to see if all registration fields have been filled
    private fun validate(): Boolean {
        val name = regName.text.toString()
        val email = regEmail.text.toString()
        val password = regPassword.text.toString()

        if (name.isEmpty() && email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Please enter information", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }
}

