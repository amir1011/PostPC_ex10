package huji.postpc2021.amir1011.postpc_ex10

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.*
import com.bumptech.glide.Glide
import com.google.gson.Gson
import huji.postpc2021.amir1011.postpc_ex10.appData.TokenResponse
import huji.postpc2021.amir1011.postpc_ex10.appData.User
import huji.postpc2021.amir1011.postpc_ex10.appData.UserResponse
import huji.postpc2021.amir1011.postpc_ex10.workManager.SetUserWork
import huji.postpc2021.amir1011.postpc_ex10.workManager.UserTokenWork
import huji.postpc2021.amir1011.postpc_ex10.workManager.UserWorker
import java.util.*

class MainActivity : AppCompatActivity() {

    private val URL = "https://hujipostpc2019.pythonanywhere.com/"
    private val imagePath = "/images/%s.png"

    private var userImage: ImageView? = null
    private var welcomeText: TextView? = null
    private var changePretty: Button? = null
    private var curToken: String? = null
    private var connectButton: Button? = null
    private var username: EditText? = null
    private var prettyName: TextView? = null
    private val sp: SharedPreferences = getSharedPreferences(
            "local_app_db",
            Context.MODE_PRIVATE
    )

    private enum class Actions{
        GetUser,
        GetUserToken,
        UpdateUser,
        UpdateImage
    }

    private fun doOneTimeWorkRequest(typeOfAction: Actions, curData: String?)
    {
        val workCurrId = UUID.randomUUID().toString()
        val currWork = OneTimeWorkRequest.Builder(UserTokenWork::class.java)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(
                        when(typeOfAction)
                        {
                            Actions.GetUser ->
                                Data.Builder().putString("token_key", curToken).build()
                            Actions.GetUserToken ->
                                Data.Builder().putString("username_key", curData).build()
                            Actions.UpdateImage ->
                                Data.Builder().putString("token_key", curToken)
                                    .putString("image_url_key", curData).build()
                            else -> Data.Builder().putString("token_key", curToken)
                                    .putString("image_url_key", null)
                                    .putString("pretty_name_key", curData).build()
                        }
                        ).addTag(workCurrId).build()

        WorkManager.getInstance(applicationContext).enqueue(currWork)
        WorkManager.getInstance(applicationContext)
                .getWorkInfosByTagLiveData(workCurrId).observe(this,
                        { workInfoList: List<WorkInfo>? ->
                            if (workInfoList == null || workInfoList.isEmpty()) return@observe
                            val workInfo = workInfoList[0]

                            if(typeOfAction == Actions.UpdateImage
                                    || typeOfAction == Actions.UpdateUser)
                            {
                                if (workInfo.state == WorkInfo.State.FAILED) {
                                    Toast.makeText(applicationContext, "Failed updating user",
                                            Toast.LENGTH_SHORT).show()
                                    return@observe
                                }
                            }

                            if(typeOfAction == Actions.GetUserToken)
                            {
                                val curTokenJson = workInfo.outputData
                                        .getString("user_output_token_key")
                                if (curTokenJson == null || curTokenJson == "") return@observe

                                val curResponseToken = Gson()
                                        .fromJson(curTokenJson, TokenResponse::class.java)

                                if (curResponseToken.data == null || curResponseToken.data == "")
                                    return@observe

                                val editor = sp!!.edit()
                                editor.putString("token_key", curResponseToken.data)
                                editor.apply()
                                curToken = curResponseToken.data

                                username!!.visibility = View.GONE
                                connectButton!!.visibility = View.GONE
                                changeLoading(true)
                                doOneTimeWorkRequest(Actions.GetUser, null)
                            }

                            else
                            {
                                val curResponseJson = workInfo.outputData
                                        .getString("user_output_key")
                                if (curResponseJson == null || curResponseJson == "") return@observe
                                val curResponse = Gson()
                                        .fromJson(curResponseJson, UserResponse::class.java)

                                if (curResponse == null || curResponse.data == null) return@observe

                                changeLoading(false)
                                showUserInfo(curResponse.data)
                            }
                        })
    }

