package ph.edu.comteq.pesobud.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.edu.comteq.pesobud.data.BudgetStatus
import ph.edu.comteq.pesobud.data.BudgetSummary

@Composable
fun BudgetWarningBanner(
    budgetSummary: BudgetSummary,
    modifier: Modifier = Modifier
) {
    if (budgetSummary.status == BudgetStatus.NEAR_LIMIT) {
        // Amber warning banner for 80% or more
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFEF3C7))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color(0xFFD97706),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "You've used ${budgetSummary.percentageUsed}% of your budget",
                color = Color(0xFF92400E),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    } else if (budgetSummary.status == BudgetStatus.OVER_BUDGET) {
        // Red warning banner for 100% or more
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFEE2E2))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color(0xFFDC2626),
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "You've gone over your monthly budget",
                color = Color(0xFF991B1B),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
