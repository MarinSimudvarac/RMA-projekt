package com.example.footballquiz

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment: Fragment() {

    private lateinit var ligaPrvakaButton: Button
    private lateinit var spiEpButton: Button
    private lateinit var ligaPeticeButton: Button
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflater koristi XML layout za prikazivanje fragmenta
        return inflater.inflate(R.layout.fragment_home, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //pokretanje glazbe
        mediaPlayer = MediaPlayer.create(context, R.raw.uefa_champions_league_anthem);
        mediaPlayer.start();


        ligaPrvakaButton = view.findViewById(R.id.ligaprvakabutton)
        spiEpButton = view.findViewById(R.id.spiepbutton)
        ligaPeticeButton = view.findViewById(R.id.ligapeticebutton)

        ligaPrvakaButton.setOnClickListener {
            val intent = Intent(activity, LigaPrvakaActivity::class.java)
            startActivity(intent)
        }

        spiEpButton.setOnClickListener {
            val intent = Intent(activity, SpiEpActivity::class.java)
            startActivity(intent)
        }

        ligaPeticeButton.setOnClickListener {
            val intent = Intent(activity, LigaPeticeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Zaustavljanje reprodukcije kada se fragment pauzira
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        // Zaustavljanje reprodukcije kada se aktivnost pauzira
        mediaPlayer.stop()
    }
}