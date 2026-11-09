package ph.edu.comteq.pesobud.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Format LocalDate for UI display like Sep 10, 2025
fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US)
    return date.format(formatter)
}
