package com.example.guessthephrase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.guessthephrase.databinding.ActivityAddPhraseBinding
import kotlinx.android.synthetic.main.activity_add_phrase.*

class AddPhraseActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddPhraseBinding
    private val databaseHelper by lazy { DatabaseHelper(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhraseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            val pharse = etPhrase.text.toString()
            etPhrase.text.clear()

            if (pharse.isNotEmpty()) {
                if (databaseHelper.saveData(pharse).toInt() != -1) {
                    Toast.makeText(this, "Added successfully!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}