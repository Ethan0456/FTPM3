package com.example.ftpm3.Screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.ftpm3.FtpViewModel
import com.example.ftpm3.`UI-Components`.CustomDialog
import com.example.ftpm3.`UI-Components`.SelectableItem
import it.sauronsoftware.ftp4j.FTPFile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    ftpViewModel: FtpViewModel,
    navHostController: NavHostController
) {
    var listOfFilesObserver = ftpViewModel._listOfFiles.observeAsState<List<FTPFile>>()
    var curDirObserver = ftpViewModel._currentDirectory.observeAsState()
    var selectedFilesObserver = ftpViewModel._selectedFiles.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        CenterAlignedTopAppBar(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .weight(0.55f),
            title = {
                Text(
                    text = "FTPClient",
                    color = MaterialTheme.colorScheme.primary,
                    style = TextStyle(fontSize = 25.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                )
            },
            actions = {
                if (selectedFilesObserver.value!!.isNotEmpty()) {
                    IconButton(onClick = {
                        if (selectedFilesObserver.value!!.size != listOfFilesObserver.value!!.size) {
                            listOfFilesObserver.value!!.forEach {
                                if (!ftpViewModel._selectedFiles.value!!.contains(it)) {
                                    ftpViewModel._selectedFiles.setValue(ftpViewModel._selectedFiles.value?.plus(listOf(it)))
                                }
                            }
                        } else {
                            ftpViewModel._selectedFiles.value = listOf()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Localized description"
                        )
                    }
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .weight(9f)
        ) {
            items(
                count = listOfFilesObserver.value!!.size,
                key = {
                      index -> listOfFilesObserver.value!![index].hashCode()
                },
                itemContent = { index ->
                    val file = listOfFilesObserver.value!![index]
                    val ftype = file.type
                    val isSelected = selectedFilesObserver.value!!.contains(file)
                SelectableItem (
                    modifier = Modifier
                        .padding(5.dp),

                    selected = true,
                    title = file.name,
                    subTitle = if (ftype == 1) {
                        "Dir"
                    } else {
                        "File ${file.size.toInt()} MB"
                    },
                    type = ftype,
                    checkState = isSelected,
                    onClick = {
                        if (ftype == 1) {
                            ftpViewModel._currentDirectory.setValue(ftpViewModel._currentDirectory.value + file.name + "/")
                            ftpViewModel.listDir()
                            Log.i("Tag", "Directory: ${ftpViewModel._currentDirectory.value.toString()}")
                            Log.i("Tag", file.name)
                        } else {
                            try {
                                ftpViewModel.downloadFile(remoteFilePath = curDirObserver.value+"/"+file.name,fileName = file.name)
                            } catch (e: FileAlreadyExistsException) {
                                Log.i("Tag", "${file.name} already exists")
                            }
                        }
                    })
                {
                    b:Boolean ->
                    when(b) {
                        false-> ftpViewModel._selectedFiles.setValue(ftpViewModel._selectedFiles.value?.toList()?.plus(listOf(file)))
                        true-> ftpViewModel._selectedFiles.setValue(ftpViewModel._selectedFiles.value?.toList()?.minus(listOf(file)))
                    }
                }
                }
            )
        }
        BottomAppBar(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .weight(1f)
                .padding(0.dp),
            actions = {
                Text(
                    text = "Path : ${curDirObserver.value.toString()}",
                    color = MaterialTheme.colorScheme.primary,
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (selectedFilesObserver.value!!.isEmpty()) {
                            Log.i("Tag", "Uploading")


                        } else {
                            Log.i("Tag", "Size : ${selectedFilesObserver.value!!.size}")
                            for (file in selectedFilesObserver.value!!.toList()) {
                                try {
                                    ftpViewModel.downloadFile(remoteFilePath = curDirObserver.value+"/"+file.name,fileName = file.name)
                                } catch (e: FileAlreadyExistsException) {
                                    Log.i("Tag", "${file.name}")
                                }
                            }
                        }
                    },
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    if (selectedFilesObserver.value!!.isEmpty()) {
                        Icon(Icons.Default.Add, "Upload File")
                    } else {
                        Icon(Icons.Default.ArrowDropDown, "Download File")
                    }
                }
            }
        )
    }
    var showDialog = remember { mutableStateOf(false) }
    BackHandler {
        Log.i(TAG,"${ftpViewModel.defaultDir.value.toString()}")
        Log.i(TAG,"${ftpViewModel._currentDirectory.value.toString()}")
        if (ftpViewModel._currentDirectory.value != ftpViewModel.defaultDir.value) {
            Log.i(TAG,"BackPressed")
            ftpViewModel._currentDirectory.setValue(ftpViewModel.removeLastDirectoryFromPath(ftpViewModel._currentDirectory.value.toString()))
            ftpViewModel.listDir()
        }
        else {
            showDialog.value = true
        }
    }
//    ExitDialogBox(
//        ftpViewModel = ftpViewModel,
//        navHostController = navHostController,
//        showDialog = showDialog
//    )

    CustomDialog(
        ftpViewModel = ftpViewModel,
        navHostController = navHostController,
        showDialog = showDialog,
        title = "Do You Want to Disconnect?",
        composable = {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("No")
                }
                Button(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    onClick = {
                        navHostController.popBackStack()
                        ftpViewModel.disconnect()
                    }
                ) {
                    Text("Yes")
                }
            }
        }
    )
}

@Composable
fun ExitDialogBox(
    ftpViewModel: FtpViewModel,
    navHostController: NavHostController,
    showDialog: MutableState<Boolean>
) {
    if (showDialog.value == true) {
        Dialog(
            onDismissRequest = { showDialog.value = false }
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .wrapContentSize()
                    .clip(RoundedCornerShape(10.dp)),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Do you want to Disconnect?")
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier.width(100.dp),
                        onClick = {
                            showDialog.value = false
                        }
                    ) {
                        Text("No")
                    }
                    Button(
                        modifier = Modifier.width(100.dp),
                        onClick = {
                            ftpViewModel.disconnect()
                            navHostController.popBackStack()
                        }
                    ) {
                        Text("Yes")
                    }
                }
            }
        }
    }
}
//            items(items=listOfFilesObserver.value!!.toList()) { file ->
//                val ftype = file.type
//                SelectableItem (
//                    modifier = Modifier
//                        .padding(5.dp),
//
//                    selected = true,
//                    title = file.name,
//                    subTitle = if (ftype == 1) {
//                        "Dir"
//                    } else {
//                        "File ${file.size.toInt()} MB"
//                    },
//                    type = ftype,
//                    onClick = {
//                        if (ftype == 1) {
//                            ftpViewModel._currentDirectory.setValue(ftpViewModel._currentDirectory.value + file.name + "/")
//                            ftpViewModel.listDir()
//                            Log.i("Tag", "Directory: ${ftpViewModel._currentDirectory.value.toString()}")
//                            Log.i("Tag", file.name)
//                        } else {
//                            ftpViewModel.downloadFile(fileName = file.name)
//                        }
//                    })
//                {
//                        b:Boolean ->
//                    when(b) {
//                        false-> ftpViewModel._selectedFiles.setValue(ftpViewModel._selectedFiles.value?.toList()?.minus(listOf(file)))
//                        true-> ftpViewModel._selectedFiles.setValue(ftpViewModel._selectedFiles.value?.toList()?.plus(listOf(file)))
//                    }
//                }
//            }
