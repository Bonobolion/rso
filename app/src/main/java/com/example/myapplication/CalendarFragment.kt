package com.example.myapplication


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_create_den.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.w3c.dom.Text

/**
 * A simple [Fragment] subclass.
 */
class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setText()
    }

    private fun setText(){
        //fetch events list from database
        //if (!events)
        val text = "You're all set. There's nothing to do."
        //else
        //calendar_msg.text="Your upcoming events"
        //make recycler view displayable.

        //Change text
        view?.findViewById<TextView>(R.id.calendar_msg)?.text = text
    }



}
