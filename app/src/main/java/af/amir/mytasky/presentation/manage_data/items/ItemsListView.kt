package af.amir.mytasky.presentation.manage_data.items

import af.amir.mytasky.presentation.manage_data.model.CategoryDisplayItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun ItemsListView(
    modifier: Modifier = Modifier,
    items: List<CategoryDisplayItem>,
    onClick: (item: CategoryDisplayItem) -> Unit,
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp), contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        itemsIndexed(items) { index, item ->
            ItemView(
                titleText = item.label,
                titleTextColor = MaterialTheme.colorScheme.primary,
            ) {
                onClick(item)
            }

        }


    }

}

@Composable
private fun ItemView(
    modifier: Modifier = Modifier,
    titleText: String,
    titleTextColor: Color,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    shape: Shape = RoundedCornerShape(12.dp),
    onClick: () -> Unit,
) {
    Card(
        elevation = CardDefaults.cardElevation(1.5.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = shape,
        onClick = { onClick() }
    ) {
        Box(
            modifier = modifier
                .clip(shape)
                .background(backgroundColor)
                .padding(horizontal = 8.dp, vertical = 12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = titleText,
                    color = titleTextColor,
                    modifier = Modifier
                        .weight(9f)
                        .padding(8.dp),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )

            }

        }
    }

}