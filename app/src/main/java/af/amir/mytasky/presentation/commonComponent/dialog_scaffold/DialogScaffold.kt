package af.amir.mytasky.presentation.commonComponent.dialog_scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun DialogScaffold(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onDoneClick:()->Unit,
    titleText : String,
    positiveButtonText : String,
    negativeButtonText : String,
    content : @Composable ()->Unit
) {

    val colorPrimary = MaterialTheme.colorScheme.primary
    val colorSurface = MaterialTheme.colorScheme.surface
    val colorOnSurface = MaterialTheme.colorScheme.onSurface
    val colorSecondary = MaterialTheme.colorScheme.secondary.copy(0.5f)
    val colorOnSecondary = MaterialTheme.colorScheme.onSecondary
    val colorOnPrimary = MaterialTheme.colorScheme.onPrimary



    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .width(300.dp)
                                .background(colorSurface),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = titleText,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = colorOnSurface
                                )
                            }

                            content()

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onDoneClick()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = colorPrimary),
                                    modifier = Modifier
                                        .weight(1f),
                                ) {
                                    Text(text =positiveButtonText, color = colorOnPrimary)
                                }
                                Button(
                                    onClick = {
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = colorSecondary),
                                    modifier = Modifier
                                        .weight(1f),
                                ) {
                                    Text(text = negativeButtonText, color = colorOnSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}