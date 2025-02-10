package com.example.proyecto_friedman_lapo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var loginLayout: LinearLayout
    private lateinit var chatLayout: LinearLayout
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etMessage: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnSend: Button
    private lateinit var rvChat: RecyclerView
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter

    // Contexto matemático inicial para el chatbot
    private val mathContext = """Eres un asistente matemático experto. Tu objetivo es:
        1. Ayudar a resolver problemas matemáticos paso a paso
        2. Explicar conceptos matemáticos de manera clara
        3. Proporcionar ejemplos prácticos
        4. Guiar al estudiante en su aprendizaje
        Por favor, mantén tus respuestas enfocadas en matemáticas."""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize Firebase Auth
        auth = Firebase.auth

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        setupToolbar()
        checkCurrentUser()
    }

    private fun initializeViews() {
        loginLayout = findViewById(R.id.loginLayout)
        chatLayout = findViewById(R.id.chatLayout)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etMessage = findViewById(R.id.etMessage)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        btnSend = findViewById(R.id.btnSend)
        rvChat = findViewById(R.id.rvChat)
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(messages)
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = adapter
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener { loginUser() }
        btnRegister.setOnClickListener { registerUser() }
        btnSend.setOnClickListener { sendMessage() }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    auth.signOut()
                    showLoginLayout()
                    true
                }
                else -> false
            }
        }
    }

    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            showChatLayout()
        }
    }

    private fun showLoginLayout() {
        loginLayout.visibility = View.VISIBLE
        chatLayout.visibility = View.GONE
        messages.clear()
        adapter.notifyDataSetChanged()
        etEmail.text.clear()
        etPassword.text.clear()
    }

    private fun showChatLayout() {
        loginLayout.visibility = View.GONE
        chatLayout.visibility = View.VISIBLE
        
        // Agregar mensaje inicial del bot
        if (messages.isEmpty()) {
            messages.add(Message(
                "assistant",
                "¡Hola! Soy tu asistente matemático. Puedo ayudarte con:\n" +
                "• Álgebra y ecuaciones\n" +
                "• Geometría y trigonometría\n" +
                "• Cálculo diferencial e integral\n" +
                "• Estadística y probabilidad\n\n" +
                "¿En qué puedo ayudarte hoy?"
            ))
            adapter.notifyItemInserted(0)
            rvChat.scrollToPosition(0)
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "El correo es requerido"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Ingresa un correo válido"
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "La contraseña es requerida"
            return
        }

        if (password.length < 6) {
            etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        // Mostrar progreso
        btnLogin.isEnabled = false
        btnRegister.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showChatLayout()
                } else {
                    showError("Error al iniciar sesión: ${task.exception?.message}")
                }
                btnLogin.isEnabled = true
                btnRegister.isEnabled = true
            }
    }

    private fun registerUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "El correo es requerido"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Ingresa un correo válido"
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "La contraseña es requerida"
            return
        }

        if (password.length < 6) {
            etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        // Mostrar progreso
        btnLogin.isEnabled = false
        btnRegister.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showChatLayout()
                    showError("Usuario registrado exitosamente")
                } else {
                    showError("Error al registrar: ${task.exception?.message}")
                }
                btnLogin.isEnabled = true
                btnRegister.isEnabled = true
            }
    }

    private fun sendMessage() {
        val message = etMessage.text.toString().trim()
        if (message.isNotEmpty()) {
            // Agregar mensaje del usuario
            val userMessage = Message("user", message)
            messages.add(userMessage)
            adapter.notifyItemInserted(messages.size - 1)
            rvChat.scrollToPosition(messages.size - 1)
            etMessage.text.clear()

            // Mostrar indicador de "escribiendo..."
            val typingMessage = Message("assistant", "Escribiendo...")
            messages.add(typingMessage)
            adapter.notifyItemInserted(messages.size - 1)
            rvChat.scrollToPosition(messages.size - 1)

            lifecycleScope.launch {
                try {
                    // Incluir el contexto matemático en cada solicitud
                    val contextMessage = Message("system", mathContext)
                    val chatRequest = ChatRequest(messages = listOf(contextMessage, userMessage))
                    val response = RetrofitClient.instance.getChatResponse(chatRequest)
                    
                    // Remover el mensaje de "escribiendo..."
                    messages.removeAt(messages.size - 1)
                    adapter.notifyItemRemoved(messages.size)

                    if (response.isSuccessful) {
                        response.body()?.choices?.firstOrNull()?.message?.let { botMessage ->
                            messages.add(botMessage)
                            adapter.notifyItemInserted(messages.size - 1)
                            rvChat.scrollToPosition(messages.size - 1)
                        }
                    } else {
                        showError("Error: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    // Remover el mensaje de "escribiendo..." en caso de error
                    messages.removeAt(messages.size - 1)
                    adapter.notifyItemRemoved(messages.size)
                    showError("Error: ${e.message}")
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}