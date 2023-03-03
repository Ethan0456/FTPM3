package com.example.ftpm3

import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.sauronsoftware.ftp4j.FTPFile
import java.io.File
import androidx.compose.runtime.*
import androidx.lifecycle.LiveData
import androidx.navigation.NavHostController
import com.example.ftpm3.Screens.Screens
import kotlinx.coroutines.*


class FtpViewModel: ViewModel() {
    private var _ip = MutableLiveData("192.168.1.100")
    private var _port = MutableLiveData("21")
    private var _username = MutableLiveData("abhi")
    private var _password = MutableLiveData("AM*^(-)0418080904")
    private var repository: Repository = Repository()

    var _currentDirectory = MutableLiveData("/")
    var _listOfFiles: MutableLiveData<List<FTPFile>> = MutableLiveData<List<FTPFile>>(listOf())
    var _selectedFiles: MutableLiveData<List<FTPFile>> = MutableLiveData<List<FTPFile>>(listOf())
    val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    val downloadsPath = downloadsDirectory.getAbsolutePath();

    val ip: LiveData<String> = _ip
    val port: LiveData<String> = _port
    val username: LiveData<String> = _username
    val password: LiveData<String> = _password


//    private fun getInstance() {
//        runBlocking {
//            clientInstance = repository.getClientInstance()
//        }
//    }

//    fun checkClientInstance(): FTPClient {
//        getInstance()
//        return clientInstance
//    }

    fun getClientInstance() = repository.client

    fun selectFile() {
//        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
//        chooseFile.type = "*/*"
//        var chooseFileIntent = Intent.createChooser(chooseFile, "Choose a file")
//        startActivityForResult(this, chooseFileIntent)
    }

    fun onValueChanged(field: String, newTxt: String) {
        when(field) {
            "ip" -> _ip.value = newTxt
            "port" -> _port.value = newTxt
            "username" -> _username.value = newTxt
            "password" -> _password.value = newTxt
        }
    }

    fun onConnectClicked(ftpViewModel: FtpViewModel, navHostController: NavHostController) {
//        if (!ftpViewModel.checkClientInstance().isConnected) {
        if (!ftpViewModel.getClientInstance().isConnected) {
            try {
                Log.i("Tag","OnConnectClicked...")
                ftpViewModel.connect()
                ftpViewModel.listDir()
                navHostController.navigate(Screens.Main.route)
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }
        else {
//            ftpViewModel.listDir()
        }
    }

    fun connect() {
        Log.i("Tag","ftpview connect...")
        val res = CoroutineScope(Dispatchers.Main).launch {
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
//            Files.createDirectory(Paths.get(downloadsPath+"/"+"FTPM3"))
        CoroutineScope(Dispatchers.Main).launch {
            repository.downloadFile(remoteFilePath, File("${downloadsPath+"/"+fileName}"))
        }
    }
}