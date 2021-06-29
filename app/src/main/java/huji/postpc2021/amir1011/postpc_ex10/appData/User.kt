package huji.postpc2021.amir1011.postpc_ex10.appData

class User {
    var name: String? = null
    var prettyVerName: String? = null
    var imageUrl: String? = null

    fun getUsername(): String? {
        return name
    }

    fun getPrettyName(): String? {
        return prettyVerName
    }

    fun getImage(): String? {
        return imageUrl
    }
}