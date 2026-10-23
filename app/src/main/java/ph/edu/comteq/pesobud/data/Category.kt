package ph.edu.comteq.pesobud.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ph.edu.comteq.pesobud.ui.theme.CategoryFoodBg
import ph.edu.comteq.pesobud.ui.theme.CategoryFoodFg
import ph.edu.comteq.pesobud.ui.theme.CategoryLoadBg
import ph.edu.comteq.pesobud.ui.theme.CategoryLoadFg
import ph.edu.comteq.pesobud.ui.theme.CategoryOthersBg
import ph.edu.comteq.pesobud.ui.theme.CategoryOthersFg
import ph.edu.comteq.pesobud.ui.theme.CategorySchoolBg
import ph.edu.comteq.pesobud.ui.theme.CategorySchoolFg
import ph.edu.comteq.pesobud.ui.theme.CategoryTransportBg
import ph.edu.comteq.pesobud.ui.theme.CategoryTransportFg

enum class Category(
    val displayName: String,
    val icon: ImageVector,
    val color: Color,
    val backgroundColor: Color
) {
    FOOD("Food", Icons.Default.Restaurant, CategoryFoodFg, CategoryFoodBg),
    TRANSPORT("Transport", Icons.Default.DirectionsBus, CategoryTransportFg, CategoryTransportBg),
    SCHOOL("School", Icons.Default.School, CategorySchoolFg, CategorySchoolBg),
    LOAD_DATA("Load/Data", Icons.Default.Smartphone, CategoryLoadFg, CategoryLoadBg),
    OTHERS("Others", Icons.Default.Category, CategoryOthersFg, CategoryOthersBg);

    companion object {
        fun fromDisplayName(name: String): Category {
            return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: OTHERS
        }
    }
}
