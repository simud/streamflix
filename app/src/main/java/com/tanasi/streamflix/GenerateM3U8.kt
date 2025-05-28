package com.tanasi.streamflix

import com.tanasi.streamflix.models.Movie
import com.tanasi.streamflix.models.Video
import com.tanasi.streamflix.providers.StreamingCommunityProvider
import com.tanasi.streamflix.utils.UserPreferences
import kotlinx.coroutines.runBlocking
import java.io.File

fun generateM3U8(movieTitlesOrIds: List<String>, outputFile: String) {
    // Inizializza UserPreferences (simulazione senza contesto Android)
    UserPreferences.setup(object : android.content.Context {
        override fun getSharedPreferences(name: String, mode: Int): android.content.SharedPreferences {
            return object : android.content.SharedPreferences {
                private val data = mutableMapOf<String, Any>()
                override fun getAll(): Map<String, *> = data
                override fun getString(key: String, defValue: String?): String? = data[key] as? String ?: defValue
                override fun getStringSet(key: String, defValue: Set<String>?): Set<String>? = null
                override fun getInt(key: String, defValue: Int): Int = data[key] as? Int ?: defValue
                override fun getLong(key: String, defValue: Long): Long = data[key] as? Long ?: defValue
                override fun getFloat(key: String, defValue: Float): Float = data[key] as? Float ?: defValue
                override fun getBoolean(key: String, defValue: Boolean): Boolean = data[key] as? Boolean ?: defValue
                override fun contains(key: String): Boolean = data.containsKey(key)
                override fun edit(): android.content.SharedPreferences.Editor {
                    return object : android.content.SharedPreferences.Editor {
                        override fun putString(key: String, value: String?): android.content.SharedPreferences.Editor {
                            value?.let { data[key] = it }; return this
                        }
                        override fun putStringSet(key: String, values: Set<String>?): android.content.SharedPreferences.Editor = this
                        override fun putInt(key: String, value: Int): android.content.SharedPreferences.Editor {
                            data[key] = value; return this
                        }
                        override fun putLong(key: String, value: Long): android.content.SharedPreferences.Editor {
                            data[key] = value; return this
                        }
                        override fun putFloat(key: String, value: Float): android.content.SharedPreferences.Editor {
                            data[key] = value; return this
                        }
                        override fun putBoolean(key: String, value: Boolean): android.content.SharedPreferences.Editor {
                            data[key] = value; return this
                        }
                        override fun remove(key: String): android.content.SharedPreferences.Editor {
                            data.remove(key); return this
                        }
                        override fun clear(): android.content.SharedPreferences.Editor {
                            data.clear(); return this
                        }
                        override fun commit(): Boolean = true
                        override fun apply() {}
                    }
                }
                override fun registerOnSharedPreferenceChangeListener(listener: android.content.SharedPreferences.OnSharedPreferenceChangeListener) {}
                override fun unregisterOnSharedPreferenceChangeListener(listener: android.content.SharedPreferences.OnSharedPreferenceChangeListener) {}
            }
        }
        // Implementazioni minime per altri metodi Context
        override fun getResources(): android.content.res.Resources = throw NotImplementedError()
        override fun getPackageName(): String = "com.tanasi.streamflix"
        override fun getSystemService(name: String): Any? = null
        override fun getApplicationContext(): android.content.Context = this
        override fun getFilesDir(): java.io.File = java.io.File(System.getProperty("user.dir"))
        override fun getString(id: Int): String = id.toString()
        override fun getString(id: Int, vararg formatArgs: Any): String = id.toString()
        override fun getSharedPreferences(name: String, mode: Int, callback: android.content.SharedPreferences.OnSharedPreferenceChangeListener?): android.content.SharedPreferences = getSharedPreferences(name, mode)
    })

    val m3u8Content = StringBuilder("#EXTM3U\n")
    runBlocking {
        movieTitlesOrIds.forEach { input ->
            try {
                // Se l'input è un ID (formato id-slug), usalo direttamente
                val movieId = if (input.matches(Regex("\\d+-.*"))) {
                    input
                } else {
                    // Cerca il film per titolo
                    val searchResults = StreamingCommunityProvider.search(input, 1)
                    (searchResults.find { it is Movie } as? Movie)?.id
                }

                if (movieId != null) {
                    val servers = StreamingCommunityProvider.getServers(
                        movieId,
                        Video.Type.Movie(movieId, input, "", "")
                    )
                    val server = servers.firstOrNull()
                    if (server != null) {
                        val video = StreamingCommunityProvider.getVideo(server)
                        m3u8Content.append("#EXTINF:-1,${input}\n${video.source}\n")
                    } else {
                        println("Nessun server trovato per il film: $input")
                    }
                } else {
                    println("Film non trovato: $input")
                }
            } catch (e: Exception) {
                println("Errore durante l'elaborazione di $input: ${e.message}")
            }
        }
    }
    File(outputFile).writeText(m3u8Content.toString())
    println("File M3U8 generato: $outputFile")
}

fun main() {
    // Esempio: lista di titoli o ID di film
    val movies = listOf("The Matrix", "Inception")
    generateM3U8(movies, "movies.m3u8")
}
