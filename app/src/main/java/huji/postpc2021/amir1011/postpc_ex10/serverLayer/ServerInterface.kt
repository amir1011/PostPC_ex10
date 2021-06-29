package huji.postpc2021.amir1011.postpc_ex10.serverLayer

import com.google.gson.JsonObject
import huji.postpc2021.amir1011.postpc_ex10.appData.TokenResponse
import huji.postpc2021.amir1011.postpc_ex10.appData.UserResponse
import retrofit2.Call
import retrofit2.http.*


interface ServerInterface {

    @GET("/users")
    fun connectivityCheck(): Call<UserResponse>

    @GET("/user/")
    fun getUser(@Header("Authorization") token: String): Call<UserResponse>

    @GET("/users/{username}/token/")
    fun getUsersToken(@Path("username") username: String?): Call<TokenResponse>

    @Headers("Content-Type: application/json")
    @POST("/user/edit/")
    fun editUser(
        @Header("Authorization") token: String,
        @Body json: JsonObject
    ): Call<UserResponse>
}