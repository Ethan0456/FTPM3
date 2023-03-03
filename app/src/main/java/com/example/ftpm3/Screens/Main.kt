package com.example.ftpm3.Screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ftpm3.FtpViewModel
import com.example.ftpm3.`UI-Components`.SelectableItem
import it.sauronsoftware.ftp4j.FTPFile
import org.w3c.dom.Text

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
            .fillMaxSize()
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
