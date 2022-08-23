package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


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
    private val user: MutableMap<String, Any> = HashMap()
    private val _toastMessage: MutableLiveData<String> = MutableLiveData()
    val toastMessage: LiveData<String> = _toastMessage
    private lateinit var database : DatabaseReference

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
                _toastMessage.value = "User detail is updated"
            }
            .addOnFailureListener {
                _toastMessage.value = "User detail is not updated"
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

    fun updateUser(){
        database = FirebaseDatabase.getInstance().getReference("userdata")
        val user= mapOf<String,Any>(
            "firstName" to firstName,
            "lastName" to lastName,
            "dateofbirth" to dateOfBirth,
            "gender" to gender,
            "image" to image,
            "phone" to phoneNumber

        )
        database.child(firstName.value.toString()).updateChildren(user).addOnSuccessListener {
            _toastMessage.value="Successfuly Updated"

        }.addOnFailureListener{
            it.printStackTrace()
            _toastMessage.value="Failed to Update"
        }
    }


}