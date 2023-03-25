package com.example.ftpm3.Screens

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ftpm3.FtpViewModel
import com.example.ftpm3.R
import com.example.ftpm3.`UI-Components`.CustomDialog
import com.example.ftpm3.`UI-Components`.SelectableItem
import it.sauronsoftware.ftp4j.FTPFile
import java.io.File
import java.io.FileOutputStream


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    ftpViewModel: FtpViewModel,
    navHostController: NavHostController
) {
    var listOfFilesObserver = ftpViewModel._listOfFiles.observeAsState<List<FTPFile>>()
    var curDirObserver = ftpViewModel._currentDirectory.observeAsState()
    var selectedFilesObserver = ftpViewModel._selectedFiles.observeAsState()

    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Text(
            text = "FTPClient",
            color = MaterialTheme.colorScheme.primary,
            style = TextStyle(fontSize = 25.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
        )
        CenterAlignedTopAppBar(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .weight(0.55f),
            title = {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "${curDirObserver.value.toString().replace("/"," | ").substring(1)}",
                        modifier = Modifier.horizontalScroll(scrollState),
                        color = MaterialTheme.colorScheme.primary,
                        style = TextStyle(
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        textAlign = TextAlign.Left,
                        maxLines = 1,
                    )
                }
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

                    val fileSizeInKilobytes = file.size / 1024.0
                    val fileSizeInMegabytes = fileSizeInKilobytes / 1024.0
                    val fileSizeInGigabytes = fileSizeInMegabytes / 1024.0
                    val modifiedDate = file.modifiedDate

                    val perfectSize =
                    if (file.size > 0 && file.size < 999) { String.format("%.2f B", file.size.toFloat()) }
                    else if (file.size > 999 && fileSizeInKilobytes < 999) { String.format("%.2f KB", fileSizeInKilobytes.toFloat()) }
                    else if (fileSizeInKilobytes > 999 && fileSizeInMegabytes < 999) { String.format("%.2f MB", fileSizeInMegabytes.toFloat()) }
                    else { String.format("%.2f GB", fileSizeInGigabytes.toFloat()) }

                    SelectableItem (
                        icon = (if (ftype == 1) {
                            painterResource(id = R.drawable.folder)
                        } else {
                            painterResource(id = R.drawable.file)
                        }),
                        modifier = Modifier
                            .padding(5.dp),
                        selected = true,
                        title = file.name,
                        subTitle = if (ftype == 1) {
                            "Directory"
                        } else {
                            "Size : $perfectSize\nModified Date : $modifiedDate "
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
                        },
                        onLongPress = {

                        }
                    )
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
                .background(color = Color.Transparent)
                .weight(1f)
                .padding(0.dp),
            actions = {
//                Row(
//                    modifier = Modifier.weight(8f),
//                    horizontalArrangement = Arrangement.End,
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.addbox),
//                        modifier = Modifier
//                            .size(30.dp)
//                            .padding(start = 3.dp, end = 3.dp)
//                            .clickable {
//
//                            },
//                        contentDescription = "Add New",
//                    )
//                    Icon(
//                        painter = painterResource(id = R.drawable.sync),
//                        modifier = Modifier
//                            .size(30.dp)
//                            .padding(start = 3.dp, end = 3.dp)
//                            .clickable {
//
//                            },
//                        contentDescription = "Sync"
//                    )
//                }
            },
            floatingActionButton = {
                CustomFloatingActionButton(
                    ftpViewModel = ftpViewModel,
                    selectedFilesObserver = selectedFilesObserver,
                    curDirObserver = curDirObserver
                )
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

    CustomDialog(
        ftpViewModel = ftpViewModel,
        navHostController = navHostController,
        showDialog = showDialog,
        title = "Do You Want to Disconnect?",
        composable = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .wrapContentSize(),
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("No")
                }
                Button(
                    modifier = Modifier
                        .wrapContentSize(),
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
@SuppressLint("Range")
private fun getDisplayName(contentResolver: ContentResolver, uri: Uri): String {
    var displayName = ""
    val cursor = contentResolver.query(uri, null, null, null, null)
    cursor?.let {
        if (it.moveToFirst()) {
            displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
        it.close()
    }
    return displayName
}

private fun getFileFromUri(context: Context, uri: Uri): File {
    val contentResolver = context.contentResolver
    val displayName = getDisplayName(contentResolver, uri)
    val inputStream = contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, displayName)
    val outputStream = FileOutputStream(file)

    inputStream?.copyTo(outputStream)

    return file
}


@Composable
fun CustomFloatingActionButton(
    ftpViewModel: FtpViewModel,
    selectedFilesObserver: State<List<FTPFile>?>,
    curDirObserver: State<String?>
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                uri?.let {
                    val file = getFileFromUri(context, uri)
                    ftpViewModel.uploadFile(file)
                }
            }
        }
    )
    FloatingActionButton(
        onClick = {
            if (selectedFilesObserver.value!!.isEmpty()) {
                Log.i("Tag", "Uploading")
                launcher.launch("*/*")
                if (ftpViewModel.selectedFilePath != null){
                    Log.i(TAG,"Uploading...${ftpViewModel.selectedFilePath.absolutePath}")
                    ftpViewModel.uploadFile()
                }
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
//            Icon(Icons.Default.KeyboardArrowUp, "Upload File")
            Icon(
                painter = painterResource(id = R.drawable.upload),
                modifier = Modifier.size(30.dp),
                contentDescription = "Visibility Icon"
            )
        } else {
//            Icon(Icons.Default.KeyboardArrowDown, "Download File")
            Icon(
                painter = painterResource(id = R.drawable.download),
                modifier = Modifier.size(30.dp),
                contentDescription = "Visibility Icon"
            )
        }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////
//  Additional Code

//
//@Composable
//fun ExitDialogBox(
//    ftpViewModel: FtpViewModel,
//    navHostController: NavHostController,
//    showDialog: MutableState<Boolean>
//) {
//    if (showDialog.value == true) {
//        Dialog(
//            onDismissRequest = { showDialog.value = false }
//        ) {
//            Column(
//                modifier = Modifier
//                    .background(MaterialTheme.colorScheme.background)
//                    .wrapContentSize()
//                    .clip(RoundedCornerShape(10.dp)),
//                verticalArrangement = Arrangement.SpaceAround,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text("Do you want to Disconnect?")
//                Row(
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Button(
//                        modifier = Modifier.width(100.dp),
//                        onClick = {
//                            showDialog.value = false
//                        }
//                    ) {
//                        Text("No")
//                    }
//                    Button(
//                        modifier = Modifier.width(100.dp),
//                        onClick = {
//                            ftpViewModel.disconnect()
//                            navHostController.popBackStack()
//                        }
//                    ) {
//                        Text("Yes")
//                    }
//                }
//            }
//        }
//    }
//}
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


////////////////////////////////////////////////////////////////////////////////////////////////
// SCAFFOLD



//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainScreen(
//    ftpViewModel: FtpViewModel,
//    navHostController: NavHostController
//) {
//    var listOfFilesObserver = ftpViewModel._listOfFiles.observeAsState<List<FTPFile>>()
//    var curDirObserver = ftpViewModel._currentDirectory.observeAsState()
//    var selectedFilesObserver = ftpViewModel._selectedFiles.observeAsState()
//    Column(
//        modifier = Modifier
//            .fillMaxSize(),
//        verticalArrangement = Arrangement.SpaceEvenly,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        TopBar(
//            1.0f,
//            ftpViewModel,
//            listOfFilesObserver,
//            curDirObserver,
//            selectedFilesObserver
//        )
//        CustomContent(
//            ftpViewModel,
//            listOfFilesObserver,
//            curDirObserver,
//            selectedFilesObserver
//        )
//        BottomBar(
//            ftpViewModel,
//            curDirObserver,
//            selectedFilesObserver
//        )
//    }
//    var showDialog = remember { mutableStateOf(false) }
//    BackHandler {
//        Log.i(TAG,"${ftpViewModel.defaultDir.value.toString()}")
//        Log.i(TAG,"${ftpViewModel._currentDirectory.value.toString()}")
//        if (ftpViewModel._currentDirectory.value != ftpViewModel.defaultDir.value) {
//            Log.i(TAG,"BackPressed")
//            ftpViewModel._currentDirectory.setValue(ftpViewModel.removeLastDirectoryFromPath(ftpViewModel._currentDirectory.value.toString()))
//            ftpViewModel.listDir()
//        }
//        else {
//            showDialog.value = true
//        }
//    }
//    CustomDialog(
//        ftpViewModel = ftpViewModel,
//        navHostController = navHostController,
//        showDialog = showDialog,
//        title = "Do You Want to Disconnect?",
//        composable = {
//            Row(
//                horizontalArrangement = Arrangement.SpaceAround,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Button(
//                    modifier = Modifier
//                        .wrapContentSize()
//                        .padding(16.dp),
//                    onClick = {
//                        showDialog.value = false
//                    }
//                ) {
//                    Text("No")
//                }
//                Button(
//                    modifier = Modifier
//                        .wrapContentSize()
//                        .padding(16.dp),
//                    onClick = {
//                        navHostController.popBackStack()
//                        ftpViewModel.disconnect()
//                    }
//                ) {
//                    Text("Yes")
//                }
//            }
//        }
//    )
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TopBar(
//    weight: Float,
//    ftpViewModel: FtpViewModel,
//    listOfFilesObserver: State<List<FTPFile>?>,
//    curDirObserver: State<String?>,
//    selectedFilesObserver: State<List<FTPFile>?>,
//) {
//    CenterAlignedTopAppBar (
//        modifier = Modifier
//            .background(MaterialTheme.colorScheme.background),
//        title = {
//            Text(
//                text = "FTPClient",
//                color = MaterialTheme.colorScheme.primary,
//                style = TextStyle(fontSize = 25.sp),
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                fontWeight = FontWeight.Bold,
//            )
//        },
//        actions = {
//            if (selectedFilesObserver.value!!.isNotEmpty()) {
//                IconButton(onClick = {
//                    if (selectedFilesObserver.value!!.size != listOfFilesObserver.value!!.size) {
//                        listOfFilesObserver.value!!.forEach {
//                            if (!ftpViewModel._selectedFiles.value!!.contains(it)) {
//                                ftpViewModel._selectedFiles.setValue(
//                                    ftpViewModel._selectedFiles.value?.plus(
//                                        listOf(it)
//                                    )
//                                )
//                            }
//                        }
//                    } else {
//                        ftpViewModel._selectedFiles.value = listOf()
//                    }
//                }) {
//                    Icon(
//                        imageVector = Icons.Filled.Check,
//                        tint = MaterialTheme.colorScheme.primary,
//                        contentDescription = "Localized description"
//                    )
//                }
//            }
//            Text(
//                text = "Path : ${curDirObserver.value.toString()}",
//                color = MaterialTheme.colorScheme.primary,
//            )
//        }
//    )
//}
//
//@Composable
//fun CustomContent(
//    ftpViewModel: FtpViewModel,
//    listOfFilesObserver: State<List<FTPFile>?>,
//    curDirObserver: State<String?>,
//    selectedFilesObserver: State<List<FTPFile>?>,
//) {
//    LazyColumn(
//        modifier = Modifier
//    ) {
//        items(
//            count = listOfFilesObserver.value!!.size,
//            key = {
//                    index -> listOfFilesObserver.value!![index].hashCode()
//            },
//            itemContent = { index ->
//                val file = listOfFilesObserver.value!![index]
//                val ftype = file.type
//                val isSelected = selectedFilesObserver.value!!.contains(file)
//                SelectableItem (
//                    modifier = Modifier
//                        .padding(5.dp),
//
//                    selected = true,
//                    title = file.name,
//                    subTitle = if (ftype == 1) {
//                        "Directory"
//                    } else {
//                        "File Size ${file.size.toInt()} MB"
//                    },
//                    type = ftype,
//                    checkState = isSelected,
//                    onClick = {
//                        if (ftype == 1) {
//                            ftpViewModel._currentDirectory.setValue(ftpViewModel._currentDirectory.value + file.name + "/")
//                            ftpViewModel.listDir()
//                            Log.i("Tag", "Directory: ${ftpViewModel._currentDirectory.value.toString()}")
//                            Log.i("Tag", file.name)
//                        } else {
//                            try {
//                                ftpViewModel.downloadFile(remoteFilePath = curDirObserver.value+"/"+file.name,fileName = file.name)
//                            } catch (e: FileAlreadyExistsException) {
//                                Log.i("Tag", "${file.name} already exists")
//                            }
//                        }
//                    })
//                {
//                        b:Boolean ->
//                    when(b) {
//                        false-> ftpViewModel._selectedFiles.setValue(ftpViewModel._selectedFiles.value?.toList()?.plus(listOf(file)))
//                        true-> ftpViewModel._selectedFiles.setValue(ftpViewModel._selectedFiles.value?.toList()?.minus(listOf(file)))
//                    }
//                }
//            }
//        )
//    }
//}
//
//@Composable
//fun BottomBar(
//    ftpViewModel: FtpViewModel,
//    curDirObserver: State<String?>,
//    selectedFilesObserver: State<List<FTPFile>?>,
//) {
//    FloatingActionButton(
//        onClick = {
//            if (selectedFilesObserver.value!!.isEmpty()) {
//                Log.i("Tag", "Uploading")
//
//
//            } else {
//                Log.i("Tag", "Size : ${selectedFilesObserver.value!!.size}")
//                for (file in selectedFilesObserver.value!!.toList()) {
//                    try {
//                        ftpViewModel.downloadFile(remoteFilePath = curDirObserver.value+"/"+file.name,fileName = file.name)
//                    } catch (e: FileAlreadyExistsException) {
//                        Log.i("Tag", "${file.name}")
//                    }
//                }
//            }
//        },
//        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
//        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
//    ) {
//        if (selectedFilesObserver.value!!.isEmpty()) {
//            Icon(Icons.Default.Add, "Upload File")
//        } else {
//            Icon(Icons.Default.ArrowDropDown, "Download File")
//        }
//    }
//}
