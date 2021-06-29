package huji.postpc2021.amir1011.postpc_ex10.workManager

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import huji.postpc2021.amir1011.postpc_ex10.appData.User
import huji.postpc2021.amir1011.postpc_ex10.appData.UserResponse
import huji.postpc2021.amir1011.postpc_ex10.serverLayer.ServerHolder
import huji.postpc2021.amir1011.postpc_ex10.serverLayer.ServerInterface
import retrofit2.Response
import java.io.IOException

class UserWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {
    override fun doWork(): Result {

        val curServerInterface: ServerInterface = ServerHolder.getServerInstance()
        val userToken = "token " + inputData.getString("token")
        return try {
            val response: Response<UserResponse> = curServerInterface.getUser(userToken).execute()
            val user: UserResponse = response.body() ?: return Result.failure()
            val outputData = Data.Builder()
                .putString("user_output_key", Gson().toJson(user))
                .build()
            Result.success(outputData)
        } catch (e: IOException) {
            e.printStackTrace()
            Result.retry()
        }
    }

}