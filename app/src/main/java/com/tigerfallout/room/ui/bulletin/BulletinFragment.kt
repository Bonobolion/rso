package com.tigerfallout.room.ui.bulletin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tigerfallout.room.R

class BulletinFragment : Fragment() {

    private lateinit var bulletinViewModel: BulletinViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bulletinViewModel =
            ViewModelProviders.of(this).get(BulletinViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_bulletin, container, false)
        val textView: TextView = root.findViewById(R.id.text_bulletin)
        bulletinViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}