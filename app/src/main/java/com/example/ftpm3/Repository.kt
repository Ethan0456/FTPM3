package com.example.ftpm3

import android.util.Log
import it.sauronsoftware.ftp4j.FTPClient
import it.sauronsoftware.ftp4j.FTPFile
import kotlinx.coroutines.*
import java.io.File

class Repository {
    val client = FTPClient()

    suspend fun getClientInstance() = withContext(Dispatchers.IO) { client }
    suspend fun connect(ip: String, port: Int, username: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                Log.i("Tag","Connecting...")
                client.connect(ip, port)
                client.login(username, password)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            client.disconnect(true)
        }
    }

    suspend fun listDirectory(path: String): List<FTPFile> = withContext(Dispatchers.IO) {
        client.list(path).toList()
    }

    suspend fun downloadFile(remoteFile: String, localFile: File) {
        withContext(Dispatchers.IO) {
                client.download(remoteFile, localFile)
            }
    }
    suspend fun deleteFile(remoteFile: String) {
        withContext(Dispatchers.IO) {
            client.deleteFile(remoteFile)
        }
    }

    suspend fun uploadFile(localFile: File, remotePath: String) {
        withContext(Dispatchers.IO) {
            client.upload(localFile)
        }
    }
}
