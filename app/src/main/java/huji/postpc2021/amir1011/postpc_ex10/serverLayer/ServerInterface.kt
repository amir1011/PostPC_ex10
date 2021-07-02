package huji.postpc2021.amir1011.postpc_ex10.serverLayer

import com.google.gson.JsonObject
import huji.postpc2021.amir1011.postpc_ex10.appData.TokenResponse
import huji.postpc2021.amir1011.postpc_ex10.appData.UserResponse
import retrofit2.Call
import retrofit2.http.*


interface ServerInterface {

    @GET("/users/0")
    fun connectivityCheck(): Call<UserResponse>

    @GET("/user/")
    fun getUserInfo(@Header("Authorization") token: String): Call<UserResponse>

    @GET("/users/{username}/token/")
    fun getUsersToken(@Path("username") username: String): Call<TokenResponse>

    @Headers("Content-Type: application/json")
    @POST("/user/edit/")
    fun editUser(
            @Header("Authorization") token: String,
            @Body json: JsonObject
    ): Call<UserResponse>





//    @GET("users/{username}/token/")
//    fun login(@Path("username") userName: String): Call<TokenResponse>
//
//    @GET("user/")
//    fun getUserInfo(@Header("Authorization") auth: String): Call<UserResponse>
//
//    @Headers("Content-Type: application/json")
//    @POST("user/edit/")
//    fun editUserPrettyName(
//            @Header("Authorization") auth: String,
//            @Body request: SetUserPrettyNameRequest
//    ): Call<UserResponse>
//
//    @Headers("Content-Type: application/json")
//    @POST("user/edit/")
//    fun editUserImage(
//            @Header("Authorization") auth: String,
//            @Body request: SetUserImageRequest
//    ): Call<UserResponse>
}