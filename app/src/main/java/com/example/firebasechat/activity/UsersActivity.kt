package com.example.firebasechat.activity


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasechat.firebase.FirebaseService
import com.example.firebasechat.R
import com.example.firebasechat.adapter.UserAdapter
import com.example.firebasechat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging

class UsersActivity : AppCompatActivity() {
    var userList = ArrayList<User>()
    lateinit var userRecyclerView: RecyclerView
    lateinit var imgProfile: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
        }
        userRecyclerView = findViewById<RecyclerView>(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        var imgBack = findViewById<ImageView>(R.id.imgBack)
        imgBack.setOnClickListener {
            onBackPressed()
        }
        imgProfile = findViewById<ImageView>(R.id.imgProfile)
        imgProfile.setOnClickListener {
            val intent = Intent(
                this@UsersActivity,
                ProfileActivity::class.java
            )
            startActivity(intent)
        }
        getUsersList()
    }

    fun getUsersList() {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        var userid = firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")


        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")

        imgProfile = findViewById<ImageView>(R.id.imgProfile)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val currentUser = snapshot.getValue(User::class.java)
                if (currentUser!!.profileImage == ""){
                    imgProfile.setImageResource(R.drawable.profile_image)
                }else{
                    Glide.with(this@UsersActivity).load(currentUser.profileImage).into(imgProfile)
                }

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)

                    if (!user!!.userId.equals(firebase.uid)) {

                        userList.add(user)
                    }
                }

                val userAdapter = UserAdapter(this@UsersActivity, userList)

                userRecyclerView.adapter = userAdapter
            }

        })
    }
}