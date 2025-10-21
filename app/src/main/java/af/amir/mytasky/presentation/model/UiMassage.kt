package af.amir.mytasky.presentation.model

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

sealed class UiMessage {
    data class Dynamic(val message: String) : UiMessage()
    data class Resource(@StringRes val messageResId: Int) : UiMessage()

    @Composable
    fun asString() : String {
        return when(this){
            is Dynamic -> message
            is Resource -> stringResource(messageResId)
        }
    }

    fun asString(context : Context) : String {
        return when(this){
            is Dynamic -> message
            is Resource -> context.getString(messageResId)
        }
    }


}