package com.example.footballquiz

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.footballquiz.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var editTextLoginPassword: EditText
    private lateinit var showHidePasswordButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        editTextLoginPassword = findViewById(R.id.editTextLoginPassword)
        showHidePasswordButton = findViewById(R.id.showHidePasswordButton)

        showHidePasswordButton.setOnClickListener {
            if (editTextLoginPassword.transformationMethod is PasswordTransformationMethod) {
                // Trenutno je prikazana skrivena lozinka, promijeni način prikaza u običan tekst
                editTextLoginPassword.transformationMethod = null
                showHidePasswordButton.setImageResource(R.drawable.ic_password_visibility_on)
            } else {
                // Trenutno je prikazan običan tekst, promijeni način prikaza u skrivenu lozinku
                editTextLoginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                showHidePasswordButton.setImageResource(R.drawable.ic_password_visibility_off)
            }

            // Osvježi tekst kako bi se prikazala nova transformacija, postavljanje kursora na kraj teksta unutar EditText polja,
            // editTextRegPassword.text?.length ?: 0 će vratiti duljinu teksta unutar EditText polja ako tekst nije null, inače će vratiti 0.
            editTextLoginPassword.setSelection( editTextLoginPassword.text?.length ?: 0)
        }

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextLoginEmail.text.toString()
            val password = binding.editTextLoginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Polja ne mogu biti prazna", Toast.LENGTH_SHORT).show()
            }
        }
        binding.textViewNewUser.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
    }
}