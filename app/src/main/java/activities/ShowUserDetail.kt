package activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.firestore.R
import com.example.firestore.databinding.ActivityShowUserDetailBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class ShowUserDetail : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseUSer: FirebaseUser
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
        getData()
        binding.btnLogOut.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }

        }
    }
    private fun getData(){
        auth= FirebaseAuth.getInstance()
        firebaseUSer= auth.currentUser!!
        databaseReference=FirebaseDatabase.getInstance().getReference("Users")
        Log.d("TAG",firebaseUSer.uid)
        val snapshot=databaseReference.child(firebaseUSer.uid).get()
        snapshot.addOnSuccessListener {
            binding.tvFirstName.text=it.child("firstName").value.toString()
            binding.tvLastName.text=it.child("lastName").value.toString()
            binding.tvdateOfBirth.text=it.child("dateOfBirth").value.toString()
            binding.tvgender.text=it.child("gender").value.toString()
            binding.tvPhone.text=it.child("number").value.toString()
            Glide.with(this).load(it.child("image").value.toString()).circleCrop().into( binding.imageview)
        }
        /*addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvFirstName.text = snapshot.child("firstName").value.toString()
                Log.d("TAG",snapshot.child("firstName").value.toString())
                binding.tvLastName.text = snapshot.child("lastName").value.toString()
                binding.tvdateOfBirth.text = snapshot.child("dateOfBirth").value.toString()
                binding.tvgender.text = snapshot.child("gender").value.toString()
                binding.tvPhone.text = snapshot.child("number").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ShowUserDetail, "Database Error", Toast.LENGTH_SHORT).show()
            }

        })*/

    }
}