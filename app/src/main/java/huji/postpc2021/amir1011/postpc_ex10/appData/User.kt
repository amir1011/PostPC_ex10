package huji.postpc2021.amir1011.postpc_ex10.appData

class User {
    var username: String? = null
    var pretty_name: String? = null
    var image_url: String? = null

    fun getusername(): String? {
        return username
    }

    fun getPrettyName(): String? {
        return pretty_name
    }

    fun getImage(): String? {
        return image_url
    }
}