package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_join_den.*

class JoinDen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_den)

        join_den_button.setOnClickListener {
            //TODO:ADD JOIN AN EXISTING DEN FUNCTIONALITY
        }

        //send user to den creation screen when create den text is tapped
        create_den_prompt.setOnClickListener {
            val intent = Intent(this, CreateDen::class.java)
            startActivity(intent)
        }
    }
}
