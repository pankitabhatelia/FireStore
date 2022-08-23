package activities

import android.app.DatePickerDialog
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
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        binding.userdata = userViewModel
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val image = intent.getStringExtra("profile")
        Glide.with(this).load(image?.toUri()).circleCrop().into(binding.ivProfile)
        val name = intent.getStringExtra("name")
        userViewModel.firstName.value = name
        userViewModel.image.value = image

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        datePickerDialog = DatePickerDialog(this, dateSelectedListener, year, month, day)
        cal.set(year - 1, month, day)
        datePickerDialog.datePicker.maxDate = cal.timeInMillis
        binding.dob.setOnClickListener {
            datePickerDialog.show()
        }
        if(auth.currentUser!=null) {
            userViewModel.firstName.value=name
            userViewModel.lastName.value=binding.lastName.text.toString()
            userViewModel.dateOfBirth.value=binding.dob.text.toString()
            userViewModel.gender.value=binding.gender.text.toString()
            userViewModel.image.value=image
            userViewModel.phoneNumber.value=binding.phone.text.toString()
            userViewModel.updateUser()
            auth.signOut()
        }
        observer()
    }

    private val dateSelectedListener =
        DatePickerDialog.OnDateSetListener { _, myear, mmonth, mdayOfMonth ->
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