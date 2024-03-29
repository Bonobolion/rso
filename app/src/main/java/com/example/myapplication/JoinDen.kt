package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_join_den.*
import kotlinx.android.synthetic.main.registration_page.*

class JoinDen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_den)

        join_den_button.setOnClickListener {
            if(validate()) {
                joinDen()
            }
        }
        //send user to den creation screen when create den text is tapped
        create_den_prompt.setOnClickListener {
            val intent = Intent(this, CreateDen::class.java)
            startActivity(intent)
        }
    }

    //take string and makes it lowercase
    private fun String.makeLowercase(): String {
        val charIterator = iterator() // CharIterator of string
        var output = ""
        for (char in charIterator) {
            output += char.toLowerCase()
        }
        return output
    }

    private fun joinDen(){
        val db = FirebaseFirestore.getInstance()
        //den addresses are stored in all lowercase strings in the database
        val denAddress = den_name_textView.text.toString().makeLowercase()

        //create a reference to the dens collection in firestore database
        val denRef = db.collection("dens")
        //query the database for the given den address
        val query = denRef.whereEqualTo("den_address",denAddress).get()
                        .addOnSuccessListener {
                                documents ->
                            //if the query brings no results then give user error message
                            if(documents.isEmpty()) {
                                Toast.makeText(this, "Failed to find den with this address, please make sure it's the correct address", Toast.LENGTH_SHORT).show()
                            }
                            //else if the den is found
                            else{
                                //save the document data as a map to access values later
                                for (document in documents){
                                    val den = document.data
                                    //add user to the den by assigning their den ID to the join den den ID
                                    val uid = FirebaseAuth.getInstance().uid.toString()
                                    //create a field for the user called denID and save the den's ID to it
                                    val denAssignment = hashMapOf("denID" to den.getValue("den_ID"))
                                    //merge the new data into the existing user on the database
                                    db.collection("users").document(uid)
                                        .set(denAssignment, SetOptions.merge())
                                    Toast.makeText(this, "Joining den: " + den.getValue("den_name"), Toast.LENGTH_SHORT).show()
                                }
                                //send the user to the home page
                                val intent = Intent(this, Home::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                }
        }.addOnFailureListener {
            //if search for den's address failed give the user an error message
            Toast.makeText(this, "Failed to find den: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    //this function checks to see if all registration fields have been filled
    private fun validate(): Boolean {
        val denName = den_name_textView.text.toString()

        if (denName.isEmpty()) {
            Toast.makeText(this, "Please enter the den's address", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

}
