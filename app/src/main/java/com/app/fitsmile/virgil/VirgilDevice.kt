package com.app.android.dentulu.virgil

import android.content.Context
import android.util.Log
import com.app.fitsmile.BuildConfig.BASE_URL
import com.app.fitsmile.app.AppPreference
import com.app.fitsmile.virgil.VirgilInitializeListener
import com.virgilsecurity.android.common.model.EThreeParams
import com.virgilsecurity.android.ethree.interaction.EThree
import com.virgilsecurity.common.model.Completable
import com.virgilsecurity.common.model.Result
import com.virgilsecurity.sdk.cards.Card
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.measureTimeMillis

open class VirgilDevice(val context: Context) {

    var eThree: EThree? = null

    private val benchmarking = false

    fun log(e: String) {
        Log.d("Virgil", e)
    }

    fun getVirgilToken(appPreference: AppPreference): String {
        try {
            val baseUrl =
                    BASE_URL + "virgil_jwt"
            val fullUrl = URL(baseUrl)

            val urlConnection = fullUrl.openConnection() as HttpURLConnection
            urlConnection.setRequestProperty("Authorization", appPreference.token)
            urlConnection.setRequestProperty("Dentulu-Userid", appPreference.userId)
            urlConnection.requestMethod = "GET"

            val httpResult = urlConnection.responseCode
            if (httpResult == HttpURLConnection.HTTP_OK) {
                val response =
                        InputStreamReader(urlConnection.inputStream, "UTF-8").buffered().use {
                            it.readText()
                        }
                val jsonObject = JSONObject(response)

                return jsonObject.getString("virgilToken")
            } else {
                throw RuntimeException("$httpResult")
            }
        } catch (e: IOException) {
            throw RuntimeException("$e")
        } catch (e: JSONException) {
            throw RuntimeException("$e")
        }
    }

    fun initialize(appPreference: AppPreference, virgilInitializeListener: VirgilInitializeListener) {
        if (appPreference.firebaseUID.isNotEmpty()) {
            try {
                val identity = appPreference.firebaseUID
                log("initializing virgil for identity : $identity")
                val eThreeParams = EThreeParams(identity, { getVirgilToken(appPreference) }, context)
                eThree = EThree(eThreeParams)
                log("Initialized Successfully")
                virgilInitializeListener.onInitialized()
            } catch (e: Exception) {
                log("Failed initializing: ${e.message}")
                virgilInitializeListener.onError(e.message)
            }
        } else {
            log("Failed initializing: Firebase UID not found")
            virgilInitializeListener.onError("Chat details not found, please login again")
        }
    }

    fun isInitialized(): Boolean {
        return eThree != null
    }

    fun getEThreeInstance(): EThree {
        val eThree = eThree

        if (eThree == null) {
            val errorMessage = "eThree not initialized"
            throw Throwable(errorMessage)
        }

        return eThree
    }

    fun register(): Completable {
        val eThree = getEThreeInstance()

        //# start of snippet: e3kit_register
        return eThree.register()
        //# end of snippet: e3kit_register
    }

    fun findUser(identity: String): Result<Card> {
        val eThree = getEThreeInstance()
        return eThree.findUser(identity)
    }

    fun encrypt(text: String, card: Card): String {
        val eThree = getEThreeInstance()
        var encryptedText = ""
        var time: Long = 0

        try {
            val repetitions = if (benchmarking) 100 else 1
            for (i in 1..repetitions) {
                time += measureTimeMillis {
                    //# start of snippet: e3kit_encrypt
                    encryptedText = eThree.authEncrypt(text, card)
                    //# end of snippet: e3kit_encrypt
                }
            }

            log("Encrypted and signed: '$encryptedText'. Took: ${time / repetitions}ms")
        } catch (e: Throwable) {
            log("Failed encrypting and signing: $e")
            throw Throwable(e)
        }

        return encryptedText
    }

    fun decrypt(text: String, senderCard: Card): String {
        val eThree = getEThreeInstance()
        var decryptedText = ""
        var time: Long = 0

        try {
            val repetitions = if (benchmarking) 100 else 1
            for (i in 1..repetitions) {
                time += measureTimeMillis {
                    //# start of snippet: e3kit_decrypt
                    decryptedText = eThree.authDecrypt(text, senderCard)
                    //# end of snippet: e3kit_decrypt
                }

            }
            log("Decrypted and verified: $decryptedText. Took: ${time / repetitions}ms")
        } catch (e: Throwable) {
            log("Failed decrypting and verifying: $e")
            throw Throwable(e)
        }

        return decryptedText
    }

    fun getVirgilKey(appPreference: AppPreference): String {
        val baseUrl =
                BASE_URL + "virgil_key"
        val fullUrl = URL(baseUrl)

        val urlConnection = fullUrl.openConnection() as HttpURLConnection
        urlConnection.doOutput = true
        urlConnection.doInput = true
        urlConnection.setRequestProperty(
                "Authorization",
                appPreference.token
        )
        urlConnection.setRequestProperty("Dentulu-Userid", appPreference.userId)
        urlConnection.requestMethod = "GET"

        val httpResult = urlConnection.responseCode
        if (httpResult == HttpURLConnection.HTTP_OK) {
            val response =
                    InputStreamReader(urlConnection.inputStream, "UTF-8").buffered().use {
                        it.readText()
                    }

            val jsonObject = JSONObject(response)

            val virgilKey = jsonObject.getString("virgilKey")
            log("Password got from api endpoint to backup/restore : $virgilKey")
            return virgilKey
        } else {
            throw Throwable("$httpResult")
        }
    }

    fun backupPrivateKey(appPreference: AppPreference): Completable {
        val eThree = getEThreeInstance()
        val password = getVirgilKey(appPreference)
        log("Password used to backup $password")
        return eThree.backupPrivateKey(password)
    }

    fun restorePrivateKey(appPreference: AppPreference): Completable {
        val eThree = getEThreeInstance()
        val password = getVirgilKey(appPreference)
        log("Password used to restore $password")
        return eThree.restorePrivateKey(password)
    }

    fun resetPrivateKeyBackup(): Completable {
        val eThree = getEThreeInstance()
        return eThree.resetPrivateKeyBackup()
    }

    fun hasLocalPrivateKey(): Boolean {
        val eThree = getEThreeInstance()
        return eThree.hasLocalPrivateKey()
    }

    fun rotatePrivateKey(): Completable {
        val eThree = getEThreeInstance()
        return eThree.rotatePrivateKey()
    }

    fun cleanup() {
        try {
            val eThree = getEThreeInstance()
            eThree.cleanup()
        } catch (e: Throwable) {
            log("Failure cleaning up: ${e.message}")
        }
    }

    fun deInitialiseVirgil() {
        eThree = null
    }

    fun unregister(): Completable {
        val eThree = getEThreeInstance()
        return eThree.unregister()
    }
}