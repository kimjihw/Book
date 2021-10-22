package com.example.book.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.book.R
import com.example.book.databinding.ActivityAddDiaryBinding

import com.example.book.navigation.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_diary.*
import java.text.SimpleDateFormat
import java.util.*

class AddDiaryActivity : AppCompatActivity() {
	val binding by lazy{ActivityAddDiaryBinding.inflate(layoutInflater)}
	var PICK_IMAGE_FROM_ALBUM = 0
	var storage : FirebaseStorage? = null
	var photoUri : Uri? = null
	var auth : FirebaseAuth? = null
	var firestore : FirebaseFirestore? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)

		storage = FirebaseStorage.getInstance()
		auth = FirebaseAuth.getInstance()
		firestore = FirebaseFirestore.getInstance()

		var photoPickerIntent = Intent(Intent.ACTION_PICK)
		photoPickerIntent.type = "image/*"
		startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

		binding.adddiaryBtnUpload.setOnClickListener {
			contentUpload()
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if(requestCode == PICK_IMAGE_FROM_ALBUM){
			if(resultCode == Activity.RESULT_OK){
				// 사진을 선택했을 때 이미지 경로가 넘어옴
				photoUri = data?.data
				addphoto_image.setImageURI(photoUri)
			}else{
				// 취소 버튼
				finish()
			}
		}
	}
	fun contentUpload(){
		// 파일 명 만들기

		var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
		var imageFileName = "IMAGE_" + timestamp + "_.png"

		var storageRef = storage?.reference?.child("diary")?.child(imageFileName)

		storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
			return@continueWithTask storageRef.downloadUrl
		}?.addOnSuccessListener { uri->
			var contentDTO = ContentDTO()

				contentDTO.imageUrl = uri.toString()

				contentDTO.uid = auth?.currentUser?.uid

				contentDTO.userId = auth?.currentUser?.email

				contentDTO.diary = addphoto_edit_explain.text.toString()

				contentDTO.music = music_recommand.text.toString()

				contentDTO.timestamp = System.currentTimeMillis()

				firestore?.collection("diary")?.document()?.set(contentDTO)

				setResult(Activity.RESULT_OK)

				finish()
		}


//		storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
//			storageRef.downloadUrl.addOnSuccessListener { uri ->
//				var contentDTO = ContentDTO()
//
//				contentDTO.imageUrl = uri.toString()
//
//				contentDTO.uid = auth?.currentUser?.uid
//
//				contentDTO.userId = auth?.currentUser?.email
//
//				contentDTO.diary = addphoto_edit_explain.text.toString()
//
//				contentDTO.timestamp = System.currentTimeMillis()
//
//				firestore?.collection("diary")?.document()?.set(contentDTO)
//
//				setResult(Activity.RESULT_OK)
//
//				finish()
//			}
//		}
	}
}