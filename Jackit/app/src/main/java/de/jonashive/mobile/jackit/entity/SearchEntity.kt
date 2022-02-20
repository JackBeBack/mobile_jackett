package de.jonashive.mobile.jackit.entity

class SearchItemEntity{
    @JvmField var rss: Channel = Channel()
}

class Channel{
    @JvmField var item: Array<Item> = arrayOf()
}

class Item{
    @JvmField var title: String? = null
    @JvmField var pubDate: String? = null
    @JvmField var size: String? = null
    @JvmField var guid: String? = null
    var gb : String = "0"
        get() = String.format("%.2f", (((size?.toLong() ?: 0)/1024).toFloat()/1024/1024))
    var seeder: String? = null
    var peer: String? = null
    var magnet: String? = null

}