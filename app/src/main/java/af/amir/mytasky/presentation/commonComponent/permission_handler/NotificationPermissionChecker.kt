package af.amir.mytasky.presentation.commonComponent.permission_handler

import af.amir.mytasky.presentation.util.PermissionManager
import af.amir.mytasky.presentation.util.PermissionStatus
import android.Manifest
import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun NotificationPermissionChecker(
    permission: String = Manifest.permission.POST_NOTIFICATIONS,
    onGrantedPermission: (idGranted: Boolean) -> Unit,
) {

    var showRationaleDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onGrantedPermission(isGranted)
        }

    LaunchedEffect(Unit){
        val permissionStatus = PermissionManager().checkPermission(activity, permission)

        when (permissionStatus) {
            PermissionStatus.NEEDS_REQUEST -> permissionLauncher.launch(permission)
            PermissionStatus.NEEDS_RATIONALE -> showRationaleDialog = true
            else -> onGrantedPermission(true)
        }
    }


    PermissionRationaleDialog(showRationaleDialog = showRationaleDialog) {
        showRationaleDialog = false
        onGrantedPermission(false)
    }

}