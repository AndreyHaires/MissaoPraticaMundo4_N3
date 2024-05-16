package com.example.doma

import AudioHelper
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MessageListActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var listView: ListView
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var audioHelper: AudioHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)
        audioHelper = AudioHelper(applicationContext)


        val btnHome: Button = findViewById(R.id.btnHome)
        btnHome.setOnClickListener {
            // Voltar para a MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        listView = findViewById(R.id.listView)
        textToSpeech = TextToSpeech(this, this)

        val messages = arrayOf(
            "Hoje: Tempo ensolarado com temperaturas entre 25°C e 30°C.",
            "Lembrete: Reunião de equipe às 14h no escritório.",
            "Consulta Médica: Dr. Silva às 10h na Clínica Central."

        )

        val adapter = ArrayAdapter(this, R.layout.list_item_message, messages)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val message = parent.getItemAtPosition(position) as String
            speakMessage(message)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale("pt", "BR"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Linguagem não suportada
            }
        } else {
            // Falha de inicialização
        }
    }

    private fun speakMessage(message: String) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        textToSpeech.shutdown()
        super.onDestroy()
    }
}
