import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

@Composable
fun ForceDefaultSystemSettings(
  content: @Composable () -> Unit
) {

  val stableDensity = remember {
    val defaultDensityDpi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      DisplayMetrics.DENSITY_DEVICE_STABLE
    } else {

      Resources.getSystem().displayMetrics.densityDpi
    }

    defaultDensityDpi / 160f
  }

  val fixedDensity = remember(stableDensity) {
    Density(density = stableDensity, fontScale = 1f)
  }

  CompositionLocalProvider(
    LocalDensity provides fixedDensity,
  ) {
    content()
  }
}