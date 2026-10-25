package com.germanverbmaster.android.learner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp

/** Decorative marks; the adjacent button label names the service for accessibility. */
@Composable
fun LoginProviderIcon(provider: String) {
    val color=MaterialTheme.colorScheme.onSurface
    Canvas(Modifier.size(20.dp)) {
        scale(size.width/24f,size.height/24f,pivot=androidx.compose.ui.geometry.Offset.Zero) {
            if(provider=="google") {
                drawPath(PathParser().parsePathString("M21.6 12.227c0-.639-.057-1.252-.163-1.84H12v3.48h5.367a4.588 4.588 0 0 1-1.99 3.009v2.504h3.223c1.885-1.737 2.98-4.293 2.98-7.153Z").toPath(),color)
                drawPath(PathParser().parsePathString("M12 22c2.7 0 4.967-.891 6.622-2.42l-3.223-2.504c-.896.6-2.044.955-3.399.955-2.614 0-4.829-1.767-5.618-4.143H3.04v2.604A9.999 9.999 0 0 0 12 22Z").toPath(),color)
                drawPath(PathParser().parsePathString("M6.382 13.888A6.004 6.004 0 0 1 6.07 12c0-.654.112-1.288.312-1.888V7.508H3.04A9.999 9.999 0 0 0 2 12c0 1.602.384 3.118 1.04 4.492l3.342-2.604Z").toPath(),color)
                drawPath(PathParser().parsePathString("M12 5.818c1.467 0 2.784.505 3.817 1.498l2.863-2.863C16.965 2.99 14.7 2 12 2a9.999 9.999 0 0 0-8.96 5.508l3.342 2.604C6.771 7.585 8.986 5.818 12 5.818Z").toPath(),color)
            } else {
                listOf(3f to 3f,13f to 3f,3f to 13f,13f to 13f).forEach { (x,y) ->
                    drawRect(color,androidx.compose.ui.geometry.Offset(x,y),androidx.compose.ui.geometry.Size(8f,8f))
                }
            }
        }
    }
}
