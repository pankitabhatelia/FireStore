package com.example.firestore
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firestore.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        auth.currentUser.also {
            if (it != null) {
                user = it
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.btnCreate.setOnClickListener {
            signIn()
        }
        binding.btnSignIn.setOnClickListener {
            signInGoogle()
        }

    }
    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = auth?.currentUser
        user?.let {
            startActivity(Intent(this, UserDetail::class.java))
            Toast.makeText(this, "welcome back", Toast.LENGTH_SHORT).show()
        }
    }
    private fun signIn() {
        val userEmail = binding.email.text.toString().trim()
        val userPassword = binding.password.text.toString().trim()

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
        .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "created account successfully !", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, UserDetail::class.java)
                    intent.putExtra("email", auth.currentUser?.email)
                    intent.putExtra("name", auth.currentUser?.displayName)
                    intent.putExtra("profile",auth.currentUser?.photoUrl)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Account is already exist... !", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, UserDetail::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                intent.putExtra("profile",account.photoUrl.toString())
                intent.putExtra("givenName",account.givenName)
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}