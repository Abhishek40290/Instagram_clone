package com.example.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.instagramclone.Models.User
import com.example.instagramclone.databinding.ActivitySignUpBinding
import com.example.instagramclone.utils.USER_NODE
import com.example.instagramclone.utils.USER_PROFILE_FOLDERS
import com.example.instagramclone.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    lateinit var user: User
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        uri ->
        uri?.let {
             uploadImage(uri, USER_PROFILE_FOLDERS) {
                if(it==null){

                }else {
                    user.image = it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        user = User()
        val text = "<font color=#FF000000>Already have an Account</font> <font color=#1E88E5>Login ?</font>"
        binding.login.text = Html.fromHtml(text)

        binding.signUp.setOnClickListener{
            if((binding.name.editText?.text.toString() == "") or
                (binding.email.editText?.text.toString() == "") or
                (binding.password.editText?.text.toString() == "")
                    ) {
                Toast.makeText(this, "Please fill the above fields", Toast.LENGTH_LONG).show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.email.editText?.text.toString(),
                    binding.password.editText?.text.toString()
                ).addOnCompleteListener {
                    result ->
                    if(result.isSuccessful) {
                        user.name = binding.name.editText?.text.toString()
                        user.email = binding.email.editText?.text.toString()
                        user.password = binding.password.editText?.text.toString()
                        Firebase.firestore.collection(USER_NODE)
                            .document(Firebase.auth.currentUser!!.uid).set(user)
                            .addOnSuccessListener {
                             startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            }

                        Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, result.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.profileImage.setOnClickListener{
            launcher.launch("image/*")
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}