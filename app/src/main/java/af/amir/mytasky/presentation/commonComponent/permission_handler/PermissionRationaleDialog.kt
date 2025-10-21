package af.amir.mytasky.presentation.commonComponent.permission_handler

import af.amir.mytasky.R
import af.amir.mytasky.presentation.commonComponent.dialog_scaffold.DialogScaffold
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionRationaleDialog(
    showRationaleDialog : Boolean,
    rationaleTitle: String = stringResource(R.string.permission_rationale_title),
    rationaleMessage: String = stringResource(R.string.permission_rationale_message),
    rationalePositiveButtonText: String = stringResource(R.string.permission_rationale_positive),
    rationaleNegativeButtonText: String = stringResource(R.string.permission_rationale_negative),
    onDismiss : ()->Unit,
) {
    val context = LocalContext.current




    DialogScaffold(
        showDialog = showRationaleDialog,
        titleText = rationaleTitle,
        positiveButtonText = rationalePositiveButtonText,
        negativeButtonText = rationaleNegativeButtonText,
        onDismiss = onDismiss,
        onDoneClick = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
            onDismiss()
        }) {
        Box(Modifier.padding(8.dp), contentAlignment = Alignment.Center){
            Text(rationaleMessage, textAlign = TextAlign.Center,modifier = Modifier.padding(8.dp))
        }
    }

}