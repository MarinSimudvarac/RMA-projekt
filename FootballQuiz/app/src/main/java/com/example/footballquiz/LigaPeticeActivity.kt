package com.example.footballquiz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class LigaPeticeActivity : AppCompatActivity() {

    private lateinit var pravilaIgreTextView: TextView
    private lateinit var pravilo1TextView: TextView
    private lateinit var pravilo5TextView: TextView
    private lateinit var uspjehTextView: TextView
    private lateinit var buttonZapocni: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ligapetice)

        pravilaIgreTextView = findViewById(R.id.pravilaIgreTextView)
        pravilo1TextView = findViewById(R.id.pravilo1TextView)
        pravilo5TextView = findViewById(R.id.pravilo5TextView)
        uspjehTextView = findViewById(R.id.uspjehTextView)
        buttonZapocni = findViewById(R.id.buttonZapocni)

        buttonZapocni.setOnClickListener {

            val fragmentLigaPeticeEasy = LigaPeticeEasyFragment()

            pravilaIgreTextView.visibility = View.GONE
            pravilo1TextView.visibility = View.GONE
            pravilo5TextView.visibility = View.GONE
            uspjehTextView.visibility = View.GONE
            buttonZapocni.visibility = View.GONE

            switchFragment(fragmentLigaPeticeEasy)
        }
    }

    private fun switchFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }
}