package com.example.book.navigation.model

data class ContentDTO(
	var diary: String? = null,
	var imageUrl : String? = null,
	var uid: String? = null,
	var userId: String? = null,
	var timestamp: Long? = null,
	var favoriteCount: Int = 0,
	var favorites: MutableMap<String, Boolean> = HashMap(),
	var music : String ? = null
) {
	data class Comment(
		var uid: String? = null,
		var userId: String? = null,
		var comment: String? = null,
		var timestamp: Long? = null
	)
}