package activities
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.firestore.databinding.ActivityUserDetailBinding
import com.google.firebase.auth.FirebaseAuth
import viewmodel.UserViewModel
import java.util.*

class UserDetail : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel
    private val cal = Calendar.getInstance()
    private lateinit var datePickerDialog: DatePickerDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding.userdata = userViewModel
        setContentView(binding.root)
        var firstName:String=binding.firstName.text.toString()
        var lastName:String=binding.lastName.text.toString()
        var dateOfBirth:String=binding.dob.text.toString()
        var gender:String=binding.gender.text.toString()
        var number:Int=0

        auth = FirebaseAuth.getInstance()

        Glide.with(this).load(intent.getStringExtra("profile")?.toUri()).circleCrop().into(binding.ivProfile)
        val name = intent.getStringExtra("name")
        userViewModel.firstName.value=name
        binding.btnSignOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        datePickerDialog = DatePickerDialog(this, dateSelectedListener, year, month, day)
        cal.set(year - 1, month, day)
        datePickerDialog.datePicker.maxDate = cal.timeInMillis
        binding.dob.setOnClickListener {
            datePickerDialog.show()
        }
        if(auth.currentUser!=null){
            userViewModel.updateUser(firstName,lastName,dateOfBirth,gender,
                intent.getStringExtra("profile")!!,number)
        }
        observer()
    }
    private val dateSelectedListener = DatePickerDialog.OnDateSetListener { _, myear, mmonth, mdayOfMonth ->
        //Just set age and date
        val date = "$mdayOfMonth/${mmonth + 1}/$myear"
        binding.dob.setText(date)
    }
    private fun observer() {
        userViewModel.toastMessage.observe(this) {
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}