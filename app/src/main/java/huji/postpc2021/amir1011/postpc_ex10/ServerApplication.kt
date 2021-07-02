package huji.postpc2021.amir1011.postpc_ex10

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class ServerApplication: Application() {

    companion object {
        private var instance: ServerApplication? = null
        private var sp: SharedPreferences? = null
        fun getInstance(): ServerApplication? {
            return instance
        }
        fun getSp(): SharedPreferences? {
            return sp
        }
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        sp = getSharedPreferences("local_app_db", Context.MODE_PRIVATE)
    }

}