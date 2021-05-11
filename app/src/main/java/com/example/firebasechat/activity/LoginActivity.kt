package com.example.firebasechat.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.firebasechat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private  var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var btnLogin = findViewById<Button>(R.id.btnLogin)
        var etEmail = findViewById<TextView>(R.id.etEmail)
        var etPassword = findViewById<TextView>(R.id.etPassword)
        var btnSignUp = findViewById<Button>(R.id.btnSignUp)

        auth = FirebaseAuth.getInstance()
        //firebaseUser = auth!!.currentUser!!

        //check if user login then navigate to user screen
        if (firebaseUser != null) {
            val intent = Intent(
                this@LoginActivity,
                UsersActivity::class.java
            )
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(
                    applicationContext,
                    "email and password are required",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                auth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            etEmail.setText("")
                            etPassword.setText("")
                            val intent = Intent(
                                this@LoginActivity,
                                UsersActivity::class.java
                            )
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "email or password invalid",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        btnSignUp.setOnClickListener {
            val intent = Intent(
                this@LoginActivity,
                SignUpActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }
}