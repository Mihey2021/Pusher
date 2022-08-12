package ru.netology.pusher

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.netology.pusher.databinding.ActivityMainBinding
import java.io.FileInputStream
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token =
            "dah78796RrGIicERAOgaKF:APA91bEX5jWsuSiX82GXpYp1z2F_4kMcELmaygxQtGxaHTtmsWcohUbFLyWKsRPrlokKVV_lAk4joPymYV2mXymuZmJnwybR_SQltOzv9nVXZzo07DN2EeOetqW4QgvUtJPIaGwKQZNX"

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(FileInputStream("/storage/emulated/0/Download/fcm.json")))
            .build()

        FirebaseApp.initializeApp(options)

        binding.btnSendMessage.setOnClickListener {

            val userName = binding.editTextUserName.text.toString()
            val postAuthor = binding.editTextPostAuthor.text.toString()
            val action = binding.editTextAction.text.toString()
            val postContent = binding.editTextPostContent.text.toString()

            if (userName.isBlank() || postAuthor.isBlank() || action.isBlank()) {
                Toast.makeText(this, getString(R.string.errEmptyField), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnSendMessage.isEnabled = false

            val message = Message.builder()
                .putData("action", action)
                .putData(
                    "content",
                    """{
                      "userId": 1,
                      "userName": "$userName",
                      "postId": 2,
                      "postAuthor": "$postAuthor",
                      "postContent": "$postContent"
                    }""".trimIndent()
                )
                .setToken(token)
                .build()

            val job = GlobalScope.launch { sendMessage(message) }
            while(true) {
                if (job.isCompleted) {
                    Toast.makeText(
                        this,
                        getString(R.string.messageSent),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnSendMessage.isEnabled = true
                    break
                }
            }
        }
    }

    private suspend fun sendMessage(message: Message) {
        coroutineScope {
            launch {
                FirebaseMessaging.getInstance().send(message)
             }
        }
    }
}

