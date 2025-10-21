package af.amir.mytasky.presentation.commonComponent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    subTitleTextStyle: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = MaterialTheme.typography.bodySmall.fontSize,
        textAlign = TextAlign.Center
    ),
    titleTextStyle: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = MaterialTheme.typography.titleMedium.fontSize,
        textAlign = TextAlign.Center
    ),
) {

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {

            Text(text = title, style = titleTextStyle)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = subTitle, style = subTitleTextStyle)
        }
    }
}