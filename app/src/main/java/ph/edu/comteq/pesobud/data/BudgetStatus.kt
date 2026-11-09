package ph.edu.comteq.pesobud.data

// Enum for budget health status
enum class BudgetStatus {
    NO_BUDGET,
    UNDER_BUDGET,
    NEAR_LIMIT,
    OVER_BUDGET
}

// Data holder for calculated budget summary
data class BudgetSummary(
    val budget: Double,
    val spent: Double,
    val remaining: Double,
    val percentageUsed: Int,
    val status: BudgetStatus
)

// Compute budget status and percentage from spent and budget amount
fun calculateBudgetSummary(spent: Double, budgetAmount: Double?): BudgetSummary {
    val budget = budgetAmount ?: 0.0
    if (budget <= 0.0) {
        return BudgetSummary(
            budget = 0.0,
            spent = spent,
            remaining = 0.0,
            percentageUsed = 0,
            status = BudgetStatus.NO_BUDGET
        )
    }

    val remaining = budget - spent
    val percentage = ((spent / budget) * 100).toInt()

    val status = when {
        percentage >= 100 -> BudgetStatus.OVER_BUDGET
        percentage >= 80 -> BudgetStatus.NEAR_LIMIT
        else -> BudgetStatus.UNDER_BUDGET
    }

    return BudgetSummary(
        budget = budget,
        spent = spent,
        remaining = remaining,
        percentageUsed = percentage,
        status = status
    )
}
