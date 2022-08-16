package com.example.firestore

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.firestore.databinding.ActivityUserDetailBinding
import com.google.firebase.auth.FirebaseAuth

class UserDetail : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val email = intent.getStringExtra("email")
        val name = intent.getStringExtra("name")
        val givenName = intent.getStringExtra("givenName")

        binding.tvUserEmail.text = email
        binding.tvUserName.text = name
        binding.tvGivenName.text = givenName
        binding.ivProfile.setImageURI((intent.getStringExtra("profile"))?.toUri())
        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}