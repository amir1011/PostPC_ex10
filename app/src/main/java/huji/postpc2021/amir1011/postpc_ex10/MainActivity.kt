package huji.postpc2021.amir1011.postpc_ex10

import android.Manifest
import android.app.ActivityManager.isRunningInTestHarness
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
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
    private var sp: SharedPreferences? = null

    private enum class Actions{
        GetUser,
        GetUserToken,
        UpdateUser,
        UpdateImage
    }

//    private fun isTestMode(): Boolean {
//        return try {
////            "C:\\Users\\HP\\AndroidStudioProjects\\PostPC_ex10\\app\\src\\androidTest\\java\\huji\\postpc2021\\amir1011\\postpc_ex10"
//                application.classLoader.loadClass("src.androidTest.java.huji.postpc2021.amir1011.postpc_ex10.AndroidAppFlowTests")
//                // alternatively (see the comment below):
//                // Class.forName("foo.bar.test.SomeTest");
//                true
//            } catch (e: Exception) {
//                false
//            }
//    }

    private fun doOneTimeWorkRequest(typeOfAction: Actions, curData: String?)
    {
        val workCurrId = UUID.randomUUID().toString()
        val currWork = OneTimeWorkRequest.Builder(
                when (typeOfAction) {
                    Actions.GetUser -> UserWorker::class.java
                    Actions.GetUserToken -> UserTokenWork::class.java
                    else -> SetUserWork::class.java
                }
        )
                .setConstraints(
                        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setInputData(
                        when (typeOfAction) {
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

                            if (typeOfAction == Actions.UpdateImage
                                    || typeOfAction == Actions.UpdateUser
                            ) {
                                if (workInfo.state == WorkInfo.State.FAILED) {
                                    Toast.makeText(
                                            applicationContext, "Failed updating user",
                                            Toast.LENGTH_SHORT
                                    ).show()
                                    return@observe
                                }
                            }

                            if (typeOfAction == Actions.GetUserToken) {
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
                            } else {
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
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.INTERNET),
                    100
            )
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
        sp = getSharedPreferences("local_app_db", Context.MODE_PRIVATE)

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



//        if(BuildConfig.DEBUG)
//        {
//            val editor: SharedPreferences.Editor = sp!!.edit()
//            editor.clear().apply()
//        }



//        val editor: SharedPreferences.Editor = sp!!.edit()
//        editor.clear().apply()

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
                user.getusername()
            else user.getPrettyName()

        if (curName != null) welcomeText!!.text = "Welcome Back Dear, \n$curName !"
        else welcomeText!!.text = ""
        prettyName!!.text = ""

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

}