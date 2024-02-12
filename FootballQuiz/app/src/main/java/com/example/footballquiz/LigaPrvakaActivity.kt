package com.example.footballquiz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class LigaPrvakaActivity : AppCompatActivity() {

    private lateinit var pravilaIgreTextView: TextView
    private lateinit var pravilo1TextView: TextView
    private lateinit var pravilo2TextView: TextView
    private lateinit var pravilo3TextView: TextView
    private lateinit var pravilo4TextView: TextView
    private lateinit var pravilo5TextView: TextView
    private lateinit var uspjehTextView: TextView
    private lateinit var buttonZapocni: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ligaprvaka)

        pravilaIgreTextView = findViewById(R.id.pravilaIgreTextView)
        pravilo1TextView = findViewById(R.id.pravilo1TextView)
        pravilo2TextView = findViewById(R.id.pravilo2TextView)
        pravilo3TextView = findViewById(R.id.pravilo3TextView)
        pravilo4TextView = findViewById(R.id.pravilo4TextView)
        pravilo5TextView = findViewById(R.id.pravilo5TextView)
        uspjehTextView = findViewById(R.id.uspjehTextView)
        buttonZapocni = findViewById(R.id.buttonZapocni)

        buttonZapocni.setOnClickListener {

            val fragmentLigaPrvakaEasy = LigaPrvakaEasyFragment()

            pravilaIgreTextView.visibility = View.GONE
            pravilo1TextView.visibility = View.GONE
            pravilo2TextView.visibility = View.GONE
            pravilo3TextView.visibility = View.GONE
            pravilo4TextView.visibility = View.GONE
            pravilo5TextView.visibility = View.GONE
            uspjehTextView.visibility = View.GONE
            buttonZapocni.visibility = View.GONE

            switchFragment(fragmentLigaPrvakaEasy)
        }
    }

    private fun switchFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }
}