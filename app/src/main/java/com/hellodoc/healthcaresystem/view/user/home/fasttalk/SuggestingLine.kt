package com.hellodoc.healthcaresystem.view.user.home.fasttalk

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.SuggestLine

@Composable
fun ConversationsLine(onChoice: (String) -> Unit){
    Divider(
        color = MaterialTheme.colorScheme.secondaryContainer,
        thickness = 1.dp,
    )
    Column (
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gợi ý",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Spacer(modifier = Modifier.width(16.dp))
            }
            items(conversations) { conversation ->
                ConversationLine(conversation, onChoice)
            }

        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider(
            color = MaterialTheme.colorScheme.secondaryContainer,
            thickness = 1.dp,
        )
    }
}

@Composable
fun ConversationLine(conversation: SuggestLine,onChoice: (String) -> Unit){
    Text(
        text = conversation.content,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(6.dp))
            .clickable {
                onChoice(conversation.content)
            }
            .background(color = MaterialTheme.colorScheme.background)
            .border( 1.dp, MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(6.dp))
            .padding(15.dp)



    )
}

val conversations = listOf(
    SuggestLine(
        content = "Tôi ăn cơm rồi",
        id = "1"
    ),
    SuggestLine(
        content = "Tôi chưa ăn cơm",
        id = "2"
    ),
    SuggestLine(
        content = "Tôi ăn hồi nãy rồi",
        id = "3"
    ),
)