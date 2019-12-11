package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_bulletin_board.*
import kotlinx.android.synthetic.main.activity_create_den.*
import kotlinx.android.synthetic.main.bulletinrow.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class BulletinBoard : AppCompatActivity() {

    companion object{
        val TAG = "bulletin"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bulletin_board)
        recyclerview_bulletinboard.layoutManager = LinearLayoutManager(this)
        //establish the adapter for the bulletin board
        recyclerview_bulletinboard.adapter = adapter

        bulletin_postButton.setOnClickListener {
            if(validate()){
                createPost()
                bulletin_postTextfield.text = null
            }
        }
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
                    .whereEqualTo("denID", denID).orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshots, e ->
                        if (e != null) {
                            Log.w(TAG, "listen:error", e)
                            return@addSnapshotListener
                        }
                        for (dc in snapshots!!.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> Log.d(TAG, "New post: ${dc.document.data}")
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

    //takes input in the text field and saves a post entry in the database to be displayed
    //in the recycler view
    private fun createPost() {
        //get firestore reference
        val db = FirebaseFirestore.getInstance()
        //create timestamp for post
        val tsLong = System.currentTimeMillis() / 1000
        val timeStamp = tsLong.toString()
        //grab the user's id for the post
        val creatorUID = FirebaseAuth.getInstance().uid.toString()
        //generate a random ID for the post
        val postID = UUID.randomUUID().toString()
        //grab content from input text field
        val postContent = bulletin_postTextfield.text.toString()

        //query to get the current user's denID
        val query = db.collection("users").document(creatorUID)
            .get().addOnSuccessListener {
            //save the retrieved data in a map object
            val buffer = it.data
            //grab the denID from the buffer map object and save it to denID
            val denID = buffer?.getValue("denID").toString()

            //create the post map that will be saved in firestore
            val post = hashMapOf(
                "postID" to postID,
                "creatorID" to creatorUID,
                "timestamp" to timeStamp,
                "denID" to denID,
                "post_content" to postContent,
                "author" to buffer?.getValue("firstName").toString()
            )

                val newPost = Post(postContent, buffer?.getValue("firstName").toString(), timeStamp)
                adapter.add(BulletinPost(newPost))
            //save the newly created post mapping to the firestore database
            db.collection("bulletin_posts").document(postID).set(post)
                //if successful notify user with a toast and enter the home activity
                .addOnSuccessListener {
                    Toast.makeText(this, "post successfully created", Toast.LENGTH_SHORT).show()
                }
                //if data creation failed, output error message
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to create den: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }



    //validates that the text fields have been filled
    private fun validate() : Boolean {
        val postContent = bulletin_postTextfield.text.toString()

        //if a text field is empty notify the user with a toast and return false
        //return true if all fields have been filled
        if(postContent.isEmpty()){
            Toast.makeText(this, "Please fill in your post", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}



class BulletinPost(val post : Post): Item<GroupieViewHolder>() {

    private fun convertLongToTime (time : Long): String {
        val date = Date(time * 1000)
        val format = SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.US)
         return format.format(date)
    }



    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        //will be called in list
        viewHolder.itemView.post_content.text = post.content

        val time = post.ts.toLong()
        val date = convertLongToTime(time)

        viewHolder.itemView.post_author.text = post.author + ", " + date

    }
    override fun getLayout(): Int {
        return R.layout.bulletinrow
    }
}

class Post(val content : String, val author : String, val ts : String)