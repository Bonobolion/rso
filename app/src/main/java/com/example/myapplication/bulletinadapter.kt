package com.example.myapplication

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bulletinrow.view.*


class BulletinAdapter: RecyclerView.Adapter<BulletinViewHolder>(){

    //returns number of items
    override fun getItemCount(): Int {
        return 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulletinViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.bulletinrow, parent,false)
        return BulletinViewHolder(cellForRow)
    }

    override fun onBindViewHolder(holder: BulletinViewHolder, position: Int) {

    }

}

class BulletinViewHolder(val view: View): RecyclerView.ViewHolder(view){

}