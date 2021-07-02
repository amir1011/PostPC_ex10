package huji.postpc2021.amir1011.postpc_ex10.serverLayer

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServerHolder: Application() {


    companion object{
        private val okHttpClient: OkHttpClient by lazy {
            val okHttpClientBuilder = OkHttpClient.Builder()
            val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(interceptor)
            return@lazy okHttpClientBuilder.build()
        }

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://hujipostpc2019.pythonanywhere.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        private val serverInterface by lazy {
            retrofit.create(ServerInterface::class.java)
        }

        fun getServerInstance(): ServerInterface
        {
            return serverInterface
        }
    }

    fun getSP(): SharedPreferences? {
        return getSharedPreferences("local_app_db", Context.MODE_PRIVATE)
    }

}