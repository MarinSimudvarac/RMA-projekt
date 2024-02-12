package com.example.footballquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.footballquiz.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var pitanjaRef: DatabaseReference
    private lateinit var database: DatabaseReference
    private lateinit var showHidePasswordButton: ImageButton
    private lateinit var showHidePasswordButton2: ImageButton
    private lateinit var editTextRegPassword: EditText
    private lateinit var editTextRegConfirmPassword: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pitanjaRef = FirebaseDatabase.getInstance().getReference("Pitanja")

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        showHidePasswordButton = findViewById(R.id.showHidePasswordButton)
        showHidePasswordButton2 = findViewById(R.id.showHidePasswordButton2)
        editTextRegPassword = findViewById(R.id.editTextRegPassword)
        editTextRegConfirmPassword = findViewById(R.id.editTextRegConfirmPassword)

        showHidePasswordButton.setOnClickListener {
            if ( editTextRegPassword.transformationMethod is PasswordTransformationMethod) {
                // Trenutno je prikazana skrivena lozinka, promijeni način prikaza u običan tekst
                editTextRegPassword.transformationMethod = null
                showHidePasswordButton.setImageResource(R.drawable.ic_password_visibility_on)
            } else {
                // Trenutno je prikazan običan tekst, promijeni način prikaza u skrivenu lozinku
                editTextRegPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                showHidePasswordButton.setImageResource(R.drawable.ic_password_visibility_off)
            }

            // Osvježi tekst kako bi se prikazala nova transformacija, postavljanje kursora na kraj teksta unutar EditText polja,
            // editTextRegPassword.text?.length ?: 0 će vratiti duljinu teksta unutar EditText polja ako tekst nije null, inače će vratiti 0.
            editTextRegPassword.setSelection( editTextRegPassword.text?.length ?: 0)
        }

        showHidePasswordButton2.setOnClickListener {
            if (editTextRegConfirmPassword.transformationMethod is PasswordTransformationMethod) {
                // Trenutno je prikazana skrivena lozinka, promijeni način prikaza u običan tekst
                editTextRegConfirmPassword.transformationMethod = null
                showHidePasswordButton2.setImageResource(R.drawable.ic_password_visibility_on)
            } else {
                // Trenutno je prikazan običan tekst, promijeni način prikaza u skrivenu lozinku
                editTextRegConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                showHidePasswordButton2.setImageResource(R.drawable.ic_password_visibility_off)
            }

            // Osvježi tekst kako bi se prikazala nova transformacija, postavljanje kursora na kraj teksta unutar EditText polja,
            // editTextRegPassword.text?.length ?: 0 će vratiti duljinu teksta unutar EditText polja ako tekst nije null, inače će vratiti 0.
            editTextRegConfirmPassword.setSelection(editTextRegConfirmPassword.text?.length ?: 0)
        }

        binding.buttonRegister.setOnClickListener{
            createQuestionsInDatabase()
            val username = binding.editTextRegUsername.text.toString()
            val email = binding.editTextRegEmail.text.toString()
            val password = binding.editTextRegPassword.text.toString()
            val confirmPassword = binding.editTextRegConfirmPassword.text.toString()

            val user = User(username, email)

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password == confirmPassword){
                    database = FirebaseDatabase.getInstance().getReference("Korisnici")
                    database.child(username).setValue(user).addOnSuccessListener {
                        Toast.makeText(this,"Uspješno spremljeno", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        Toast.makeText(this,"Greška", Toast.LENGTH_SHORT).show()
                    }
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful){
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else{
                    Toast.makeText(this, "Zaporka se ne podudara", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Polja ne mogu biti prazna", Toast.LENGTH_SHORT).show()
            }
        }
        binding.textViewExistingUser.setOnClickListener{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun createQuestionsInDatabase() {

        //Liga prvaka Laka kategorija
        val question1 = Question("1", "Koji je najtrofejniji klub u povijesti Lige prvaka do 2023. godine?", listOf("Real Madrid", "Barcelona", "Bayern Munich", "Manchester City"), "Real Madrid")
        val question2 = Question("2", "Koji igrač ima najviše odigranih utakmica u povijesti Lige prvaka?", listOf("Iker Casillas", "Lionel Messi", "Cristiano Ronaldo", "Toni Kroos"), "Cristiano Ronaldo")
        val question3 = Question("3", "Koji igrač ima najviše postignutih golova u povijesti Lige prvaka?", listOf("Robert Lewandowski", "Lionel Messi", "Karim Benzema", "Cristiano Ronaldo"), "Cristiano Ronaldo")
        val question4 = Question("4", "Koji od navedenih klubova nikad nije osvojio Ligu prvaka?", listOf("Barcelona", "HSV", "Nottingham Forest", "Atletico Madrid"), "Atletico Madrid")
        val question5 = Question("5", "Koji klub je prvi osvojio naslov Kupa prvaka odnosno Lige prvaka?", listOf("Bayern Munich", "Real Madrid", "PSG", "Juventus"), "Real Madrid")
        val question6 = Question("6", "Od koje godine se Liga prvaka tako i službeno zove?", listOf("1992", "1990", "1997", "1995"), "1992")
        val question7 = Question("7", "Kako se zvala Liga Prvaka prije nego što je službeno promijenila ime?", listOf("Kup pobjednika kupova", "Europski kup/Kup prvaka", "Europska liga", "Super kup"), "Europski kup/Kup prvaka")
        val question8 = Question("8", "Koliko klubova bude ukupno u grupnoj fazi Lige prvaka?", listOf("16", "24", "32", "64"), "32")
        val question9 = Question("9", "Koliko skupina ima grupna faza Lige prvaka?", listOf("4", "6", "8", "16"), "8")
        val question10 = Question("10", "Koji klub je jedini obranio titulu prvaka 3 puta za redom?", listOf("Milan", "Real Madrid", "Barcelona", "Bayern Munich"), "Real Madrid")

        pitanjaRef.child("Liga prvaka").child("Laka kategorija").child(question1.id).setValue(question1)
        pitanjaRef.child("Liga prvaka").child("Laka kategorija").child(question2.id).setValue(question2)
        pitanjaRef.child("Liga prvaka").child("Laka kategorija").child(question3.id).setValue(question3)
        pitanjaRef.child("Liga prvaka").child("Laka kategorija").child(question4.id).setValue(question4)
        pitanjaRef.child("Liga prvaka").child("Laka kategorija").child(question5.id).setValue(question5)
        pitanjaRef.child("Liga prvaka").child("Laka kategorija").child(question6.id).setValue(question6)
        pitanjaRef.child("Liga prvaka").child("Laka kategorija").child(question7.id).setValue(question7)
        pitanjaRef.child("Liga prvaka").child("Laka kategorija").child(question8.id).setValue(question8)
        pitanjaRef.child("Liga prvaka").child("Laka kategorija").child(question9.id).setValue(question9)
        pitanjaRef.child("Liga prvaka").child("Laka kategorija").child(question10.id).setValue(question10)

        //Liga Prvaka Srednje teška kategorija
        val question11 = Question("11", "Koje godine prije 2023. je drastično promijenjen kvalifikacijski sustav Lige prvaka?", listOf("1997", "1995", "1992", "1979"), "1997")
        val question12 = Question("12", "Koji golman ima najviše odigranih utakmica u povijesti Lige prvaka?", listOf("Iker Casillas", "Gianluigi Buffon", "Manuel Neuer", "Jan Oblak"), "Iker Casillas")
        val question13 = Question("13", "Koji igrač zauzima četvrto mjesto u tablici postignutih golova u povijesti Lige prvaka?", listOf("Robert Lewandowski", "Raul", "Van Nistelrooy", "Karim Benzema"), "Karim Benzema")
        val question14 = Question("14", "Koji od navedenih klubova ima najmanje sezona u kojima su se kvalificirali za Ligu prvaka?", listOf("Lyon", "Atletico Madrid", "Chelsea", "PSG"), "PSG")
        val question15 = Question("15", "Koji od navedenih klubova zauzima 2. mjesto po broju sezona u kojima su se kvalificirali u Ligu prvaka?", listOf("Bayern Munich", "Benfica", "PSG", "Barcelona"), "Benfica")
        val question16 = Question("16", "Na koliko jezika se izvodi himna Lige prvaka?", listOf("1", "2", "3", "4"), "3")
        val question17 = Question("17", "Koji klub je osvojio 3 titule Lige prvaka za redom od 1973. do 1976. godine?", listOf("Ajax", "Real Madrid", "Milan", "Bayern Munich"), "Bayern Munich")
        val question18 = Question("18", "Koliko osvojenih Liga prvaka ima AJAX?", listOf("1", "3", "4", "2"), "4")
        val question19 = Question("19", "Koje godine je ukinuto pravilo gola u gostima?", listOf("2020", "2023", "2018", "2021"), "2021")
        val question20 = Question("20", "U kojem klubu je Zlatan Ibrahimović postigao većinu svojih golova u Ligi prvaka?", listOf("Ajax", "Milan", "Barcelona", "PSG"), "PSG")

        pitanjaRef.child("Liga prvaka").child("Srednje teška kategorija").child(question11.id).setValue(question11)
        pitanjaRef.child("Liga prvaka").child("Srednje teška kategorija").child(question12.id).setValue(question12)
        pitanjaRef.child("Liga prvaka").child("Srednje teška kategorija").child(question13.id).setValue(question13)
        pitanjaRef.child("Liga prvaka").child("Srednje teška kategorija").child(question14.id).setValue(question14)
        pitanjaRef.child("Liga prvaka").child("Srednje teška kategorija").child(question15.id).setValue(question15)
        pitanjaRef.child("Liga prvaka").child("Srednje teška kategorija").child(question16.id).setValue(question16)
        pitanjaRef.child("Liga prvaka").child("Srednje teška kategorija").child(question17.id).setValue(question17)
        pitanjaRef.child("Liga prvaka").child("Srednje teška kategorija").child(question18.id).setValue(question18)
        pitanjaRef.child("Liga prvaka").child("Srednje teška kategorija").child(question19.id).setValue(question19)
        pitanjaRef.child("Liga prvaka").child("Srednje teška kategorija").child(question20.id).setValue(question20)

        //Liga prvaka Teška kategorija
        val question21 = Question("21", "Koji klub je peti po redu po broju osvojenih titula Lige prvaka?", listOf("Bayern Munich", "Ajax", "Liverpool", "Barcelona"), "Barcelona")
        val question22 = Question("22", "Koje godine je Porto osvojio svoju zadnju titulu u Ligi prvaka", listOf("2005", "2004", "2003", "2002"), "2004")
        val question23 = Question("23", "Grčki klubovi su samo jednom nastupili u finalu Lige prvaka. Koji klub je to bio?", listOf("AEK", "Olympiacos", "Paok", "Panathinaikos"), "Panathinaikos")
        val question24 = Question("24", "Celtic je svoju jedinu titulu osvojio 1967. godine. Protiv koga je igrao u finalu?", listOf("Lyon", "Real Madrid", "Milan", "Inter Milan"), "Inter Milan")
        val question25 = Question("25", "Koji klub uz Marseille ima najviše nastupa u finalima od francuskih klubova?", listOf("PSG", "Reims", "Monaco", "St-Etienne"), "Reims")
        val question26 = Question("26", "Koji igrač stoji na 2. mjestu po broju nastupa u Ligi prvaka", listOf("Lionel Messi", "Karim Benzema", "Iker Casillas", "Xavi"), "Iker Casillas")
        val question27 = Question("27", "Koji klub je osvojio 3 titule Lige prvaka za redom od 1970. do 1973. godine?", listOf("Ajax", "Real Madrid", "Milan", "Bayern Munich"), "Ajax")
        val question28 = Question("28", "Tko izvodi himnu Lige prvaka?", listOf("Bečka filharmonija", "Berlinska filharmonija", "Engleska kraljevska filharmonija", "Budimpeštanski festivalski orkestar"), "Engleska kraljevska filharmonija")
        val question29 = Question("29", "Koje godine je Nottingham Forest osvojio svoju prvu titulu Lige prvaka?", listOf("1981", "1980", "1978", "1979"), "1979")
        val question30 = Question("30", "Koji igrač ulazi u listu top 10 igrača s najviše nastupa te je nastupao u samo jednom klubu u Ligi prvaka?", listOf("Thomas Muller", "Lionel Messi", "Karim Benzema", "Xavi"), "Xavi")

        pitanjaRef.child("Liga prvaka").child("Teška kategorija").child(question21.id).setValue(question21)
        pitanjaRef.child("Liga prvaka").child("Teška kategorija").child(question22.id).setValue(question22)
        pitanjaRef.child("Liga prvaka").child("Teška kategorija").child(question23.id).setValue(question23)
        pitanjaRef.child("Liga prvaka").child("Teška kategorija").child(question24.id).setValue(question24)
        pitanjaRef.child("Liga prvaka").child("Teška kategorija").child(question25.id).setValue(question25)
        pitanjaRef.child("Liga prvaka").child("Teška kategorija").child(question26.id).setValue(question26)
        pitanjaRef.child("Liga prvaka").child("Teška kategorija").child(question27.id).setValue(question27)
        pitanjaRef.child("Liga prvaka").child("Teška kategorija").child(question28.id).setValue(question28)
        pitanjaRef.child("Liga prvaka").child("Teška kategorija").child(question29.id).setValue(question29)
        pitanjaRef.child("Liga prvaka").child("Teška kategorija").child(question30.id).setValue(question30)

        // SP i EP Laka kategorija
        val question31 = Question("31", "Koje zemlje imaju najviše osvojenih Europskih prvenstava?", listOf("Njemačka i Italija", "Njemačka i Španjolska", "Španjolska i Italija", "Francuska i Italija"), "Njemačka i Španjolska")
        val question32 = Question("32", "Od koje godine se igra Europsko prvenstvo?", listOf("1960", "1962", "1958", "1956"), "1960")
        val question33 = Question("33", "Gdje je odigrano prvo Europsko prvenstvo?", listOf("Španjolska", "Francuska", "Italija", "Njemačka"), "Francuska")
        val question34 = Question("34", "Koliko reprezentacija je bilo na EP 2020/21?", listOf("16", "20", "24", "32"), "24")
        val question35 = Question("35", "Koja zemlja ima najviše osvojenih Svjetskih prvenstava?", listOf("Njemačka", "Brazil", "Argentina", "Francuska"), "Brazil")
        val question36 = Question("36", "Od koje godine se igra Svjetsko prvenstvo?", listOf("1930", "1926", "1928", "1932"), "1930")
        val question37 = Question("37", "Gdje je odigrano prvo Svjetsko prvenstvo?", listOf("Argentina", "Urugvaj", "Francuska", "Brazil"), "Urugvaj")
        val question38 = Question("38", "Koliko reprezentacija je bilo na SP 2022. u Kataru?", listOf("16", "24", "32", "64"), "32")
        val question39 = Question("39", "Koliko skupina ima grupna faza Svjetskog prvenstva?", listOf("4", "6", "8", "16"), "8")
        val question40 = Question("40", "Tko nadgleda Svjetsko prvenstvo?", listOf("UEFA", "FIFA", "FIFA i UEFA", "Nijedna od ponuđenih"), "FIFA")

        pitanjaRef.child("SP i EP").child("Laka kategorija").child(question31.id).setValue(question31)
        pitanjaRef.child("SP i EP").child("Laka kategorija").child(question32.id).setValue(question32)
        pitanjaRef.child("SP i EP").child("Laka kategorija").child(question33.id).setValue(question33)
        pitanjaRef.child("SP i EP").child("Laka kategorija").child(question34.id).setValue(question34)
        pitanjaRef.child("SP i EP").child("Laka kategorija").child(question35.id).setValue(question35)
        pitanjaRef.child("SP i EP").child("Laka kategorija").child(question36.id).setValue(question36)
        pitanjaRef.child("SP i EP").child("Laka kategorija").child(question37.id).setValue(question37)
        pitanjaRef.child("SP i EP").child("Laka kategorija").child(question38.id).setValue(question38)
        pitanjaRef.child("SP i EP").child("Laka kategorija").child(question39.id).setValue(question39)
        pitanjaRef.child("SP i EP").child("Laka kategorija").child(question40.id).setValue(question40)

        // SP i EP Srednje teška kategorija
        val question41 = Question("41", "Koje godine nije odigrano Svjetsko prvenstvo?", listOf("1942", "1950", "1954", "1938"), "1942")
        val question42 = Question("42", "Tko je najbolji strijelac u povijesti SP?", listOf("Miroslav Klose", "Ronaldo (pravi)", "Cristiano Ronaldo", "Gerd Muller"), "Miroslav Klose")
        val question43 = Question("43", "Koliko reprezentacija je uzelo medalje na SP?", listOf("15", "25", "10", "20"), "20")
        val question44 = Question("44", "Koji je najteži poraz organizatora natjecanja na SP?", listOf("8:0", "8:1", "7:0", "7:1"), "7:1")
        val question45 = Question("45", "Maskota SP 2018. u Rusiji zvala se Zabivaka, koju životinju je predstavljala?", listOf("Medvjeda", "Vuka", "Lava", "Duha"), "Vuka")
        val question46 = Question("46", "Koje države su prve dobile pravo na dvojno domaćinstvo Europskih prvenstava?", listOf("Austrija i Švicarska", "Švedska i Danska", "Belgija i Nizozemska", "Poljska i Ukrajina"), "Belgija i Nizozemska")
        val question47 = Question("47", "Koja reprezentacija je osvojila Europsko prvenstvo 2004.?", listOf("Francuska", "Španjolska", "Portugal", "Grčka"), "Grčka")
        val question48 = Question("48", "U koliko se država odigralo Europsko prvenstvo 2020/21.?", listOf("1", "2", "12", "10"), "12")
        val question49 = Question("49", "Na kojem stadionu je odigrano finale Europskog prvenstva 2020/21.?", listOf("Wembley", "Camp Nou", "Allianz Arena", "Park Prinčeva"), "Wembley")
        val question50 = Question("50", "Tko dijeli 1. mjesto na listi najboljih strijelaca Europskih prvenstava? ", listOf("Platini i Shearer", "Shearer i Cristiano Ronaldo", "Cristiano Ronaldo i Ruud Van Nistelrooy", "Platini i Cristiano Ronaldo"), "Platini i Cristiano Ronaldo")

        pitanjaRef.child("SP i EP").child("Srednje teška kategorija").child(question41.id).setValue(question41)
        pitanjaRef.child("SP i EP").child("Srednje teška kategorija").child(question42.id).setValue(question42)
        pitanjaRef.child("SP i EP").child("Srednje teška kategorija").child(question43.id).setValue(question43)
        pitanjaRef.child("SP i EP").child("Srednje teška kategorija").child(question44.id).setValue(question44)
        pitanjaRef.child("SP i EP").child("Srednje teška kategorija").child(question45.id).setValue(question45)
        pitanjaRef.child("SP i EP").child("Srednje teška kategorija").child(question46.id).setValue(question46)
        pitanjaRef.child("SP i EP").child("Srednje teška kategorija").child(question47.id).setValue(question47)
        pitanjaRef.child("SP i EP").child("Srednje teška kategorija").child(question48.id).setValue(question48)
        pitanjaRef.child("SP i EP").child("Srednje teška kategorija").child(question49.id).setValue(question49)
        pitanjaRef.child("SP i EP").child("Srednje teška kategorija").child(question50.id).setValue(question50)

        // SP i EP Teška kategorija
        val question51 = Question("51", "Koja reprezentacija je prva uspjela obraniti naslov prvaka na SP?", listOf("Brazil", "Urugvaj", "Francuska", "Italija"), "Italija")
        val question52 = Question("52", "Koje godine je prvi puta u povijesti Svjetsko Prvenstvo bilo popraćeno izravnim televizijskim prijenosom?", listOf("1950", "1954", "1958", "1962"), "1954")
        val question53 = Question("53", "Koji igrač ima najviše odigranih utakmica na SP?", listOf("Cristiano Ronaldo", "Gerd Muller", "Paolo Maldini", "Matthaus"), "Matthaus")
        val question54 = Question("54", "Hrvatska reprezentacija je dva puta igrala za treće mjesto na SP i oba puta je dobila tu utakmicu s istim rezultatom. Koji rezultat je bio?", listOf("1:0", "3:2", "2:0", "2:1"), "2:1")
        val question55 = Question("55", "Na ikonskom SP-u u JAR-u 2010. prednjačio je koji „instrument“ odnosno navijački rekvizit?", listOf("Baklje", "Vuvuzele", "Bubnjevi", "Frule"), "Vuvuzele")
        val question56 = Question("56", "Dokle je Hrvatska najdalje dogurala na Europskim prvenstvima?", listOf("Polufinale", "Osmina finala", "Četvrtfinale", "Skupina"), "Četvrtfinale")
        val question57 = Question("57", "Danska ima jedan naslov na Europskim prvenstvima i to je bilo kada se uopće nisu uspjeli kvalificirati za to prvenstvo. Koje godine je to bilo?", listOf("1992", "1988", "1996", "2000"), "1992")
        val question58 = Question("58", "Jugoslavija je 1976. ugostila Europsko prvenstvo. U finalu te godine bili su Zapadna Njemačka i zemlja koja je tada osvojila naslov pobjednika ______", listOf("Jugoslavija", "Francuska", "Čehoslovačka", "Španjolska"), "Čehoslovačka")
        val question59 = Question("59", "Na prvom Europskom prvenstvu sve 3 medalje uzele su države koje više „ne postoje“. To su bile prvoplasirani Sovjetski savez, drugoplasirana ___________ ? i treće plasirana Čehoslovačka", listOf("Istočna Njemačka", "Zapadna Njemačka", "Kraljevina SHS", "Jugoslavija"), "Jugoslavija")
        val question60 = Question("60", "Od kojeg Svjetskog prvenstva se ne igra utakmica za 3. mjesto?", listOf("Nikada se nije ni igralo za 3. mjesto", "1972", "1996", "1984"), "1984")

        pitanjaRef.child("SP i EP").child("Teška kategorija").child(question51.id).setValue(question51)
        pitanjaRef.child("SP i EP").child("Teška kategorija").child(question52.id).setValue(question52)
        pitanjaRef.child("SP i EP").child("Teška kategorija").child(question53.id).setValue(question53)
        pitanjaRef.child("SP i EP").child("Teška kategorija").child(question54.id).setValue(question54)
        pitanjaRef.child("SP i EP").child("Teška kategorija").child(question55.id).setValue(question55)
        pitanjaRef.child("SP i EP").child("Teška kategorija").child(question56.id).setValue(question56)
        pitanjaRef.child("SP i EP").child("Teška kategorija").child(question57.id).setValue(question57)
        pitanjaRef.child("SP i EP").child("Teška kategorija").child(question58.id).setValue(question58)
        pitanjaRef.child("SP i EP").child("Teška kategorija").child(question59.id).setValue(question59)
        pitanjaRef.child("SP i EP").child("Teška kategorija").child(question60.id).setValue(question60)

        // Liga Petice Laka kategorija
        val question61 = Question("61", "Koja od navedenih liga ne spada u skupinu Liga pet?", listOf("La Liga", "Belgium Jupiler Pro League", "Ligue 1", "Bundesliga"), "Belgium Jupiler Pro League")
        val question62 = Question("62", "Koje godine je nastala prva nogometna liga (engleska Premier League)?", listOf("1888", "1921", "1904", "1875"), "1888")
        val question63 = Question("63", "U kojem gradu je nastala prva nogometna liga na svijetu?", listOf("London", "Manchester", "Liverpool", "Birmingham"), "Manchester")
        val question64 = Question("64", "Koliko klubova je bilo u prvoj nogometnoj ligi?", listOf("10", "20", "12", "18"), "12")
        val question65 = Question("65", "Tko je prvi osvojio prvu nogometnu ligu na svijetu?", listOf("Burnley", "Preston North End", "Aston Villa", "Blackburn Rovers"), "Preston North End")
        val question66 = Question("66", "Koji klub je u Francuskoj ligi uspio osvojiti istu čak 7 puta zaredom?", listOf("Bordeaux", "Marseille", "PSG", "Monaco"), "Bordeaux")
        val question67 = Question("67", "Koji je najtrofejniji klub u povijesti Serie A?", listOf("Inter", "Juventus", "Milan", "Genoa"), "Juventus")
        val question68 = Question("68", "Koji je najtrofejniji klub u povijesti Bundeslige?", listOf("Koln", "Werder Bremen", "Bayern Munich", "Wolfsburg"), "Bayern Munich")
        val question69 = Question("69", "Koji je najtrofejniji klub u povijesti La Lige?", listOf("Atletico Madrid", "Barcelona", "Real Madrid", "Athletic Bilbao"), "Real Madrid")
        val question70 = Question("70", "Koji je najtrofejniji klub u povijesti engleske Premier League?", listOf("Liverpool", "Manchester United", "Manchester City", "Arsenal"), "Manchester United")

        pitanjaRef.child("Liga petice").child("Laka kategorija").child(question61.id).setValue(question61)
        pitanjaRef.child("Liga petice").child("Laka kategorija").child(question62.id).setValue(question62)
        pitanjaRef.child("Liga petice").child("Laka kategorija").child(question63.id).setValue(question63)
        pitanjaRef.child("Liga petice").child("Laka kategorija").child(question64.id).setValue(question64)
        pitanjaRef.child("Liga petice").child("Laka kategorija").child(question65.id).setValue(question65)
        pitanjaRef.child("Liga petice").child("Laka kategorija").child(question66.id).setValue(question66)
        pitanjaRef.child("Liga petice").child("Laka kategorija").child(question67.id).setValue(question67)
        pitanjaRef.child("Liga petice").child("Laka kategorija").child(question68.id).setValue(question68)
        pitanjaRef.child("Liga petice").child("Laka kategorija").child(question69.id).setValue(question69)
        pitanjaRef.child("Liga petice").child("Laka kategorija").child(question70.id).setValue(question70)

        // Liga Petice Srednje teška kategorija
        val question71 = Question("71", "Tko je najbolji strijelac u povijesti engleske Premier League?", listOf("Alan Shearer", "Wayne Rooney", "Andy Cole", "Sergio Aguero"), "Alan Shearer")
        val question72 = Question("72", "Od 2001. do 2008. godine glavni sponzor engleske Premier League bio je?", listOf("Barclays", "Carling", "Gazprom", "Heineken"), "Barclays")
        val question73 = Question("73", "Najtrofejniji klub u La Ligi po broju naslova domaćeg prvenstva je?", listOf("Valencia", "Real Sociedad", "Sevilla", "Athletic Bilbao"), "Athletic Bilbao")
        val question74 = Question("74", "Od 2008. do 2016. godine glavni sponozor La Lige bio je?", listOf("Banco Santander", "Qatar Airways", "Samsung", "BBVA"), "BBVA")
        val question75 = Question("75", "Koji od sljedećih klubova iz Ligue 1 ima najviše osvojenih naslova prvaka?", listOf("Lyon", "Nantes", "Girondins de Bordeaux", "Lille"), "Nantes")
        val question76 = Question("76", "Najmanje poraza (1) u jednoj sezoni Ligue 1 (1994./95.) uspio je ostvariti koji klub?", listOf("PSG", "Lille", "Nantes", "Bordeaux"), "Nantes")
        val question77 = Question("77", "Koji je treći najtrofejniji klub u Bundesligi?", listOf("Wolfsburg", "Borussia Monchengladbach", "Borussia Dortmund", "Nurnberg"), "Nurnberg")
        val question78 = Question("78", "Od svih liga 5, Bundesliga ima najmanje klubova u jednoj sezoni koji se natječu. Koliko ih ima?", listOf("16", "20", "18", "22"), "18")
        val question79 = Question("79", "Koji klub ima najviše sezona provedenih u Serie A?", listOf("Inter", "Juventus", "Roma", "AC Milan"), "Inter")
        val question80 = Question("80", "Ako u Serie A dva kluba imaju isto bodova, koji je prvi tie-breaker u tom slučaju?", listOf("Tko ima veći broj postignutih golova", "Gol razlika", "Gol razlika u H2H", "H2H bodovi"), "H2H bodovi")

        pitanjaRef.child("Liga petice").child("Srednje teška kategorija").child(question71.id).setValue(question71)
        pitanjaRef.child("Liga petice").child("Srednje teška kategorija").child(question72.id).setValue(question72)
        pitanjaRef.child("Liga petice").child("Srednje teška kategorija").child(question73.id).setValue(question73)
        pitanjaRef.child("Liga petice").child("Srednje teška kategorija").child(question74.id).setValue(question74)
        pitanjaRef.child("Liga petice").child("Srednje teška kategorija").child(question75.id).setValue(question75)
        pitanjaRef.child("Liga petice").child("Srednje teška kategorija").child(question76.id).setValue(question76)
        pitanjaRef.child("Liga petice").child("Srednje teška kategorija").child(question77.id).setValue(question77)
        pitanjaRef.child("Liga petice").child("Srednje teška kategorija").child(question78.id).setValue(question78)
        pitanjaRef.child("Liga petice").child("Srednje teška kategorija").child(question79.id).setValue(question79)
        pitanjaRef.child("Liga petice").child("Srednje teška kategorija").child(question80.id).setValue(question80)

        // Liga Petice Teška kategorija
        val question81 = Question("81", "Koji igrač je 3. po broju nastupa u Serie A (ikona)?", listOf("Buffon", "Paolo Maldini", "Javier Zanetti", "Francesco Totti"), "Francesco Totti")
        val question82 = Question("82", "Tko je postigao najviše golova u povijesti Serie A?", listOf("Francesco Totti", "Silvio Piola", "Antonio Di Natale", "Roberto Baggio"), "Silvio Piola")
        val question83 = Question("83", "Koji igrač ima najviše odigranih utakmica u Bundesligi?", listOf("Oliver Kahn", "Gerd Muller", "Eike Immel", "Charly Korbel"), "Charly Korbel")
        val question84 = Question("84", "Najbolji strijelac Bundeslige u povijesti je?", listOf("Robert Lewandowski", "Claudio Pizzaro", "Jupp Heynckes", "Gerd Muller"), "Gerd Muller")
        val question85 = Question("85", "Koje godine se La Liga zadnji puta prebacila na format od 20 klubova u prvenstvu?", listOf("1995", "1997", "1987", "2000"), "1997")
        val question86 = Question("86", "Tko je u počecima La Lige (prvih 10 godina) imao najviše osvojenih naslova prvaka?", listOf("Real Madrid", "Barcelona", "Athletich Bilbao", "Atletico Madrid"), "Athletich Bilbao")
        val question87 = Question("87", "Koji klub drži rekord za najveću gol razliku u sezoni Ligue 1?", listOf("Stade de Reims", "Lyon", "PSG", "Nantes"), "Stade de Reims")
        val question88 = Question("88", "U 2023. godini prema UEFA-inim koeficijentima francuska Ligue 1 više nije bila u ligi 5, koja liga je zauzela njeno mjesto?", listOf("Portugalska liga", "Belgijska Jupiler Pro Liga", "Nizozemska Erdevise", "Hrvatski HNL"), "Nizozemska Erdevise")
        val question89 = Question("89", "Premier League predvode većinom klubovi iz Londona. Koliko ih ima u sezoni 2023./24.?", listOf("5", "8", "6", "7"), "7")
        val question90 = Question("90", "1994. godine sadašnji posrnuli gigant osvojio je englesku ligu. Na koji klub se misli?", listOf("Nottingham Forest", "Birmingham", "QPR", "Blackburn Rovers"), "Blackburn Rovers")

        pitanjaRef.child("Liga petice").child("Teška kategorija").child(question81.id).setValue(question81)
        pitanjaRef.child("Liga petice").child("Teška kategorija").child(question82.id).setValue(question82)
        pitanjaRef.child("Liga petice").child("Teška kategorija").child(question83.id).setValue(question83)
        pitanjaRef.child("Liga petice").child("Teška kategorija").child(question84.id).setValue(question84)
        pitanjaRef.child("Liga petice").child("Teška kategorija").child(question85.id).setValue(question85)
        pitanjaRef.child("Liga petice").child("Teška kategorija").child(question86.id).setValue(question86)
        pitanjaRef.child("Liga petice").child("Teška kategorija").child(question87.id).setValue(question87)
        pitanjaRef.child("Liga petice").child("Teška kategorija").child(question88.id).setValue(question88)
        pitanjaRef.child("Liga petice").child("Teška kategorija").child(question89.id).setValue(question89)
        pitanjaRef.child("Liga petice").child("Teška kategorija").child(question90.id).setValue(question90)


    }
}