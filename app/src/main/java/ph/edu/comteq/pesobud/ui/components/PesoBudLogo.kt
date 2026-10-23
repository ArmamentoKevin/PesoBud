package ph.edu.comteq.pesobud.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
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
import ph.edu.comteq.pesobud.ui.theme.GreenPrimary

@Composable
fun PesoBudLogo(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Sprout / Leaf on top
        Icon(
            imageVector = Icons.Default.Eco,
            contentDescription = null,
            tint = GreenPrimary,
            modifier = Modifier
                .size(44.dp)
                .offset(x = 16.dp, y = 10.dp)
        )

        // Wallet shape
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 110.dp, height = 80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(GreenPrimary)
        ) {
            // Clasp/badge circle with Peso symbol
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF81C784))
            ) {
                Text(
                    text = "₱",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "PesoBud",
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Expense Tracker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B)
        )
    }
}
