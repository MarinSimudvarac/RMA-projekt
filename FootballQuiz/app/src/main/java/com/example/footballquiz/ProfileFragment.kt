package com.example.footballquiz

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var buttonLogout: Button
    private lateinit var usernameTextView: TextView
    private lateinit var liga_prvaka_highscoreTextView: TextView
    private lateinit var spiep_highscoreTextView: TextView
    private lateinit var liga_petice_highscoreTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        buttonLogout = view.findViewById(R.id.buttonLogout)
        usernameTextView = view.findViewById(R.id.usernameTextView)
        liga_prvaka_highscoreTextView = view.findViewById(R.id.liga_prvaka_highscoreTextView)
        spiep_highscoreTextView = view.findViewById(R.id.spiep_highscoreTextView)
        liga_petice_highscoreTextView = view.findViewById(R.id.liga_petice_highscoreTextView)


        buttonLogout.setOnClickListener{
            firebaseAuth.signOut()
            val loginIntent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(loginIntent)
            requireActivity().finish()
        }


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
                        val username = snapshot.child("username").value.toString()
                        val liga_prvaka_highScore = snapshot.child("liga_prvaka_highScore").value.toString()
                        val spiep_highScore = snapshot.child("spiep_highScore").value.toString()
                        val liga_petice_highScore = snapshot.child("liga_petice_highScore").value.toString()


                        usernameTextView.text = username
                        liga_prvaka_highscoreTextView.text = liga_prvaka_highScore
                        spiep_highscoreTextView.text = spiep_highScore
                        liga_petice_highscoreTextView.text = liga_petice_highScore


                        // Prekini petlju nakon pronalaska odgovarajućeg korisnika
                        break
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "Greška prilikom dohvaćanja podataka", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "Korisnik nije ulogiran", Toast.LENGTH_SHORT).show()
        }
    }
}