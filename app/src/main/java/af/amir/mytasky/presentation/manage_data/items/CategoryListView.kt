package af.amir.mytasky.presentation.manage_data.items

import af.amir.mytasky.R
import af.amir.mytasky.presentation.commonComponent.EmptyState
import af.amir.mytasky.presentation.manage_data.model.CategoryDisplayItem
import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@SuppressLint("MutableCollectionMutableState")
@Composable
fun CategoryListView(
    modifier: Modifier = Modifier,
    categoryList: List<CategoryDisplayItem>,
    onBackClick: () -> Unit,
    onItemClick: (item: CategoryDisplayItem) -> Unit,
) {


    if (categoryList.isEmpty())
        EmptyState(
            modifier = modifier,
            title = stringResource(R.string.list_is_empty),
            subTitle = ""
        )
    else
        ItemsListView(modifier = modifier, items = categoryList) { item ->
            onItemClick(item)
        }



    BackHandler {
        onBackClick()
    }
}


@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}


