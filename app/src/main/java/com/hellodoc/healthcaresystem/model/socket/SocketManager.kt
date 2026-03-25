package com.hellodoc.healthcaresystem.model.socket

import android.util.Log
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor() {
    private var socket: Socket? = null

    companion object {
        private const val TAG = "SocketManager"
    }

    fun connect(token: String) {
        if (socket != null && socket!!.connected()) {
            Log.d(TAG, "Socket already connected")
            return
        }

        try {
            val opts = IO.Options()
            opts.query = "token=$token"
            opts.reconnection = true
            opts.reconnectionAttempts = Int.MAX_VALUE
            opts.reconnectionDelay = 1000
            opts.timeout = 20000

            // Initialize socket with Base URL from RetrofitInstance
            socket = IO.socket(RetrofitInstance.BASE_URL, opts)

            initListeners()

            socket?.connect()
        } catch (e: URISyntaxException) {
            Log.e(TAG, "URISyntaxException: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Socket Init Error: ${e.message}")
        }
    }

    private fun initListeners() {
        socket?.on(Socket.EVENT_CONNECT) {
            Log.d(TAG, "Connected to Socket Server")
        }

        socket?.on(Socket.EVENT_DISCONNECT) {
            Log.d(TAG, "Disconnected from Socket Server")
        }

        socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
            if (args.isNotEmpty()) {
                val error = args[0]
                Log.e(TAG, "Connect Error: $error")
            }
        }
    }

    fun disconnect() {
        if (socket != null) {
            socket?.disconnect()
            socket?.off()
            socket = null
            Log.d(TAG, "Socket Disconnected and Released")
        }
    }

    fun isConnected(): Boolean {
        return socket?.connected() == true
    }
    
    // Explicitly for Admin stats or other events if needed
    fun getSocket(): Socket? {
        return socket
    }
}
