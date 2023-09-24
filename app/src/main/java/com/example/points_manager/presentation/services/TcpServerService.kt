package com.example.points_manager.presentation.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.points_manager.R
import com.example.points_manager.domain.models.Point
import com.example.points_manager.domain.usecases.SavePointUseCase
import com.example.points_manager.presentation.App
import com.example.points_manager.presentation.utils.CoordsValidation
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import javax.inject.Inject

class TcpServerServiceCoroutine: Service() {

    private var serverSocket: ServerSocket? = null
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var savePointUseCase: SavePointUseCase

    override fun onCreate() {
        (applicationContext as App).appComponent.inject(this)
        startMeForeground()
        scope.launch(Dispatchers.IO) {
            var socket: Socket? = null
            try {
                serverSocket = ServerSocket(PORT)
                while (true) {
                    if (serverSocket != null) {
                        socket = serverSocket!!.accept()
                        Log.i(TAG, "New client: $socket")
                        val dataInputStream = DataInputStream(socket.getInputStream())
                        val dataOutputStream = DataOutputStream(socket.getOutputStream())

                        handleData(dataInputStream, dataOutputStream)
                        socket?.close()
                    } else {
                        Log.e(TAG, "Couldn't create ServerSocket!")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                try {
                    socket?.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    private suspend fun handleData(dataInputStream: DataInputStream, dataOutputStream: DataOutputStream) = withContext(Dispatchers.IO) {
        var canLoop = true
        while (canLoop) {
            try {
                if(dataInputStream.available() > 0){
                    val d = BufferedReader(InputStreamReader(dataInputStream))
                    val text = d.readText()
                    Log.i(TAG, "Received: " + text)
                    dataOutputStream.writeUTF("Hello Client")
                    dataOutputStream.writeUTF(trySavePoint(text))
                    canLoop = false
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.i(TAG, "Error: " + e.message)
                try {
                    dataInputStream.close()
                    dataOutputStream.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    Log.i(TAG, "Error: " + ex.message)
                }
            }  catch (e: InterruptedException) {
                e.printStackTrace()
                Log.i(TAG, "Received: " + e.message)
                try {
                    dataInputStream.close()
                    dataOutputStream.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    Log.i(TAG, "Received: " + e.message)
                }
            }
        }
    }

    private suspend fun trySavePoint(value: String): String {
        return try {
            val jsonElement = JsonParser.parseString(value)
            if(jsonElement.isJsonArray) {
                val jsonArray = jsonElement.asJsonArray
                jsonArray.forEach { jsonElement1 ->
                    tryParseAndSave(jsonElement1)
                }
            } else {
                tryParseAndSave(jsonElement)
            }
            "success"
        } catch (e: JsonParseException) {
            e.printStackTrace()
            "parse error"
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            "type error"
        }
    }

    private suspend fun tryParseAndSave(jsonElement: JsonElement) {
        val jsonObject = jsonElement.asJsonObject.getAsJsonObject("point")
        val latitude = jsonObject?.get("lat")?.asDouble
        val longitude = jsonObject?.get("long")?.asDouble
        if(latitude != null && longitude != null) {
            if(CoordsValidation.checkLatitude(latitude) && CoordsValidation.checkLongitude(longitude)) {
                withContext(Dispatchers.Main) {
                    savePointUseCase.invoke(Point(id = null, latitude, longitude))
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun startMeForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val NOTIFICATION_CHANNEL_ID = packageName
            val channelName = "Tcp Server Background Service"
            val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Tcp Server is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(2, notification)
        } else {
            startForeground(1, Notification())
        }
    }

    companion object {
        private val TAG = TcpServerServiceCoroutine::class.java.simpleName
        private const val PORT = 9876
    }

}