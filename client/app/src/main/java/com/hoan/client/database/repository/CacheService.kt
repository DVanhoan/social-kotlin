package com.hoan.client.database.repository

import android.util.Log
import android.widget.ImageView
import com.hoan.client.R
import com.hoan.client.database.ImageCacheDatabase
import com.hoan.client.network.response.UserResponse
import com.hoan.client.network.RetrofitInstance
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CacheService {

    // Sử dụng object nên không cần getInstance() nữa.
    private val picasso: Picasso by lazy { Picasso.get() }
    private val cache: HashMap<String, String> = HashMap()

    private fun put(key: String, value: String) {
        cache[key] = value
    }

    private fun get(key: String): String? = cache[key]

    private fun update(key: String, value: String) {
        cache[key] = value
    }

    fun clear() {
        cache.clear()
    }

    fun resetKey(key: String) {
        cache.remove(key)
    }

    fun cacheProfilePicture(user: UserResponse, target: ImageView) {
        val tag = user.profilePicture ?: return
        val uri = get(tag)
        if (uri == null) {
            Log.d("CACHE_PROFILE_PICTURE", "Cache missed, downloading image with tag: $tag")
            loadProfilePictureIntoView(user, target)
        } else {
            Log.d("CACHE_PROFILE_PICTURE", "Cache hit, loading image with tag: $tag")
            // Nếu muốn chuyển đổi từ chuỗi sang Bitmap, có thể dùng ImageBitmapString
            // val bitmap = ImageBitmapString.StringToBitMap(uri)
            // target.setImageBitmap(bitmap)
            picasso.load(uri).placeholder(R.color.primaryAccent).into(target)
        }
    }

    private fun cache(tag: String, target: ImageView, fn: (String, ImageView) -> Unit) {
        val uri = get(tag)
        if (uri == null) {
            Log.d("CACHE_PROFILE_PICTURE_F", "Cache missed, downloading image with tag: $tag")
            fn(tag, target)
        } else {
            Log.d("CACHE_PROFILE_PICTURE_F", "Cache hit, loading image with tag: $tag")
            picasso.load(uri).placeholder(R.color.primaryAccent).into(target)
        }
    }

    fun cacheProfilePicture(userId: Long, target: ImageView) {
        val tag = userId.toString()
        cache(tag, target, this::loadProfilePictureIntoView)
    }

    fun cachePostImage(filename: String, target: ImageView) {
        cache(filename, target, this::loadPostImageIntoView)
    }

    fun cacheReactionImage(filename: String, target: ImageView) {
        cache(filename, target, this::loadReactionImageIntoView)
    }

    private fun loadProfilePictureIntoView(user: UserResponse, view: ImageView) {
        if (user.profilePicture == null) return
        RetrofitInstance.userService.getProfilePictureUrl(user.id)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        getImageUrlSuccess(response.code(), response.body()!!, user.profilePicture!!, view)
                    } else {
                        generalError(response.code(), Exception("Error loading profile picture: ${response.message()}"))
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun loadProfilePictureIntoView(id: String, view: ImageView) {
        val userId = id.toLongOrNull() ?: return
        RetrofitInstance.userService.getProfilePictureUrl(userId)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        getImageUrlSuccess(response.code(), response.body()!!, id, view)
                    } else {
                        generalError(response.code(), Exception("Error loading profile picture: ${response.message()}"))
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun loadPostImageIntoView(filename: String, view: ImageView) {
        RetrofitInstance.postService.getImageUrl(filename)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        getImageUrlSuccess(response.code(), response.body()!!, filename, view)
                    } else {
                        generalError(response.code(), Exception("Error loading post image: ${response.message()}"))
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun loadReactionImageIntoView(filename: String, view: ImageView) {
        RetrofitInstance.reactionService.getReactionImageUrl(filename)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        getImageUrlSuccess(response.code(), response.body()!!, filename, view)
                    } else {
                        generalError(response.code(), Exception("Error loading reaction image: ${response.message()}"))
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    generalError(500, t)
                }
            })
    }

    private fun getImageUrlSuccess(
        statusCode: Int,
        responseBody: ResponseBody,
        tag: String,
        view: ImageView
    ) {
        Log.d("GET_IMAGE_URL", "Successfully got image url: $responseBody Status code: $statusCode")
        val uri = responseBody.string()
        Log.d("GET_IMAGE_URL", "URI: $uri")
        put(tag, uri)
        picasso.load(uri).placeholder(R.color.primaryAccent).into(view)
    }

    private fun generalError(statusCode: Int, e: Throwable) {
        Log.e("API_ERROR", "Error $statusCode during API call")
        e.printStackTrace()
    }

    // Nếu cần khởi tạo cơ sở dữ liệu cache, ta có thể gọi hàm này (hiện đang để comment)
    fun initDatabase(db: ImageCacheDatabase) {
        // CacheService.db = db.imageCacheDao()
    }
}
