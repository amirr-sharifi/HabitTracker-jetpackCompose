package af.amir.mytasky.presentation.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import javax.inject.Inject

enum class PermissionStatus {
    GRANTED, NEEDS_REQUEST, NEEDS_RATIONALE, UNKNOWN
}

class PermissionManager @Inject constructor() {

    fun checkPermission(context: Context, permission: String): PermissionStatus {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return PermissionStatus.UNKNOWN
        return when {
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> PermissionStatus.GRANTED

            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                permission
            ) -> PermissionStatus.NEEDS_RATIONALE

            else -> PermissionStatus.NEEDS_REQUEST
        }

    }


}