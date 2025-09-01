package ir.amirroid.clipshare.design_system.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableSection(
    title: String,
    modifier: Modifier = Modifier,
    expandedByDefault: Boolean = false,
    icons: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var expanded by rememberSaveable {
        mutableStateOf(expandedByDefault)
    }
    val rotate by animateFloatAsState(if (expanded) 0f else 180f)

    Card(modifier) {
        AppListItem(
            headlineContent = {
                AppText(title)
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    icons?.invoke(this)
                    AppIconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowUp,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotate)
                        )
                    }
                }
            }
        )
        AnimatedVisibility(expanded) {
            Box(
                Modifier.background(MaterialTheme.colorScheme.surface).fillMaxWidth()
            ) {
                content.invoke()
            }
        }
    }
}