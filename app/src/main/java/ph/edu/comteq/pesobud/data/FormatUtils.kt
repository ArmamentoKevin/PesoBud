package ph.edu.comteq.pesobud.data

import java.util.Locale

// Format amount to standard money string like ₱ 1,234.50
fun formatMoney(amount: Double): String {
    return "₱ ${String.format(Locale.US, "%,.2f", amount)}"
}
