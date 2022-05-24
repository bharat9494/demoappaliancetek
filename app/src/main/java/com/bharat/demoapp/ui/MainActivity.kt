package com.bharat.demoapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bharat.demoapp.databinding.ActivityMainBinding
import com.bharat.demoapp.misc.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        if(currentUser != null){
            val intent = Intent(this, ViewPhotosActivity::class.java)
            startActivity(intent)
        } else {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btnRegister.setOnClickListener(this)
            binding.textViewLogin.setOnClickListener(this)
        }
    }

    override fun onClick(view: View) {

        if(view == binding.textViewLogin) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        else if(view == binding.btnRegister) {

            val firstname = binding.etFirstname.text.toString().trim()
            val lastname = binding.etLastname.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if(firstname.isEmpty()) {
                Toast.makeText(this, firstnameValidation, Toast.LENGTH_SHORT).show()
                binding.etFirstname.requestFocus()
                return
            }

            if(lastname.isEmpty()) {
                Toast.makeText(this, lastnameValidation, Toast.LENGTH_SHORT).show()
                binding.etLastname.requestFocus()
                return
            }

            if(email.isEmpty()) {
                Toast.makeText(this, emailValidation, Toast.LENGTH_SHORT).show()
                binding.etEmail.requestFocus()
                return
            }

            if(password.isEmpty()) {
                Toast.makeText(this, passwordValidation, Toast.LENGTH_SHORT).show()
                binding.etPassword.requestFocus()
                return
            }

            if(password.length < 8) {
                Toast.makeText(this, passwordLengthValidation, Toast.LENGTH_SHORT).show()
                binding.etPassword.requestFocus()
                return
            }

            binding.progressLoader.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener { error ->
                    binding.progressLoader.visibility = View.GONE
                    Toast.makeText(baseContext, "$signupFailed ${error.message}", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener(this) { task ->
                    binding.progressLoader.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, signupSuccess, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ViewPhotosActivity::class.java)
                        startActivity(intent)
                    } else {
//                        Toast.makeText(baseContext, "Authentication failed. ${task.exception}", Toast.LENGTH_SHORT).show()
                    }
                }

            return
        }
    }
}