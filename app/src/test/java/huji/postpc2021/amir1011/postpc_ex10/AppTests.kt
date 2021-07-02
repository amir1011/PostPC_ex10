package huji.postpc2021.amir1011.postpc_ex10

import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AppTests : TestCase() {

//    private lateinit var controller: Controller
//    lateinit var server: Server
//
//    @Before
//    fun setup() {
//        server = Server()
//        controller = Controller(server = server)
//    }
//
//    @Test
//    fun testLogin() {
//        val token = controller.login("IdanS")
//        assertTrue(token.isNotEmpty())
//    }
//
//    @Test
//    fun testGetUserInfo() {
//        controller.login("IdanS")
//        val user = controller.getUserInfo()
//        assertTrue(user?.username == "IdanS")
//    }
//
//    @Test
//    fun testEditUserPrettyName() {
//        controller.login("IdanS")
//        var user = controller.editUserPrettyName("Nice Name")
//        assertTrue(user?.pretty_name == "Nice Name")
//        user = controller.getUserInfo()
//        assertTrue(user?.pretty_name == "Nice Name")
//
//    }
//
//    @Test
//    fun testEditUserImage() {
//        controller.login("IdanS")
//        var user = controller.editUserImage(Controller.Images.FROG)
//        assertTrue(controller.getImageByUrl(user?.image_url!!) == Controller.Images.FROG)
//        user = controller.getUserInfo()
//        assertTrue(controller.getImageByUrl(user?.image_url!!) == Controller.Images.FROG)
//    }
}