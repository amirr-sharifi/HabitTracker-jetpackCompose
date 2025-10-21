package af.amir.mytasky.presentation.manage_data

import af.amir.mytasky.R
import af.amir.mytasky.presentation.manage_data.items.CategoryListView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ManageDataScreen(
    modifier: Modifier = Modifier,
    onFinishScreen: () -> Unit,
    onItemClick:(id :Long)->Unit,
    viewModel: ManageDataViewModel = hiltViewModel(),
) {
    val displayItems by viewModel.displayItems.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect{
            when(it){
                MangeDataEffect.FinishScreen -> onFinishScreen()
                is MangeDataEffect.NavigateHabitList -> onItemClick(it.id)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleHeader()
        Spacer(modifier = Modifier.height(12.dp))


        Column(
            modifier = modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(topEnd = 20.dp, topStart = 20.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            CategoryListView(
                categoryList = displayItems,
                onBackClick = {viewModel.onEvents(ManageDataEvent.OnBack)},
                onItemClick = {viewModel.onEvents(ManageDataEvent.OnItemClick(it))})

        }
    }

}

@Composable
fun TitleHeader() {
    Text(
        modifier = Modifier.padding(top = 16.dp),
        text = stringResource(R.string.manage_your_data),
        textAlign = TextAlign.Center,
        fontSize = MaterialTheme.typography.titleLarge.fontSize,
        fontWeight = FontWeight.Bold
    )

}




