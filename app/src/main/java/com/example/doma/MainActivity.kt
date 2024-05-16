package com.example.doma

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.doma.MessageListActivity
import com.example.doma.R
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val SPEECH_REQUEST_CODE = 0
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        textToSpeech = TextToSpeech(this, this)

        val btnReadMessages: Button = findViewById(R.id.btnReadMessages)
        btnReadMessages.setOnClickListener {
            val intent = Intent(this, MessageListActivity::class.java)
            startActivity(intent)
        }

        val btnVoiceCommand: Button = findViewById(R.id.btnVoiceCommand)
        btnVoiceCommand.setOnClickListener {
            startVoiceRecognition()
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale 'ler notificações'")
        startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.getOrNull(0)

            if (spokenText.equals("ler notificações", ignoreCase = true)) {
                speakNotification()
            } else {
                Toast.makeText(this, "Comando não reconhecido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun speakNotification() {
        val notificationMessage = "Reunião com a diretoria às 15h, assunto: app para o Doma"
        textToSpeech.speak(notificationMessage, TextToSpeech.QUEUE_FLUSH, null, null)

        // Esconder o texto e icone de notificação pendente
        val notificationIcon = findViewById<ImageView>(R.id.notificationIcon)
        notificationIcon.visibility = View.GONE
        val txtPendingNotifications: TextView = findViewById(R.id.txtPendingNotifications)
        txtPendingNotifications.visibility = View.GONE

        // Mostrar o novo texto em verde
        val txtAfterReadingNotifications: TextView = findViewById(R.id.txtAfterReadingNotifications)
        txtAfterReadingNotifications.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.getDefault())
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Idioma não suportado", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Falha na inicialização do Text-to-Speech", Toast.LENGTH_SHORT).show()
        }
    }
}
