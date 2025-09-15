package ir.amirroid.clipshare

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val notificationPermissionRequestCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        checkNotificationPermission()

        setContent {
            App()
        }
    }

//    @Composable
//    fun ShowEnableKeyboardDialog(
//        onDismiss: () -> Unit
//    ) {
//        AlertDialog(
//            onDismissRequest = { onDismiss() },
//            title = { AppText("Enable Keyboard") },
//            text = {
//                AppText("To allow the service to run in the background, please activate the Clipshare keyboard in your device settings.")
//            },
//            confirmButton = {
//                TextButton(onClick = {
//                    val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
//                    startActivity(intent)
//                    onDismiss()
//                }) {
//                    Text("Go to Settings")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { onDismiss() }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    notificationPermissionRequestCode
                )
            } else {
                ClipShareServiceManager.startServiceIfNotStarted(this)
            }
        } else {
            ClipShareServiceManager.startServiceIfNotStarted(this)
        }
    }

//    private fun checkKeyboardEnable(): Boolean {
//        val enabledInputMethods = Settings.Secure.getString(
//            contentResolver,
//            Settings.Secure.ENABLED_INPUT_METHODS
//        ) ?: return false
//
//        Logger.withTag("sadsadas").d { enabledInputMethods }
//
//        val myKeyboardId = "$packageName/.ClipshareInputService"
//
//        return enabledInputMethods.split(":").contains(myKeyboardId)
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == notificationPermissionRequestCode) {
            val granted = grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                Toast.makeText(
                    this,
                    "Notification permission is required for the app to run",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } else {
                ClipShareServiceManager.startServiceIfNotStarted(this)
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}