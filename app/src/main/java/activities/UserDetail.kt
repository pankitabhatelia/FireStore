package activities
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.firestore.FileUriUtils
import com.example.firestore.R
import com.example.firestore.databinding.ActivityUserDetailBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import viewmodel.UserViewModel
import java.io.File
import java.util.*

class UserDetail : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel
    private val cal = Calendar.getInstance()
    private lateinit var datePickerDialog: DatePickerDialog
    private val cameraRequestCode = 1
    private val galleryRequestCode = 2
    private lateinit var imageUri: Uri
    private var imagePath = ""
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        binding.userdata = userViewModel
        setContentView(binding.root)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        imageUri = createImageUri()!!
        val name=intent.getStringExtra("name")
        userViewModel.firstName.value=name
        val image=intent.getStringExtra("profile")
        userViewModel.image.value=image
        Glide.with(this).load(image).circleCrop().into(binding.ivProfile)

        binding.ivProfile.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf(
                "Capture photo from Camera",
                "Select photo from Gallery"
            )
            pictureDialog.setItems(pictureDialogItem) { _, which ->
                when (which) {
                    0 -> checkPermissions(Manifest.permission.CAMERA, cameraRequestCode)
                    1 -> checkPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        galleryRequestCode
                    )
                }
            }
            pictureDialog.show()
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

        observer()
    }

    private fun checkPermissions(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this, "Permission Granted Already", Toast.LENGTH_SHORT)
                .show()
            if (requestCode == cameraRequestCode) {
                camera()
            } else {
                gallery()
            }
        }
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getGalleryResult.launch(intent)
    }

    private fun camera() {
        getResult.launch(imageUri)
    }

    private val getResult = registerForActivityResult(
        object : ActivityResultContracts.TakePicture() {
            override fun createIntent(
                context: Context,
                input: Uri
            ): Intent {
                val intent = super.createIntent(context, input)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    intent.clipData = ClipData.newRawUri("", input)
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                return intent
            }
        }) { success ->
        if (success) {
            Glide.with(this)
                .load(imageUri)
                .circleCrop()
                .into(binding.ivProfile)
            imagePath = FileUriUtils.getRealPath(this, imageUri).orEmpty()
            userViewModel.setPhoto(imagePath)
        }
    }
    private val getGalleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                imagePath = FileUriUtils.getRealPath(this, it.data?.data!!).orEmpty()
                Glide.with(this)
                    .load(imagePath)
                    .circleCrop()
                    .into(binding.ivProfile)
                userViewModel.setPhoto(imagePath)
            }
        }

    private val dateSelectedListener =
        DatePickerDialog.OnDateSetListener { _, myear, mmonth, mdayOfMonth ->
            val date = "$mdayOfMonth/${mmonth + 1}/$myear"
            binding.dob.setText(date)
        }

    private fun observer() {
        userViewModel.toastMessage.observe(this) {
            if(it=="Data is inserted successfully"){
                val intent=Intent(this,ShowUserDetail::class.java)
                intent.putExtra("firstName",userViewModel.firstName.value.toString())
                intent.putExtra("lastName",userViewModel.lastName.value.toString())
                intent.putExtra("dateOfBirth",userViewModel.dateOfBirth.value.toString())
                intent.putExtra("gender",userViewModel.gender.value.toString())
                intent.putExtra("phone",userViewModel.phoneNumber.value.toString())
                intent.putExtra("profile",userViewModel.image.value.toString())
                startActivity(intent)
                finish()
            }
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun createImageUri(): Uri? {
        val image = File(this.filesDir, "camera_photo.png")
        return FileProvider.getUriForFile(
            this,
            "com.example.firestore.fileProvider",
            image
        )
    }


}