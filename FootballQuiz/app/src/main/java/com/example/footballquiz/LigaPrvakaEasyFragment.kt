package com.example.footballquiz

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.math.sqrt
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mikhaellopez.circularprogressbar.CircularProgressBar


@Suppress("DEPRECATION")
class LigaPrvakaEasyFragment : Fragment() {

    private lateinit var pitanjaRef: DatabaseReference
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var pitanjeTextView: TextView
    private lateinit var brojPitanjaTextView: TextView
    private lateinit var odgovor1Button: Button
    private lateinit var odgovor2Button: Button
    private lateinit var odgovor3Button: Button
    private lateinit var odgovor4Button: Button
    private lateinit var shakePhoneIcon: ImageView
    private lateinit var timerTextView: TextView
    private var timer: CountDownTimer? = null
    private var preostaloVrijeme: Long = 10000
    private var trenutnoPitanje: Int = 0
    private var brojPitanja: Int = 1
    private var isQuestionSet = false
    private var isShakeDetected = false
    private var isGameActive = true
    private var isFragmentDestroyed = false
    private var prethodnoPostavljenoPitanje: Int = -1
    private var brojTocnihOdgovora = 0
    private lateinit var timerCircularProgressBar: CircularProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflater koristi XML layout za prikazivanje fragmenta
        return inflater.inflate(R.layout.fragment_ligaprvaka_easy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pitanjeTextView = view.findViewById(R.id.pitanjeTextView)
        brojPitanjaTextView = view.findViewById(R.id.brojPitanjaTextView)
        odgovor1Button = view.findViewById(R.id.odgovor1Button)
        odgovor2Button = view.findViewById(R.id.odgovor2Button)
        odgovor3Button = view.findViewById(R.id.odgovor3Button)
        odgovor4Button = view.findViewById(R.id.odgovor4Button)
        shakePhoneIcon = view.findViewById(R.id.shakePhoneIcon)
        timerTextView = view.findViewById(R.id.timerTextView)
        timerCircularProgressBar = view.findViewById(R.id.timerCircularProgressBar)

        odgovor1Button.visibility = View.INVISIBLE
        odgovor2Button.visibility = View.INVISIBLE
        odgovor3Button.visibility = View.INVISIBLE
        odgovor4Button.visibility = View.INVISIBLE

        pitanjaRef = FirebaseDatabase.getInstance().getReference("Pitanja")
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!

        if (accelerometer == null) {
            Toast.makeText(requireContext(), "Senzor protresanja nije podržan na ovom uređaju.", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }

        isQuestionSet = savedInstanceState?.getBoolean("isQuestionSet") ?: false
        if (isQuestionSet) {
            prikaziTrenutnoPitanje()
        } else {
            postaviPocetniTekst()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isQuestionSet", isQuestionSet)
    }

    private fun pokreniIgru() {
        azurirajBrojPitanja()
        if (isShakeDetected && !isQuestionSet) {
            isQuestionSet = true
            trenutnoPitanje = generirajRandomBrojPitanja()
            while (trenutnoPitanje == prethodnoPostavljenoPitanje) {
                // Osigurajte da se ne ponavlja isto pitanje
                trenutnoPitanje = generirajRandomBrojPitanja()
            }
            prethodnoPostavljenoPitanje = trenutnoPitanje
            getQuestion(trenutnoPitanje)
            preostaloVrijeme = 10000  // Resetira timer na početnu vrijednost
            startTimer()
            odgovor1Button.visibility = View.VISIBLE
            odgovor2Button.visibility = View.VISIBLE
            odgovor3Button.visibility = View.VISIBLE
            odgovor4Button.visibility = View.VISIBLE
            isShakeDetected = false
            pitanjeTextView.clearAnimation()
            shakePhoneIcon.clearAnimation()
            shakePhoneIcon.visibility = View.INVISIBLE
        }
    }

    private val accelerometerListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Ignoriramo
        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val acceleration = sqrt((x * x + y * y + z * z).toDouble())
                if (isGameActive && !isQuestionSet && acceleration > SHAKE_THRESHOLD) {
                    isShakeDetected = true
                    pokreniIgru()
                }
            }
        }
    }

    private fun getQuestion(questionNumber: Int) {
        pitanjaRef.child("Liga prvaka").child("Laka kategorija").child("$questionNumber")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val question = snapshot.getValue(Question::class.java)
                    if (question != null) {
                        displayQuestion(question)
                    } else {
                        postaviPocetniTekst()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    postaviPocetniTekst()
                }
            })
    }

    private fun displayQuestion(question: Question) {
        val pitanjeText = question.text
        pitanjeTextView.text = pitanjeText

        odgovor1Button.text = question.options[0]
        odgovor2Button.text = question.options[1]
        odgovor3Button.text = question.options[2]
        odgovor4Button.text = question.options[3]

        odgovor1Button.setOnClickListener { provjeriOdgovor(odgovor1Button, question) }
        odgovor2Button.setOnClickListener { provjeriOdgovor(odgovor2Button, question) }
        odgovor3Button.setOnClickListener { provjeriOdgovor(odgovor3Button, question) }
        odgovor4Button.setOnClickListener { provjeriOdgovor(odgovor4Button, question) }
    }

    private fun provjeriOdgovor(odabraniButton: Button, question: Question) {
        if (timer != null) {
            stopTimer()
        }

        if (odabraniButton.text == question.correctAnswer) {
            odabraniButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_green)) // Postavite boju za točan odgovor
        } else {
            odabraniButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
        }

        //odgoda nakon odgovora
        android.os.Handler().postDelayed({
            if (odabraniButton.text == question.correctAnswer) {
                sljedecePitanje()
            } else {
                zavrsiIgru()
            }
        }, 600)
    }

    private fun startTimer() {
        timer = object : CountDownTimer(preostaloVrijeme, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                preostaloVrijeme = millisUntilFinished
                val seconds = millisUntilFinished / 1000
                timerTextView.text = "$seconds"

                // Ažurirajte CircularProgressBar ovisno o preostalom vremenu
                val progress = ((millisUntilFinished) * 100 / 10000).toInt()
                if(progress <= 10){
                    timerCircularProgressBar.setProgressWithAnimation(0f)
                }else{
                    timerCircularProgressBar.setProgressWithAnimation(progress.toFloat())
                }
            }

            override fun onFinish() {
                vrijemeIsteklo()
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel()
        timerTextView.text = "${preostaloVrijeme / 1000}"
    }

    private fun zavrsiIgru() {
        if (!isFragmentDestroyed) {
            try {
                val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                builder.setTitle("Igra završena!")
                builder.setMessage("Pogrešan odgovor!")
                builder.setPositiveButton("Pokušajte ponovno") { dialog, which ->
                    brojPitanja = 1
                    brojTocnihOdgovora = 0
                    resetirajIgru()
                    pokreniIgru()
                }
                builder.setNegativeButton("Izlaz") { dialog, which ->

                    requireActivity().finish()
                }
                // Postavite Cancelable na false kako biste spriječili korisnika da stisne izvan okvira dijaloga
                builder.setCancelable(false)

                val dialog: AlertDialog = builder.create()

                dialog.window?.attributes?.windowAnimations = R.style.PopupAnimation

                dialog.show()

                sensorManager.unregisterListener(accelerometerListener) // Dodano zaustavljanje slušanja senzora

                isQuestionSet = false
                postaviPocetniTekst()
            } catch (e: Exception) {
                // Obrada iznimke, npr. logiranje
            }
        }
    }

    private fun sljedecePitanje() {
        if (!isFragmentDestroyed) {
            try {
                val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                builder.setTitle("Bravoo!!!")
                builder.setMessage("Točan odgovor!")
                builder.setPositiveButton("Sljedeće pitanje") { dialog, which ->

                    brojPitanja++
                    brojTocnihOdgovora++
                    if (brojTocnihOdgovora == 3) {
                        fragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer, LigaPrvakaMediumFragment())?.commit()
                    } else {
                        resetirajIgru()
                        pokreniIgru()
                    }
                }
                builder.setNegativeButton("Izlaz") { dialog, which ->

                    requireActivity().finish()
                }
                // Postavite Cancelable na false kako biste spriječili korisnika da stisne izvan okvira dijaloga
                builder.setCancelable(false)

                val dialog: AlertDialog = builder.create()

                dialog.window?.attributes?.windowAnimations = R.style.PopupAnimation

                dialog.show()

                sensorManager.unregisterListener(accelerometerListener) // Dodano zaustavljanje slušanja senzora

                isQuestionSet = false
                postaviPocetniTekst()
            } catch (e: Exception) {
                // Obrada iznimke, npr. logiranje
            }
        }
    }

    private fun vrijemeIsteklo() {
        if (!isFragmentDestroyed) {
            try {
                val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                builder.setTitle("Igra završena!")
                builder.setMessage("Vrijeme isteklo!")
                builder.setPositiveButton("Pokušajte ponovno") { dialog, which ->
                    brojPitanja = 1
                    brojTocnihOdgovora = 0
                    resetirajIgru()
                    pokreniIgru()
                }
                builder.setNegativeButton("Izlaz") { dialog, which ->

                    requireActivity().finish()
                }
                // Postavite Cancelable na false kako biste spriječili korisnika da stisne izvan okvira dijaloga
                builder.setCancelable(false)

                val dialog: AlertDialog = builder.create()

                dialog.window?.attributes?.windowAnimations = R.style.PopupAnimation

                dialog.show()

                sensorManager.unregisterListener(accelerometerListener) // Dodano zaustavljanje slušanja senzora

                isQuestionSet = false
                postaviPocetniTekst()
            } catch (e: Exception) {
                // Obrada iznimke, npr. logiranje
            }
        }
    }


    private fun resetirajIgru() {
        odgovor1Button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
        odgovor2Button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
        odgovor3Button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
        odgovor4Button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))

        preostaloVrijeme = 10000
        timerCircularProgressBar.progress = 100F //vraća circularProgressBar na 100% u FLOAT-u
        trenutnoPitanje = 0
        prethodnoPostavljenoPitanje = -1
        isShakeDetected = false
        if (timer != null) {
            stopTimer()
        }
        // Registrirajte senzor samo ako igra nije završena
        if (isGameActive) {
            sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun generirajRandomBrojPitanja(): Int {
        return (1..10).random()
    }

    private fun azurirajBrojPitanja(){
        if (brojPitanja != 1){
            brojPitanjaTextView.text = "Pitanje $brojPitanja od 3:"
        }
        else{
            brojPitanjaTextView.text = "Pitanje 1 od 3:"
        }
    }

    private fun postaviPocetniTekst() {
        val pulseAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.pulse_anim)
        pitanjeTextView.startAnimation(pulseAnimation)
        shakePhoneIcon.startAnimation(pulseAnimation)
        pitanjeTextView.text = "Protresite uređaj za postavljanje pitanja!"
        odgovor1Button.visibility = View.INVISIBLE
        odgovor2Button.visibility = View.INVISIBLE
        odgovor3Button.visibility = View.INVISIBLE
        odgovor4Button.visibility = View.INVISIBLE
    }

    private fun prikaziTrenutnoPitanje() {
        odgovor1Button.visibility = View.VISIBLE
        odgovor2Button.visibility = View.VISIBLE
        odgovor3Button.visibility = View.VISIBLE
        odgovor4Button.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isFragmentDestroyed = true
        sensorManager.unregisterListener(accelerometerListener)
    }

    companion object {
        private const val SHAKE_THRESHOLD = 14.0
    }
}