    private fun requestInternetPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.INTERNET),
                    100)
    }

    private fun changeLoading(toShow: Boolean) {
        when (toShow) {
            true -> findViewById<LinearLayout>(R.id.loading_progress).visibility = View.VISIBLE
            else -> findViewById<LinearLayout>(R.id.loading_progress).visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestInternetPermission()

        //initialize UI

        connectButton = findViewById(R.id.connect)
        username = findViewById(R.id.username)
        prettyName = findViewById(R.id.new_name)
        changePretty = findViewById(R.id.changeName)
        welcomeText = findViewById(R.id.userWelcome)
        userImage = findViewById(R.id.userImage)

        userImage!!.visibility = View.GONE
        changePretty!!.visibility = View.GONE
        welcomeText!!.visibility = View.GONE
        prettyName!!.visibility = View.GONE

        curToken = sp!!.getString("token_key", null)
        if (curToken != null) {
            username!!.visibility = View.GONE
            connectButton!!.visibility = View.GONE
            changeLoading(true)
            doOneTimeWorkRequest(Actions.GetUser, null)
        }

        connectButton!!.setOnClickListener {
            doOneTimeWorkRequest(Actions.GetUserToken, username!!.text.toString())
        }
        changePretty!!.setOnClickListener {
           doOneTimeWorkRequest(Actions.UpdateUser, prettyName!!.text.toString())
        }

    }

    private fun showImagesAccordingToCurUser(currID: Int) {
        (findViewById<View>(R.id.image_alien) as ImageView).setColorFilter(Color.argb(0, 0, 0, 0))
        (findViewById<View>(R.id.image_crab) as ImageView).setColorFilter(Color.argb(0, 0, 0, 0))
        (findViewById<View>(R.id.image_frog) as ImageView).setColorFilter(Color.argb(0, 0, 0, 0))
        (findViewById<View>(R.id.image_octopus) as ImageView).setColorFilter(Color.argb(0, 0, 0, 0))
        (findViewById<View>(R.id.image_robot) as ImageView).setColorFilter(Color.argb(0, 0, 0, 0))
        (findViewById<View>(R.id.image_unicorn) as ImageView).setColorFilter(Color.argb(0, 0, 0, 0))
        (findViewById<View>(currID) as ImageView).setColorFilter(Color.argb(75, 0, 0, 0))
    }

    private fun showUserInfo(user: User) {
        val curName: String? =
            if (user.getPrettyName() == null || user.getPrettyName().equals(""))
                user.getUsername()
            else user.getPrettyName()

        if (curName != null) welcomeText!!.text = "Welcome Back:\n$curName"
        else welcomeText!!.text = ""

        welcomeText!!.visibility = View.VISIBLE
        prettyName!!.visibility = View.VISIBLE
        changePretty!!.visibility = View.VISIBLE

        Glide.with(this).load(URL + user.getImage()).into(userImage!!)
        userImage!!.visibility = View.VISIBLE


        Glide.with(this).load(URL + imagePath.format("alien"))
                .into(findViewById<View>(R.id.image_alien) as ImageView)
        Glide.with(this).load(URL + imagePath.format("frog"))
                .into(findViewById<View>(R.id.image_frog) as ImageView)
        Glide.with(this).load(URL + imagePath.format("unicorn"))
                .into(findViewById<View>(R.id.image_unicorn) as ImageView)
        Glide.with(this).load(URL + imagePath.format("robot"))
                .into(findViewById<View>(R.id.image_robot) as ImageView)
        Glide.with(this).load(URL + imagePath.format("crab"))
                .into(findViewById<View>(R.id.image_crab) as ImageView)
        Glide.with(this).load(URL + imagePath.format("octopus"))
                .into(findViewById<View>(R.id.image_octopus) as ImageView)

        findViewById<View>(R.id.image_alien).setOnClickListener {
            showImagesAccordingToCurUser(R.id.image_alien)
            doOneTimeWorkRequest(Actions.UpdateImage, imagePath.format("alien"))
        }
        findViewById<View>(R.id.image_frog).setOnClickListener {
            showImagesAccordingToCurUser(R.id.image_frog)
            doOneTimeWorkRequest(Actions.UpdateImage, imagePath.format("frog"))
        }
        findViewById<View>(R.id.image_unicorn).setOnClickListener {
            showImagesAccordingToCurUser(R.id.image_unicorn)
            doOneTimeWorkRequest(Actions.UpdateImage, imagePath.format("unicorn"))
        }
        findViewById<View>(R.id.image_robot).setOnClickListener {
            showImagesAccordingToCurUser(R.id.image_robot)
            doOneTimeWorkRequest(Actions.UpdateImage, imagePath.format("robot"))
        }
        findViewById<View>(R.id.image_crab).setOnClickListener {
            showImagesAccordingToCurUser(R.id.image_crab)
            doOneTimeWorkRequest(Actions.UpdateImage, imagePath.format("crab"))
        }
        findViewById<View>(R.id.image_octopus).setOnClickListener {
            showImagesAccordingToCurUser(R.id.image_octopus)
            doOneTimeWorkRequest(Actions.UpdateImage, imagePath.format("octopus"))
        }
    }


//    private fun updateUser(prettyName: String) {
//        val workTagUniqueId = UUID.randomUUID()
//        val updateUserWork = OneTimeWorkRequest.Builder(SetUserWork::class.java)
//            .setConstraints(
//                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
//            )
//            .setInputData(
//                Data.Builder()
//                    .putString("key_token", curToken)
//                    .putString("key_image_url", null)
//                    .putString("key_pretty_name", prettyName).build()
//            )
//            .addTag(workTagUniqueId.toString())
//            .build()
//        WorkManager.getInstance(applicationContext).enqueue(updateUserWork)
//        WorkManager.getInstance(applicationContext)
//            .getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this,
//                { workInfos: List<WorkInfo>? ->
//                    // we know there will be only 1 work info in this list - the 1 work with that specific tag!
//                    // there might be some time until this worker is finished to work (in the mean team we will get an empty list
//                    // so check for that
//                    if (workInfos == null || workInfos.isEmpty()) return@observe
//                    val info = workInfos[0]
//                    if (info.state == WorkInfo.State.FAILED) {
//                        Toast.makeText(
//                            applicationContext,
//                            "Failed updating user",
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        return@observe
//                    }
//                    // now we can use it
//                    val userResponseAsJson =
//                        info.outputData.getString("key_output_user")
//                    if (userResponseAsJson == null || userResponseAsJson == "") {
//                        return@observe
//                    }
//                    Log.d("ex7Tag", "got user: $userResponseAsJson")
//                    val userResponse: UserResponse =
//                        Gson().fromJson(userResponseAsJson, UserResponse::class.java)
//                    if (userResponse == null || userResponse.data == null) {
//                        return@observe
//                    }
//                    val user: User = userResponse.data
//                    Log.d("ex7Tag", "got user: $user")
//                    changeLoading(false)
//                    showUserInfo(user)
//                })
//    }
//
//    private fun updateImageUrl(url: String) {
//        changeLoading(true)
//        val workTagUniqueId = UUID.randomUUID()
//        val setUserImageUrl = OneTimeWorkRequest.Builder(SetUserWork::class.java)
//            .setConstraints(
//                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
//            )
//            .setInputData(
//                Data.Builder().putString("key_token", curToken)
//                    .putString("key_image_url", url)
//                    .build()
//            )
//            .addTag(workTagUniqueId.toString())
//            .build()
//        WorkManager.getInstance().enqueue(setUserImageUrl)
//        WorkManager.getInstance().getWorkInfosByTagLiveData(workTagUniqueId.toString())
//            .observe(this,
//                { workInfos: List<WorkInfo>? ->
//                    if (workInfos == null || workInfos.isEmpty()) return@observe
//                    val info = workInfos[0]
//                    if (info.state == WorkInfo.State.FAILED) {
//                        Toast.makeText(
//                            applicationContext,
//                            "Failed updating user",
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                        return@observe
//                    }
//                    // now we can use it
//                    val userResponseAsJson =
//                        info.outputData.getString("key_output_user")
//                    if (userResponseAsJson == null || userResponseAsJson == "") {
//                        return@observe
//                    }
//                    Log.d("ex7Tag", "got user: $userResponseAsJson")
//                    val userResponse: UserResponse =
//                        Gson().fromJson(userResponseAsJson, UserResponse::class.java)
//                    if (userResponse == null || userResponse.data == null) {
//                        return@observe
//                    }
//                    val user: User = userResponse.data
//                    Log.d("ex7Tag", "got user: $user")
//                    changeLoading(false)
//                    showUserInfo(user)
//                })
//    }

//    private fun getUser() {
//        val workTagUniqueId = UUID.randomUUID()
//        val getUserWork = OneTimeWorkRequest.Builder(UserWorker::class.java)
//            .setConstraints(
//                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
//            )
//            .setInputData(Data.Builder().putString("key_token", curToken).build())
//            .addTag(workTagUniqueId.toString())
//            .build()
//        WorkManager.getInstance(applicationContext).enqueue(getUserWork)
//        WorkManager.getInstance(applicationContext)
//            .getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this,
//                { workInfos: List<WorkInfo>? ->
//                    if (workInfos == null || workInfos.isEmpty()) return@observe
//                    val info = workInfos[0]
//                    // now we can use it
//                    val userResponseAsJson =
//                        info.outputData.getString("key_output_user")
//                    if (userResponseAsJson == null || userResponseAsJson == "") {
//                        return@observe
//                    }
//                    val userResponse: UserResponse =
//                        Gson().fromJson(userResponseAsJson, UserResponse::class.java)
//                    if (userResponse == null || userResponse.data == null) {
//                        return@observe
//                    }
//                    val user: User = userResponse.data
//                    changeLoading(false)
//                    showUserInfo(user)
//                })
//      }
//
//
//
//    private fun getUserToken(curUserName: String) {
//        val workTagUniqueId = UUID.randomUUID()
//        val checkConnectivityWork = OneTimeWorkRequest.Builder(UserTokenWork::class.java)
//            .setConstraints(
//                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
//            )
//            .setInputData(Data.Builder().putString("username_key", curUserName).build())
//            .addTag(workTagUniqueId.toString())
//            .build()
//        WorkManager.getInstance(applicationContext).enqueue(checkConnectivityWork)
//        WorkManager.getInstance(applicationContext)
//            .getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe(this,
//                { workInfos: List<WorkInfo>? ->
//                    if (workInfos == null || workInfos.isEmpty()) return@observe
//                    val info = workInfos[0]
//                    val tokenAsJson =
//                        info.outputData.getString("key_output_user_token")
//                    if (tokenAsJson == null || tokenAsJson == "") {
//                        return@observe
//                    }
//                    val token: TokenResponse =
//                        Gson().fromJson(tokenAsJson, TokenResponse::class.java)
//                    if (token.data == null || token.data == "") {
//                        return@observe
//                    }
//
//                    val editor = sp!!.edit()
//                    editor.putString("token_key", token.data)
//                    editor.apply()
//                    curToken = token.data
//
//                    username!!.visibility = View.GONE
//                    connectButton!!.visibility = View.GONE
//                    changeLoading(true)
//                    getUser()
//                })
//    }

}