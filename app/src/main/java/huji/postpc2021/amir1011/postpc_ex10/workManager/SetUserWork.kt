package huji.postpc2021.amir1011.postpc_ex10.workManager

import android.content.Context
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.JsonObject
import huji.postpc2021.amir1011.postpc_ex10.appData.UserResponse
import huji.postpc2021.amir1011.postpc_ex10.serverLayer.ServerHolder
import huji.postpc2021.amir1011.postpc_ex10.serverLayer.ServerInterface
import retrofit2.Response
import java.io.IOException

class SetUserWork(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {
        override fun doWork(): ListenableWorker.Result {

            val curServerInterface: ServerInterface = ServerHolder.getServerInstance()
            val pretty = inputData.getString("pretty_name_key")
            val imageURL = inputData.getString("image_url_key")
            var token = inputData.getString("token_key")
            return try {
                token = "token $token"
                val jObject = JsonObject()
                if (pretty != null) {
                    jObject.addProperty("property_pretty_name", pretty)
                } else if (imageURL != null) {
                    jObject.addProperty("property_image_url", imageURL)
                }

                val response: Response<UserResponse> = curServerInterface.editUser(token, jObject).execute()
                val user = response.body() ?: return Result.failure()
                val outputData = Data.Builder()
                    .putString("user_output_key", Gson().toJson(user))
                    .build()
                return Result.success(outputData)
            } catch (e: IOException) {
                e.printStackTrace()
                Result.retry()
            }
        }
}