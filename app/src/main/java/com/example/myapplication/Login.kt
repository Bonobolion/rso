package com.example.myapplication


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.android.synthetic.main.login_page.*

class Login : AppCompatActivity() {

    //Firebase instance container
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        //initializes all UI elements and the Firebase instance
        auth = FirebaseAuth.getInstance()

        //once the log in button is pressed the login process begins
        logInButton.setOnClickListener {
            loginUser()
        }

        //dev login bypass button TODO: REMOVE THIS BUTTON
        bypass_button.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        //sets up the sign up text to send the user to the registration screen when tapped
        sign_up_prompt.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }
    }

    //this function handles the user attempting to login with an existing login
    private fun loginUser(){
        //checks if user has filled in the email and password field
        if(validate()){
            val loginEmail = login_email_textfield.text.toString()
            val loginPass = login_password_textfield.text.toString()

            //sign in through Firebase's authenticator
            auth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener { task ->
                //if the sign in is successful take the user to the home page
                if(task.isSuccessful){
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                }
                // If sign in fails, display a message to the user.
                else{
                    //grab the exception message from the task and display the error to the user
                    val e = task.exception as FirebaseAuthException
                    Toast.makeText(
                        this,
                        "Failed login: " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

    }

    //this function validates the login text fields
    //returns true if both email and password field have been filled in
    private fun validate() : Boolean {
        val email = login_email_textfield.text.toString()
        val password = login_password_textfield.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, "Please type in your email", Toast.LENGTH_SHORT).show()
            return false
        } else if(password.isEmpty()){
            Toast.makeText(this, "Please type in your password", Toast.LENGTH_SHORT).show()
            return false
        }
        else {
            return true
        }

    }

}
