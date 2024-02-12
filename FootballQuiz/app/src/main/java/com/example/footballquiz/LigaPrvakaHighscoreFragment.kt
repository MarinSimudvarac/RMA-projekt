package com.example.footballquiz

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit


class LigaPrvakaHighscoreFragment: Fragment() {

    private lateinit var buttonPokreni: Button
    private lateinit var buttonIzlaz: Button
    private lateinit var vasTrenutniRezultatTextView: TextView
    private lateinit var vasHighScoreTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var highScoreTextView: TextView
    private var score: Int = 0
    private var highScore: Int = 0
    private lateinit var konfettiView: KonfettiView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflater koristi XML layout za prikazivanje fragmenta
        return inflater.inflate(R.layout.fragment_ligaprvaka_highscore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        vasTrenutniRezultatTextView = view.findViewById(R.id.vasTrenutniRezultatTextView)
        vasHighScoreTextView = view.findViewById(R.id.vasHighScoreTextView)
        scoreTextView = view.findViewById(R.id.scoreTextView)
        highScoreTextView = view.findViewById(R.id.highScoreTextView)
        buttonPokreni = view.findViewById(R.id.buttonPokreni)
        buttonIzlaz = view.findViewById(R.id.buttonIzlaz)
        konfettiView = view.findViewById(R.id.konfettiView)

        fadeIn()
        konfetiAnimacija()

        // Poziv funkcije s odgodom od 2000 milisekundi (2 sekunde)
        Handler(Looper.getMainLooper()).postDelayed({
            // Ovdje pozovite željenu funkciju
            iscitajBodoveIzBaze()
        }, 2000) // Ovdje postavite željeni vremenski interval u milisekundama


        buttonPokreni.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainer, LigaPrvakaEasyFragment())?.commit()
        }

        buttonIzlaz.setOnClickListener {
            requireActivity().finish()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startScoreCounterAnimation(0, score)
            startHighScoreCounterAnimation(0, highScore)
        }, 3000)

    }

    private fun fadeIn() {
        // Postavljanje prozirnosti na 0 prije animacije
        vasTrenutniRezultatTextView.alpha = 0f
        vasHighScoreTextView.alpha = 0f
        scoreTextView.alpha = 0f
        highScoreTextView.alpha = 0f
        buttonPokreni.alpha = 0f
        buttonIzlaz.alpha = 0f

        // Prva animacija
        vasTrenutniRezultatTextView.animate()
            .alpha(1f)
            .setDuration(2000)
            .start()

        // Kašnjenje druge animacije
        Handler(Looper.getMainLooper()).postDelayed({
            scoreTextView.animate()
                .alpha(1f)
                .setDuration(2000)
                .start()
        }, 1000)

        // Kašnjenje treće animacije
        Handler(Looper.getMainLooper()).postDelayed({
            vasHighScoreTextView.animate()
                .alpha(1f)
                .setDuration(2000)
                .start()
        }, 2000)

        // Kašnjenje četvrte animacije
        Handler(Looper.getMainLooper()).postDelayed({
            highScoreTextView.animate()
                .alpha(1f)
                .setDuration(2000)
                .start()
        }, 3000)

        // Kašnjenje buttona Pokreni ponovno
        Handler(Looper.getMainLooper()).postDelayed({
            buttonPokreni.animate()
                .alpha(1f)
                .setDuration(2000)
                .start()
        }, 4000)

        // Kašnjenje buttona Izlaz
        Handler(Looper.getMainLooper()).postDelayed({
            buttonIzlaz.animate()
                .alpha(1f)
                .setDuration(2000)
                .start()
        }, 4000)

    }

    private fun startScoreCounterAnimation(startValue: Int, endValue: Int) {
        val animator = ValueAnimator.ofInt(startValue, endValue)
        animator.duration = 3000 // Trajanje animacije u milisekundama

        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            scoreTextView.text = animatedValue.toString()
        }

        animator.start()
    }

    private fun startHighScoreCounterAnimation(startValue: Int, endValue: Int) {
        val animator = ValueAnimator.ofInt(startValue, endValue)
        animator.duration = 3000 // Trajanje animacije u milisekundama

        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            highScoreTextView.text = animatedValue.toString()
        }

        animator.start()
    }

    private fun iscitajBodoveIzBaze(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserEmail = currentUser?.email

        if (!currentUserEmail.isNullOrEmpty()) {
            val database = FirebaseDatabase.getInstance().reference
            val korisniciRef = database.child("Korisnici")

            val query = korisniciRef.orderByChild("email").equalTo(currentUserEmail)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                //DataSnapshot predstavlja trenutno stanje podataka na određenom čvoru baze podataka u određenom trenutku
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //snapshot predstavlja jedno dijete (child)
                    for (snapshot in dataSnapshot.children) {
                        val rezultat = snapshot.child("liga_prvaka_score").value as? Long
                        val maxRezultat = snapshot.child("liga_prvaka_highScore").value as? Long

                        score = rezultat?.toInt() ?: 0
                        highScore = maxRezultat?.toInt() ?: 0

                        // Prekini petlju nakon pronalaska odgovarajućeg korisnika
                        break
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        context,
                        "Greška prilikom dohvaćanja podataka",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(context, "Korisnik nije ulogiran", Toast.LENGTH_SHORT).show()
        }
    }

    private fun konfetiAnimacija(){

        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )
        konfettiView.start(party)
    }
}
