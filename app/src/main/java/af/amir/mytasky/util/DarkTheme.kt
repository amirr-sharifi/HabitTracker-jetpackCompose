package af.amir.mytasky.util

import af.amir.mytasky.R
import androidx.annotation.StringRes

enum class DarkTheme(
    @StringRes
    val stringResId: Int,
) {
    On(R.string.on), Off(R.string.off), System(R.string.system),Default(0)

}