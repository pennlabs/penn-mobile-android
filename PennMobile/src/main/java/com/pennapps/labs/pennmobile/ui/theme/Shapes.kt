import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),   // Inner radius
    medium = RoundedCornerShape(16.dp), // Outer radius / container
    large = RoundedCornerShape(0.dp)
)
