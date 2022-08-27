package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import model.UserData


class UserViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    var firstName: MutableLiveData<String?> = MutableLiveData()
    var lastName: MutableLiveData<String?> = MutableLiveData()
    var dateOfBirth: MutableLiveData<String?> = MutableLiveData()
    var gender: MutableLiveData<String?> = MutableLiveData()
    var phoneNumber: MutableLiveData<String?> = MutableLiveData()
    var image: MutableLiveData<String?> = MutableLiveData()
    val firstNameError: MutableLiveData<String?> = MutableLiveData()
    val lastNameError: MutableLiveData<String?> = MutableLiveData()
    val dobError: MutableLiveData<String?> = MutableLiveData()
    val genderError: MutableLiveData<String?> = MutableLiveData()
    val phoneError: MutableLiveData<String?> = MutableLiveData()
    private val user: MutableMap<String, Any> = HashMap()
    private val _toastMessage: MutableLiveData<String> = MutableLiveData()
    val toastMessage: LiveData<String> = _toastMessage

    fun onClick() {
        if (isValid()) {
            registerTofireStore()
        }
    }

    fun registerTofireStore() {
        /*val firstName2=firstName.value?.toString()
        val lastName2= lastName.value.toString()
        val dateOfBirth2=dateOfBirth.value.toString()
        val gender2=gender.value.toString()
        val image2=image.value.toString()
        val phoneNumber2=phoneNumber.value.toString()*/
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val userData = UserData(
            firstName.value?.toString(),
            lastName.value?.toString(),
            dateOfBirth.value?.toString(),
            gender.value?.toString(),
            image.value?.toString(),
            phoneNumber.value?.toString()?.toInt()
        )
        databaseReference.child(firebaseUser.uid).setValue(userData).addOnCompleteListener {
            _toastMessage.value = "Data is inserted successfully"
        }.addOnFailureListener {
            _toastMessage.value = "Data is Failed to insert!!"
        }

    }

    private fun isValid(): Boolean {
        firstNameError.postValue(null)
        lastNameError.postValue(null)
        dobError.postValue(null)
        genderError.postValue(null)
        phoneError.postValue(null)
        when {
            firstName.value.isNullOrEmpty() -> {
                firstNameError.postValue("Please Enter First Name")
            }
            lastName.value.isNullOrEmpty() -> {
                lastNameError.postValue("Please Enter Last Name")
            }
            dateOfBirth.value.isNullOrEmpty() -> {
                dobError.postValue("Please Enter Date of Birth")
            }
            gender.value.isNullOrEmpty() -> {
                genderError.postValue("Please Enter Gender")
            }
            phoneNumber.value.isNullOrBlank() -> {
                phoneError.postValue("Result can not be empty")
            }
            else -> return true
        }
        return false
    }

    fun setPhoto(selectedphoto: String) {
        image.value = selectedphoto
    }


}