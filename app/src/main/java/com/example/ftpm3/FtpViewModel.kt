package com.example.ftpm3

import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.sauronsoftware.ftp4j.FTPFile
import java.io.File
import androidx.lifecycle.LiveData
import androidx.navigation.NavHostController
import com.example.ftpm3.Screens.Screens
import kotlinx.coroutines.*
import java.io.FileNotFoundException


class FtpViewModel(): ViewModel() {
    private var _ip = MutableLiveData("192.168.1.101")
    private var _port = MutableLiveData("21")
    private var _username = MutableLiveData("ethan")
    private var _password = MutableLiveData("ad")
    private var _defaultDir = MutableLiveData("/home/ethan/")
    private var repository: Repository = Repository()

    lateinit var _currentDirectory: MutableLiveData<String>
    var _listOfFiles: MutableLiveData<List<FTPFile>> = MutableLiveData<List<FTPFile>>(listOf())
    var _selectedFiles: MutableLiveData<List<FTPFile>> = MutableLiveData<List<FTPFile>>(listOf())
    val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    val downloadsPath = downloadsDirectory.getAbsolutePath();
    var selectedFilePath: File = File("")

    val ip: LiveData<String> = _ip
    val port: LiveData<String> = _port
    val username: LiveData<String> = _username
    val password: LiveData<String> = _password
    val defaultDir: LiveData<String> = _defaultDir
    fun removeLastDirectoryFromPath(path: String): String {
        val index = path.lastIndexOf('/')
        if (index == -1) return path
        val lastSecondIndex = path.lastIndexOf('/', index-1)
        return if (index >= 0) {
            path.substring(0, lastSecondIndex+1)
        } else {
            path
        }
    }

    fun getClientInstance() = repository.client

    fun onValueChanged(field: String, newTxt: String) {
        when(field) {
            "ip" -> _ip.value = newTxt
            "port" -> _port.value = newTxt
            "username" -> _username.value = newTxt
            "password" -> _password.value = newTxt
            "defaultDir" -> {
                _defaultDir.value = newTxt
            }
        }
    }

    fun onConnectClicked(ftpViewModel: FtpViewModel, navHostController: NavHostController) {
        if (!ftpViewModel.getClientInstance().isConnected) {
            try {
                Log.i("Tag","OnConnectClicked...")
                ftpViewModel.connect()
                _currentDirectory = MutableLiveData(_defaultDir.value)
                ftpViewModel.listDir()
                navHostController.navigate(Screens.Main.route)
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun connect() {
        Log.i("Tag","ftpview connect...")
        runBlocking {
            repository.connect(ip.value.toString(), port.value!!.toInt(), username.value.toString(), password.value.toString())
        }
    }

    fun disconnect() {
        runBlocking {
            repository.disconnect()
        }
    }

    fun listDir() {
        Log.i("Tag","ftpview listdir...")
        runBlocking {
            _listOfFiles.postValue(repository.listDirectory(_currentDirectory.value.toString()))
        }
    }

    fun downloadFile(remoteFilePath: String, fileName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.downloadFile(remoteFilePath, File("${downloadsPath+"/"+fileName}"))
        }
    }

    fun uploadFile() {
        CoroutineScope(Dispatchers.Main).launch {
            repository.uploadFile(selectedFilePath)
        }
    }
    fun uploadFile(file: File) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.uploadFile(file)
        }
    }
}