package com.example.book.navigation.util

import com.example.book.navigation.model.PushDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class FcmPush {
	var JSON = MediaType.parse("application/json; charset=utf-8")
	var url = "https://fcm.googleapis.com/fcm/send"
	var serverKey = "AAAAmmrMx88:APA91bHxPV5KWo6T-3SYQr3-JRjhN0SietnbSW7xl2Mea4yxUXF8UqMjyIeKuibLN_3tYSWgR10AAEGM15aSjhCGN4rFnopBd-KT_uG3FjdzktkDrfQaswpgAS32K2jnt9exzktGhHKJ"
	var gson : Gson ?= null
	var okHttpClient : OkHttpClient? = null

	companion object{
		var instance = FcmPush()
	}

	init {
		gson = Gson()
		okHttpClient = OkHttpClient()
	}

	fun sendMessage(destinationUid : String, title : String, message : String){
		FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener {
			task ->
			if(task.isSuccessful){
				var token = task.result?.get("pushToken").toString()
				var pushDTO = PushDTO()
				pushDTO.to = token
				pushDTO.notification.title = title
				pushDTO.notification.body = message

				var body = RequestBody.create(JSON, gson?.toJson(pushDTO)!!)
				var request = Request.Builder()
					.addHeader("Content-Type", "application/json")
					.addHeader("Authorization", "key="+serverKey)
					.url(url)
					.post(body)
					.build()

				okHttpClient?.newCall(request)?.enqueue(object : Callback{
					override fun onFailure(call: Call, e: IOException) {

					}

					override fun onResponse(call: Call, response: Response) {
						println(response.body().string())
					}
				})
			}
		}
	}
}