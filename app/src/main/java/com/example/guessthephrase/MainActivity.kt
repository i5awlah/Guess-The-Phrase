package com.example.guessthephrase

import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var myLayout: ConstraintLayout
    private lateinit var myRV: RecyclerView
    private lateinit var phraseText: TextView
    private lateinit var guessedLettersText: TextView
    private lateinit var guessField: EditText
    private lateinit var submitButton: Button

    private var messages = arrayListOf<String>()
    private var colors = arrayListOf<Int>()
    private val guessLetters = arrayListOf<Char>()

    private lateinit var phrases: ArrayList<String>
    private var phrase = ""

    private val myAnswerDictionary = mutableMapOf<Int, Char>()
    private var myAnswer = ""
    private var guessedLetters = ""
    private var count = 0

    var guessPhrase = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myLayout = findViewById(R.id.clMain)
        myRV = findViewById(R.id.rvMain)
        myRV.adapter = MessageAdaptor(messages, colors)
        myRV.layoutManager = LinearLayoutManager(this)

        phraseText = findViewById(R.id.tvPhrase)
        guessedLettersText = findViewById(R.id.tvGuessedLetters)

        guessField = findViewById(R.id.etGuessField)
        submitButton = findViewById(R.id.btnSubmitButton)
        submitButton.setOnClickListener { newGuess() }

        playGame()
    }

    // Preserve the state to allow users to rotate the device
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("guessPhrase",guessPhrase)
        outState.putInt("myCount", count)
        outState.putString("myAnswer", myAnswer)
        outState.putString("myGuessedLetters", guessedLetters)
        outState.putString("myPhrase", phrase)
        outState.putStringArrayList("myMessage", messages)
        outState.putIntegerArrayList("myColors", colors)
        //outState.putCharArrayList("myGuessLetters", guessLetters)
        // myAnswerDictionary
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)


        guessPhrase = savedInstanceState.getBoolean("guessPhrase", true)
        count = savedInstanceState.getInt("myCount", 0)

        myAnswer = savedInstanceState.getString("myAnswer", "")
        guessedLetters = savedInstanceState.getString("myGuessedLetters", "")
        phrase = savedInstanceState.getString("myPhrase", "")

        //messages = savedInstanceState.getStringArrayList("myMessage", [])
        //colors = savedInstanceState.getIntegerArrayList("myColors", [])
    }

    fun playGame() {
        myAnswer = ""
        guessedLetters = ""
        count = 0
        guessPhrase = true

        messages.clear()
        colors.clear()
        guessLetters.clear()

        enableEntry()

        phrases = arrayListOf("My son lives in London",
            "She plays basketball",
            "He goes to football every day",
            "He loves to play basketball",
            "Does he go to school?",
            "It usually rains every day here",
            "It smells very delicious in the kitchen",
            "George brushes her teeth twice a day",
            "He gets up early every day",
            "They speak English in USA")

        // Expand the game to randomly select a phrase from a list
        phrase = phrases[Random.nextInt(phrases.size)].toLowerCase()

        convertToStar(phrase)
        updateText()
    }

    private fun newGuess() {
        val userGuess = guessField.text.toString()
        guessField.text.clear()
        guessField.clearFocus()

        if(userGuess.isNotEmpty()) {
            // phrase guess
            if (guessPhrase) {
                guessPhrase = false // to switch between phrase and letter guess
                if (userGuess.toUpperCase() == phrase.toUpperCase()) {
                    messages.add("You got it!")
                    // Change the color of Text Views that display wrong phrases or letter guesses
                    colors.add(Color.GREEN)
                    disableEntry()
                    showAlert("You win!\n\nPlay again?")
                }
                else {
                    messages.add("Wrong guess: $userGuess")
                    colors.add(Color.RED)

                    updateText()
                }
                // Scroll the Recycler View to the bottom each time a new message is added
                myRV.scrollToPosition(messages.size - 1)
            } else { // letter guess
                if (userGuess.length == 1) {
                    // Don't allow the user to guess the same letter twice
                    if (isTwice(userGuess[0])) {
                        Snackbar.make(myLayout,"You shouldn't guess the same letter twice!",Snackbar.LENGTH_LONG).show()
                    }
                    else {
                        guessPhrase = true // to switch between phrase and letter guess
                        myAnswer = ""
                        checkLetters(userGuess[0])
                    }
                }
                else {
                    Snackbar.make(myLayout,"You should enter one letter!",Snackbar.LENGTH_LONG).show()
                }

            }
        }
        else {
            Snackbar.make(myLayout,"Empty guess!",Snackbar.LENGTH_LONG).show()
        }


    }

    private fun updateText() {
        phraseText.text = "Phrase:  " + myAnswer.toUpperCase()
        guessedLettersText.text = "Guessed Letters:  " + guessedLetters
        if (guessPhrase) {
            guessField.hint = "Guess the full phrase"
        }
        else {
            guessField.hint = "Guess a letter"
        }
    }

    private fun isTwice(c: Char) : Boolean {
        for (letter in guessLetters) {
            if (c == letter) return true
        }
        guessLetters.add(c)
        return false
    }

    private fun convertToStar(phrase: String) {
        for(i in phrase.indices){
            if(phrase[i] == ' '){
                myAnswerDictionary[i] = ' '
                myAnswer += ' '
            }else{
                myAnswerDictionary[i] = '*'
                myAnswer += '*'
            }
        }
    }

    private fun checkLetters(guessedLetter: Char){
        var found = 0
        for(i in phrase.indices){
            if(phrase[i] == guessedLetter){
                myAnswerDictionary[i] = guessedLetter
                found++
            }
        }
        for(i in myAnswerDictionary){myAnswer += myAnswerDictionary[i.key]}
        if(myAnswer==phrase){
            disableEntry()
            showAlert("You win!\n\nPlay again?")
        }
        if(guessedLetters.isEmpty()){guessedLetters+=guessedLetter}else{guessedLetters+=", "+guessedLetter}
        if(found>0){
            messages.add("Found $found ${guessedLetter.toUpperCase()}(s)")
            colors.add(Color.GREEN)
        }else{
            messages.add("No ${guessedLetter.toUpperCase()}s found")
            colors.add(Color.RED)
        }
        count++
        val guessesLeft = 10 - count
        if(count<10){
            messages.add("$guessesLeft guesses remaining")
            colors.add(Color.BLACK)
        }
        else {
            disableEntry()
            showAlert("You loss!\n\nPlay again?")
        }
        updateText()
        // Scroll the Recycler View to the bottom each time a new message is added
        myRV.scrollToPosition(messages.size - 1)
    }

    private fun enableEntry(){
        submitButton.isEnabled = true
        submitButton.isClickable = true
        guessField.isEnabled = true
        guessField.isClickable = true
    }

    private fun disableEntry(){
        submitButton.isEnabled = false
        submitButton.isClickable = false
        guessField.isEnabled = false
        guessField.isClickable = false
    }

    private fun showAlert(title: String){
        // first we create a variable to hold an AlertDialog builder
        val dialogBuilder = AlertDialog.Builder(this)

        // here we set the message of our alert dialog
        dialogBuilder.setMessage(title)
            // positive button text and action
            .setPositiveButton("Yes", DialogInterface.OnClickListener {
                    dialog, id -> playGame()
            })
            // negative button text and action
            .setNegativeButton("No", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })
        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Game over")
        // show alert dialog
        alert.show()
    }
}