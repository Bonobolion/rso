package com.example.myapplication


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.bulletinrow.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*

/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview_recents.layoutManager = LinearLayoutManager(context)
        recyclerview_recents.adapter = adapter

        //val recyclerView = R.id.recyclerview_recents



        listenForUpdates()
    }



    //this function updates the bulletin board once a post is made
    private fun listenForUpdates() {
        //firestore instance
        val db = FirebaseFirestore.getInstance()
        //grab the current user's uid
        val uid = FirebaseAuth.getInstance().uid.toString()

        //grab the current user's denID and on a success begin listening for posts
        val userRef = db.collection("users").document(uid).get()
            .addOnSuccessListener {
                //save the received data into a buffer map
                val buffer = it.data
                //save the denID from the buffer map
                val denID = buffer?.getValue("denID").toString()

                db.collection("bulletin_posts")
                    .whereEqualTo("denID", denID)
                    .addSnapshotListener { snapshots, e ->
                        if (e != null) {
                            Log.w(BulletinBoard.TAG, "listen:error", e)
                            return@addSnapshotListener
                        }
                        for (dc in snapshots!!.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> Log.d(BulletinBoard.TAG, "New post: ${dc.document.data}")
                            }
                            //grab content, author, and timestamp from the received data
                            val content = dc.document.getString("post_content").toString()
                            val author = dc.document.getString("author").toString()
                            val timestamp = dc.document.getString("timestamp").toString()
                            //create post object that will be placed on the recycler
                            val newPost = Post(content,author,timestamp)
                            //add post object to the recycler
                            adapter.add(BulletinPost(newPost))
                        }
                    }
            }
    }

}
