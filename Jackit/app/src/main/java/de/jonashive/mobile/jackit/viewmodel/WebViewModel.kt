package de.jonashive.mobile.jackit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prof.rssparser.Parser
import de.jonashive.mobile.jackit.Variable
import de.jonashive.mobile.jackit.VariablesViewModel
import de.jonashive.mobile.jackit.entity.Inderxers
import de.jonashive.mobile.jackit.entity.Indexer
import de.jonashive.mobile.jackit.entity.SearchItemEntity
import de.jonashive.mobile.jackit.ui.composables.webViewModle
import de.jonashive.mobile.jackit.utility.XMLParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.nio.charset.Charset


class WebViewModel: ViewModel() {
    var client = OkHttpClient()

    var indexers = MutableLiveData<List<Indexer>>()
    var searchResult = MutableLiveData<SearchItemEntity>()
    var loadingState = MutableLiveData(LoadingState.DONE)

    var errors = MutableLiveData<String>("")

    val parser = Parser.Builder()
        .charset(Charset.forName("ISO-8859-7"))
        .cacheExpirationMillis(24L * 60L * 60L * 100L) // one day
        .build()

    companion object {
        var singelton = WebViewModel()
    }

    init {

    }

    fun call(url: String, callback: (Response) -> (Unit)){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request: Request = Request.Builder()
                    .addHeader("Accept", "application/json")
                    .url(url)
                    .build()
                client.newCall(request).execute().use { response -> callback(response) }
            }catch (e: Exception){
                errors.postValue(e.message)
                loadingState.postValue(LoadingState.FAILED)
            }

        }
    }

    fun search(querry: String, source: String){
        loadingState.postValue(LoadingState.LOADING)
        searchResult.postValue(SearchItemEntity())
        val indexer = when(source){
            "Nyaa.si" -> "nyaasi"
            "AudioBookBay" -> "audiobookbay"
            "RARBG" -> "rarbg"
            "Anidex" -> "anidex"
            else -> source
        }
        val url = "${parsUrl("api/v2.0/indexers/$indexer/results/torznab/api")}&t=search&q=${querry.replace(" ", "%")}"

        println(url)
        viewModelScope.launch(Dispatchers.IO) {
            call(url){
                val body = it.body?.string()
                val torznab = body?.split("torznab:attr ")
                val search = XMLParser(SearchItemEntity::class.java).fromXML(body ?: "")

                val categorys = mutableListOf<String>()
                val seeders = mutableListOf<String>()
                val peers = mutableListOf<String>()
                val magnets = mutableListOf<String>()

                torznab?.forEach {
                    when{
                        it.startsWith("name=\"category\"") -> {categorys.add(it.split("\"")[3])}
                        it.startsWith("name=\"seeders\"") -> {seeders.add(it.split("\"")[3])}
                        it.startsWith("name=\"peers\"") -> {peers.add(it.split("\"")[3])}
                        it.startsWith("name=\"magneturl\"") -> {magnets.add(it.split("\"")[3])}
                    }
                }

                try {
                    search.rss?.item?.forEachIndexed { index, item ->
                        item.apply {
                            if (seeders.size > index){
                                this.seeder = seeders[index]
                            }
                            if (peers.size > index){
                                this.peer = peers[index]
                            }
                            if (magnets.size > index){
                                this.magnet = magnets[index]
                            }
                        }
                    }
                } catch (e: Exception){
                    errors.postValue(e.message)
                    loadingState.postValue(LoadingState.FAILED)
                }



                loadingState.postValue(LoadingState.DONE)
                if (search.rss.item.size == 0){
                    errors.postValue("No Results for: $querry")
                }else{
                    searchResult.postValue(search)
                }
            }
        }
    }

    fun getIndexer(){
        //val url = "http://192.168.178.100:9117/api/v2.0/indexers/all/results/torznab/api?apikey=r0x35ycq4j2qbwvma2l7gop3tpz3albq&t=indexers&configured=true"
        val url = "${parsUrl("api/v2.0/indexers/all/results/torznab/api")}&t=indexers&configured=true"

        println(url)
        viewModelScope.launch(Dispatchers.IO) {
            call(url){
                val indexer = XMLParser(Inderxers::class.java).fromXML(it.body?.string() ?: "")
                indexers.postValue(indexer.indexer?.toList() ?: listOf())
            }
        }
    }

    fun getMagnetFor(url: String){
        val magnetRegex = "magnet:\\?xt=urn:btih:[a-zA-Z0-9&=%.-]*".toRegex()
        viewModelScope.launch(Dispatchers.IO){
            call(url){
                val content = it.body?.string()
                val link = magnetRegex.find(content ?: "")
                addTransfer(link?.value){
                    if(it.code != 200){
                        errors.postValue("Transfer Failed (${it.code})")
                    }
                }
            }
        }
    }

    fun rss(){
        viewModelScope.launch {
            try {
                val channel = parser.getChannel("url")
                // Do something with your data
                channel.articles.forEach {
                    println(it.title)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception
            }
        }
    }

    fun parsUrl(path: String): String{
        val baseUrl = VariablesViewModel.singelton.read(Variable.BASE_URL)
        val port = VariablesViewModel.singelton.read(Variable.PORT)
        val apikey = VariablesViewModel.singelton.read(Variable.JACKETT_API_KEY)

        return "$baseUrl:$port/$path?apikey=$apikey"
    }

    fun addTransfer(link: String?, callback: (Response) -> (Unit)){
        val url = "https://www.premiumize.me/api/transfer/create"

        val formBody: RequestBody = FormBody.Builder()
            .add("apikey", "micchce9593itkg2")
            .add("src", link ?: "")
            .build()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request: Request = Request.Builder()
                    .addHeader("Accept", "application/json")
                    .post(formBody)
                    .url(url)
                    .build()
                client.newCall(request).execute().use { response -> callback(response) }
            }catch (e: Exception){
                errors.postValue(e.message)
                loadingState.postValue(LoadingState.FAILED)
            }

        }
    }
}

enum class LoadingState {
    LOADING,
    DONE,
    FAILED
}