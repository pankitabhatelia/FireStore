package activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.firestore.R
import com.example.firestore.databinding.ActivityShowUserDetailBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ShowUserDetail : AppCompatActivity() {
    private lateinit var binding: ActivityShowUserDetailBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        binding.tvFirstName.text = intent.getStringExtra("firstName")
        binding.tvLastName.text = intent.getStringExtra("lastName")
        binding.tvdateOfBirth.text = intent.getStringExtra("dateOfBirth")
        binding.tvgender.text = intent.getStringExtra("gender")
        binding.tvPhone.text = intent.getStringExtra("phone")
        Glide.with(this).load(intent.getStringExtra("profile")?.toUri()).circleCrop()
            .into(binding.imageview)
        binding.btnLogOut.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }
        }
    }
}