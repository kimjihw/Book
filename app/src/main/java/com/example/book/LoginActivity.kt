package com.example.book

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.example.book.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.Arrays.asList

class LoginActivity : AppCompatActivity() {
	val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
	var auth: FirebaseAuth? = null
	var googleSignInClient: GoogleSignInClient? = null
	var GOOGLE_LOGIN_CODE = 9001
	var callbackManager: CallbackManager? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		auth = FirebaseAuth.getInstance()

		binding.emailLoginbutton.setOnClickListener {
			signinAndSignup()
		}
		binding.facebookLoginButton.setOnClickListener {
			facebookLogin()
		}

		binding.googleSignInButton.setOnClickListener {
			googleLogin()
		}

		var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken("663216768975-nalcubfojhfd5lqn0db9jq29cg8h3rtu.apps.googleusercontent.com")
			.requestEmail()
			.build()
		googleSignInClient = GoogleSignIn.getClient(this, gso)
		callbackManager = CallbackManager.Factory.create()
	}

	fun signinAndSignup() {
		auth?.createUserWithEmailAndPassword(
			binding.emailEdittext.text.toString(),
			binding.passwordEdittext.text.toString()
		)
			?.addOnCompleteListener { task ->
				if (task.isSuccessful) {
					// 아이디 생성
					moveMainPage(task.result?.user)
				} else if (task.exception?.message.isNullOrEmpty()) {
					Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
				} else {
					signinEmail()
				}
			}
	}


	fun signinEmail() {
		auth?.signInWithEmailAndPassword(
			binding.emailEdittext.text.toString(),
			binding.passwordEdittext.text.toString()
		)?.addOnCompleteListener { task ->
			if (task.isSuccessful) {
				//아이디와 패스워드가 맞을 경우
				moveMainPage(task.result?.user)
			} else {
				Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
			}
		}
	}

	fun moveMainPage(user: FirebaseUser?) {
		if (user != null) {
			startActivity(Intent(this, MainActivity::class.java))
			finish()
		}
	}

//	fun printHashKey() {
//		try {
//			val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//			for (signature in info.signatures) {
//				val md: MessageDigest = MessageDigest.getInstance("SHA")
//				md.update(signature.toByteArray())
//				val hashKey: String = String(Base64.encode(md.digest(), 0))
//				Log.i("TAG", "printHashKey() Hash Key: $hashKey")
//			}
//		} catch (e: NoSuchAlgorithmException) {
//			Log.e("TAG", "printHashKey()", e)
//		} catch (e: Exception) {
//			Log.e("TAG", "printHashKey()", e)
//		}
//	}

	fun facebookLogin() {
		LoginManager.getInstance()
			.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))

		LoginManager.getInstance()
			.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
				override fun onSuccess(result: LoginResult?) {
					handleFacebookAccessToken(result?.accessToken)
				}

				override fun onCancel() {
				}

				override fun onError(error: FacebookException?) {

				}
			})
	}

	fun handleFacebookAccessToken(token: AccessToken?) {
		var credential = FacebookAuthProvider.getCredential(token?.token!!)
		auth?.signInWithCredential(credential)
			?.addOnCompleteListener { task ->
				if (task.isSuccessful) {
					//아이디와 패스워드가 맞을 경우
					moveMainPage(task.result?.user)
				} else {
					Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
				}
			}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		callbackManager?.onActivityResult(requestCode, resultCode, data)
		if (requestCode == GOOGLE_LOGIN_CODE) {
			var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
			if (result.isSuccess) {
				var account = result.signInAccount
				firebaseAuthWithGoogle(account)
			}
		}
	}

	fun googleLogin() {
		var signInIntent = googleSignInClient?.signInIntent
		startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
	}

	fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
		var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
		auth?.signInWithCredential(credential)
			?.addOnCompleteListener { task ->
				if (task.isSuccessful) {
					//아이디와 패스워드가 맞을 경우
					moveMainPage(task.result?.user)
				} else {
					Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
				}
			}
	}

	override fun onStart() {
		super.onStart()
		moveMainPage(auth?.currentUser)
	}
}