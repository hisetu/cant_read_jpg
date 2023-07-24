package com.example.myapplication

import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

data class ImageFile(val uri: Uri, val filePath: String)

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            loadImages()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Greeting("Android")
                        Image(
                            painter = painterResource(id = R.drawable.d5190397_),
                            contentDescription = ""
                        )
                    }
                }
            }
        }

        if (checkSelfPermission("android.permission.READ_MEDIA_IMAGES") ==
            PackageManager.PERMISSION_GRANTED
        ) {
            loadImages()
        } else {
            requestPermissionLauncher.launch("android.permission.READ_MEDIA_IMAGES")
        }
    }

    private fun loadImages() {
        val imageList = mutableListOf<ImageFile>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.RELATIVE_PATH,
                MediaStore.Video.Media.DATA
            )
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
            )
                ?.use {
                    while (it.moveToNext()) {
                        val id = it.getString(0)
                        val filePath = it.getString(3)

                        val imageUri = ContentUris
                            .withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                it.getInt(it.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                                    .toLong()
                            )

                        imageList.add(ImageFile(imageUri, filePath))
                    }
                }
        }

        imageList.forEach {
            Log.d("LLLX", it.filePath)
            try {
                contentResolver.openInputStream(it.uri).use { ins ->
                    ins?.available()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}