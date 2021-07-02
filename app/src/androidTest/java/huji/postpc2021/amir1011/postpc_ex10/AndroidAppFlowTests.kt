package huji.postpc2021.amir1011.postpc_ex10


import android.content.Intent
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import junit.framework.Assert.assertTrue
import junit.framework.TestCase
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.atomic.AtomicBoolean


@RunWith(JUnit4::class)
class AndroidAppFlowTests {
    private lateinit var activityController: ActivityScenario<MainActivity>

    @Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

//    var activityRule = ActivityTestRule(MainActivity::class.java, true, false)


//    @Test
//    fun test() {
//        activityScenarioRule.scenario.onActivity { activity: MainActivity? -> }
//    }

    @Before
    fun setUp() {

//        BuildConfig.DEBUG = true

//        val activityIntent = Intent()
//        activityIntent.putExtra("testMode", true)
//        setActivityIntent(activityIntent)

//        val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
//        activityScenarioRule.scenario.onActivity { activity: MainActivity? ->
//            val sp = activity!!.baseContext.getSharedPreferences("local_app_db", Context.MODE_PRIVATE)
////        val sp = ServerApplication.getSp()
//        val editor: SharedPreferences.Editor = sp!!.edit()
//        editor.clear().apply()
//        }
//        activityController = launch(MainActivity::class.java)
//        val sp = ServerApplication.getSp()
//        val editor: SharedPreferences.Editor = sp!!.edit()
//        editor.clear().apply()

        activityController = launch(MainActivity::class.java)
    }

//    @After
//    fun clean(){
//        BuildConfig.DEBUG = false
//    }


    @Test
    fun firstTimeEnterUsername_then_welcomePageWithAlienAppears() {

        onView(withId(R.id.username)).perform(ViewActions.typeText("SuperMan"))
        closeSoftKeyboard()
        onView(withId(R.id.connect)).perform(ViewActions.click())

        onView(withId(R.id.username)).check(matches(not(isDisplayed())))
        onView(withId(R.id.connect)).check(matches(not(isDisplayed())))

        onView(withId(R.id.new_name)).check(matches(isDisplayed()))
        onView(withId(R.id.changeName)).check(matches(isDisplayed()))
        onView(withId(R.id.linearLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.userImage)).check(matches(isDisplayed()))
    }

    @Test
    fun createOrder_then_inEditScreen_deleteOrder_thenMainScreenShouldBeShown() {

        onView(withId(R.id.new_name)).perform(ViewActions.typeText("SpiderMan"))
        closeSoftKeyboard()
        onView(withId(R.id.changeName)).perform(ViewActions.click())

        var curStr: String? = null
        activityScenarioRule.scenario.onActivity { act ->
            curStr = act.findViewById<TextView>(R.id.userWelcome).text.toString()
        }

        assertTrue(curStr == "Welcome Back Dear, SpiderMan !")
    }
}