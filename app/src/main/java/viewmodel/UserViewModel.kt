package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import model.UserData


class UserViewModel : ViewModel() {
    private lateinit var databaseReference: DatabaseReference
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
            if(isValid()) {
                registerTofireStore()
            }
    }

      fun registerTofireStore() {
          val firstName2=firstName.value.toString()
          val lastName2= lastName.value.toString()
              val dateOfBirth2=dateOfBirth.value.toString()
          val gender2=gender.value.toString()
          val image2=image.value.toString()
          val phoneNumber2=phoneNumber.value.toString()
          databaseReference=FirebaseDatabase.getInstance().getReference("Users")
          val userId=databaseReference.push().key!!
          val userData=UserData(firstName2,lastName2,dateOfBirth2,gender2,image2,phoneNumber2.toInt())
          databaseReference.child(userId).setValue(userData).addOnCompleteListener {
              _toastMessage.value="Data is inserted successfully"
          }.addOnFailureListener {
              _toastMessage.value="Data is Failed to insert!!"
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