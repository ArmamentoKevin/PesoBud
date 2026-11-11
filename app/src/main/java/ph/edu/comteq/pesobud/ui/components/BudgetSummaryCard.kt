package ph.edu.comteq.pesobud.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ph.edu.comteq.pesobud.data.BudgetStatus
import ph.edu.comteq.pesobud.data.BudgetSummary
import ph.edu.comteq.pesobud.data.formatMoney
import ph.edu.comteq.pesobud.ui.theme.GreenLightBg
import ph.edu.comteq.pesobud.ui.theme.GreenPrimary
import ph.edu.comteq.pesobud.ui.theme.TextPrimary
import ph.edu.comteq.pesobud.ui.theme.TextSecondary

@Composable
fun BudgetSummaryCard(
    budgetSummary: BudgetSummary,
    modifier: Modifier = Modifier
) {
    val isNoBudget = budgetSummary.status == BudgetStatus.NO_BUDGET
    val progress = if (budgetSummary.budget > 0) (budgetSummary.spent / budgetSummary.budget).toFloat().coerceIn(0f, 1f) else 0f

    // Dynamic progress bar color based on budget status
    val progressColor = when (budgetSummary.status) {
        BudgetStatus.OVER_BUDGET -> Color(0xFFEF4444)
        BudgetStatus.NEAR_LIMIT -> Color(0xFFF59E0B)
        else -> GreenPrimary
    }

    Column(modifier = modifier.fillMaxWidth()) {
        BudgetWarningBanner(budgetSummary = budgetSummary)
        if (budgetSummary.status == BudgetStatus.NEAR_LIMIT || budgetSummary.status == BudgetStatus.OVER_BUDGET) {
            Spacer(modifier = Modifier.height(12.dp))
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GreenLightBg),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Monthly Budget",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = GreenPrimary
                    )
                    Text(
                        text = if (isNoBudget) "No budget set" else formatMoney(budgetSummary.budget),
                        fontSize = if (isNoBudget) 15.sp else 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    color = progressColor,
                    trackColor = Color(0xFFC8E6C9),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Spent: ${formatMoney(budgetSummary.spent)}",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    val remainingText = if (isNoBudget) {
                        formatMoney(0.0)
                    } else if (budgetSummary.status == BudgetStatus.OVER_BUDGET) {
                        "Over by ${formatMoney(-budgetSummary.remaining)}"
                    } else {
                        "Remaining: ${formatMoney(budgetSummary.remaining)}"
                    }

                    Text(
                        text = remainingText,
                        fontSize = 13.sp,
                        color = if (budgetSummary.status == BudgetStatus.OVER_BUDGET) Color(0xFFEF4444) else GreenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
