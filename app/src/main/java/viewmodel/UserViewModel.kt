package viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import model.UserData


class UserViewModel : ViewModel() {
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
    val user: MutableMap<String, Any> = HashMap()
    private val _toastMessage: MutableLiveData<String> = MutableLiveData()
    val toastMessage: LiveData<String> = _toastMessage

    fun onClick() {
            if(isValid()) {
                    registerTofireStore(
                            firstName.value.toString(),
                            lastName.value.toString(),
                            dateOfBirth.value.toString(),
                            gender.value.toString(),
                            image.value.toString(),
                            phoneNumber.value!!.toInt()
                    )
            }
    }

    private fun registerTofireStore(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        gender: String,
        image: String,
        phoneNumber: Int
    ) {
        val db = FirebaseFirestore.getInstance()
        user["firstName"] = firstName
        user["lastName"] = lastName
        user["dateofbirth"] = dateOfBirth
        user["gender"] = gender
        user["image"] = image
        user["phone"] = phoneNumber
        db.collection("userdata")
            .add(user)
            .addOnSuccessListener {
                _toastMessage.value = "User is registered"
            }
            .addOnFailureListener {
                _toastMessage.value = "User is not registered"
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
     fun updateUser(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        gender: String,
        image: String,
        phoneNumber: Int
    ) {
        val db = FirebaseFirestore.getInstance()
        user["firstName"] = firstName
        user["lastName"] = lastName
        user["dateofbirth"] = dateOfBirth
        user["gender"] = gender
        user["image"] = image
        user["phone"] = phoneNumber
        db.collection("userdata")
            .document("36GkLaGUYUSWtyKFk0eZ").update(user)
            .addOnSuccessListener {
                _toastMessage.value = "User data is updated"
            }
            .addOnFailureListener {
                _toastMessage.value = "User data is not updated"
            }
    }

}