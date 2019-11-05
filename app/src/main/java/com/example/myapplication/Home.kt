package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    }




}