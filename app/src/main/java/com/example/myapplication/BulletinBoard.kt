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
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_bulletin_board.*
import kotlinx.android.synthetic.main.activity_create_den.*
import kotlinx.android.synthetic.main.bulletinrow.view.*
import java.util.*


class BulletinBoard : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bulletin_board)
        recyclerview_bulletinboard.layoutManager = LinearLayoutManager(this)
        recyclerview_bulletinboard.adapter = adapter

        bulletin_postButton.setOnClickListener {
            if(validate()){
                createPost()
                bulletin_postTextfield.text = null
            }
        }
        listenForUpdates()
    }
        private fun listenForUpdates() {
            //firestore instance
            val db = FirebaseFirestore.getInstance()
            //grab denID
            //den id is null
            val uid = FirebaseAuth.getInstance().uid.toString()
            val userRef = db.collection("users").document(uid).get()
                .addOnSuccessListener {
                    //grab the denID from the query
                    val buffer = it.data
                    val denID = buffer?.getValue("denID").toString()

                    //listen to changes in documents with the matching denID
                    val docRef = db.collection("bulletin_posts")
                        .whereEqualTo("denID",denID)
                        .addSnapshotListener{ value, e ->
                            if (e != null) {
                                Toast.makeText(this, "Failed to retrieve bulletin board",Toast.LENGTH_SHORT).show()
                                return@addSnapshotListener
                            }

                            val posts = ArrayList<Post>()

                            for (doc in value!!) {
                                val content = doc.getString("post_content").toString()
                                val author = doc.getString("author").toString()
                                //val timestamp = doc.getLong("timestamp")!!.toLong()
                                val post = Post(content,author)
                                posts.add(post)
                            }
                            if(posts.size == 0) {
                                return@addSnapshotListener
                            }
                            else {
                                adapter.add(BulletinPost(posts[posts.size-1]))
                            }
                        }
                }
        }

    //takes input in the text field and creates a post entry in the database to be displayed
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
        //grab the den ID from the user's record
        //grab content from text field
        val postContent = bulletin_postTextfield.text.toString()

        val query = db.collection("users").document(creatorUID).get().addOnSuccessListener {
            val buffer = it.data
            val denID = buffer?.getValue("denID").toString()

            val post = hashMapOf(
                "postID" to postID,
                "creatorID" to creatorUID,
                "timestamp" to timeStamp,
                "denID" to denID,
                "post_content" to postContent,
                "author" to buffer?.getValue("firstName").toString()
                )

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

    //listen for create post button press on top menu bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            //TODO: bring up delete post activity where all of the user's posts are displayed
            //and they can select which one to delete
            R.id.deletePost -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bulletinmenu, menu)
        return super.onCreateOptionsMenu(menu)
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
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        //will be called in list
        viewHolder.itemView.post_content.text = post.content
        viewHolder.itemView.post_author.text = post.author
    }
    override fun getLayout(): Int {
        return R.layout.bulletinrow
    }
}

class Post(val content : String, val author : String)