package com.bharat.demoapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bharat.demoapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if(view == binding.btnLogin) {

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if(email.isEmpty()) {
                Toast.makeText(this, "email can not be empty", Toast.LENGTH_SHORT).show()
                binding.etEmail.requestFocus()
                return
            }

            if(password.isEmpty()) {
                Toast.makeText(this, "password can not be empty", Toast.LENGTH_SHORT).show()
                binding.etPassword.requestFocus()
                return
            }

            if(password.length < 8) {
                Toast.makeText(this, "password should be minimum 8 character long!!", Toast.LENGTH_SHORT).show()
                binding.etPassword.requestFocus()
                return
            }

            binding.progressLoader.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener { error ->
                    binding.progressLoader.visibility = View.GONE
                    Toast.makeText(baseContext, "Authentication failed. ${error.message}", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener(this) { task ->
                    binding.progressLoader.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Logged in.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ViewPhotosActivity::class.java)
                        startActivity(intent)
                    } else {
//                        Toast.makeText(baseContext, "Authentication failed. ${task.exception}", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
}