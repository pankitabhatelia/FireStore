package activities

import android.app.Application
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth

class ActiveUser:Application() {
    override fun onCreate() {
        super.onCreate()

        val firebaseAuth=FirebaseAuth.getInstance()
        val firebaseUser=firebaseAuth.currentUser

        if(firebaseUser!=null){
            val intent=Intent(this@ActiveUser,ShowUserDetail::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }
    }
}