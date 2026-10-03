package com.germanverbmaster.android.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.germanverbmaster.android.domain.model.GrammarTable
import com.germanverbmaster.android.ui.b2practice.B2ContentData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GrammarBottomSheetPreview() {
    MaterialTheme {
        GrammarBottomSheetContent(
            currentPage = -1, // Menu
            totalPages = grammarLogicalPages(B2ContentData.grammarTables).size,
            onPrev = {},
            content = {
                GrammarMenu(
                    tables = B2ContentData.grammarTables,
                    onSelectTable = {}
                )
            }
        )
    }
}

@Composable
fun GrammarBottomSheetContent(
    currentPage: Int, // -1 for menu
    totalPages: Int,
    onPrev: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 32.dp + navBarPadding)
    ) {
        // Header with navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentPage >= 0) {
                IconButton(
                    onClick = onPrev,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    val icon = if (currentPage == 0) Icons.AutoMirrored.Filled.List else Icons.AutoMirrored.Filled.ArrowBack
                    Icon(icon, contentDescription = "Back")
                }
            } else {
                // Placeholder to keep the title centered
                Spacer(Modifier.size(48.dp))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (currentPage == -1) "Grammatik-Themen" else "Grammatik-Referenz",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (currentPage >= 0) {
                    Text(
                        text = "${currentPage + 1} / $totalPages",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Placeholder to keep the title centered
            Spacer(Modifier.size(48.dp))
        }

        Spacer(Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}

@Composable
fun GrammarMenu(
    tables: List<GrammarTable>,
    onSelectTable: (Int) -> Unit
) {
    val menuItems = remember(tables) {
        var tableIndex = 0
        grammarLogicalPages(tables).map { pageTables ->
            val title = if (pageTables.size > 1 && pageTables.first().title.contains("Adjektivdeklination")) {
                "Adjektivdeklination"
            } else {
                pageTables.first().title
            }
            val item = title to tableIndex
            tableIndex += pageTables.size
            item
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        itemsIndexed(menuItems) { index, item ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectTable(item.second) }
            ) {
                ListItem(
                    headlineContent = { 
                        Text("${index + 1}. ${item.first}", fontWeight = FontWeight.SemiBold)
                    },
                    trailingContent = {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
) {
    val tables = B2ContentData.grammarTables
    
    val logicalPages = remember(tables) {
        grammarLogicalPages(tables)
    }

    val pagerState = rememberPagerState(pageCount = { logicalPages.size + 1 })
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight()
    ) {
        GrammarBottomSheetContent(
            currentPage = pagerState.currentPage - 1,
            totalPages = logicalPages.size,
            onPrev = {
                scope.launch {
                    pagerState.animateScrollToPage(0)
                }
            },
            content = {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top,
                    userScrollEnabled = false
                ) { page ->
                    if (page == 0) {
                        GrammarMenu(
                            tables = tables,
                            onSelectTable = { logicalIndex ->
                                var currentPageIndex = 0
                                var currentTableIndex = 0
                                for (i in logicalPages.indices) {
                                    if (currentTableIndex == logicalIndex) {
                                        currentPageIndex = i
                                        break
                                    }
                                    currentTableIndex += logicalPages[i].size
                                }
                                scope.launch {
                                    pagerState.animateScrollToPage(currentPageIndex + 1)
                                }
                            }
                        )
                    } else {
                        val pageTables = logicalPages[page - 1]
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(pageTables) { table ->
                                GrammarTableCard(table)
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun GrammarTableCard(table: GrammarTable) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(table.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(table.note, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(Modifier.height(10.dp))
            
            if (table.useListLayout) {
                // List-based layout for long content
                table.rows.forEachIndexed { rowIndex, row ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        row.forEachIndexed { colIndex, cell ->
                            Row(modifier = Modifier.padding(vertical = 1.dp)) {
                                if (colIndex < table.headers.size) {
                                    Text(
                                        text = "${table.headers[colIndex]}: ",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        modifier = Modifier.width(100.dp)
                                    )
                                }
                                Text(
                                    text = cell,
                                    style = if (colIndex == 0) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                                    fontWeight = if (colIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (colIndex > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    if (rowIndex < table.rows.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), 
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            } else {
                // Standard table layout
                Row(Modifier.fillMaxWidth()) {
                    table.headers.forEach { h ->
                        Text(h, fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(1f))
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                
                table.rows.forEach { row ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                        row.forEachIndexed { index, cell ->
                            Text(
                                text = cell,
                                style = if (index == 0) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                                fontWeight = if (index > 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (index > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            
            if (table.rule.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                GrammarReferenceSection(
                    title = "Regel",
                    items = listOf(table.rule),
                    initiallyExpanded = true
                )
            }

            if (table.examples.isNotEmpty()) {
                GrammarReferenceSection(
                    title = "Beispiele",
                    items = table.examples,
                    initiallyExpanded = false,
                    italic = true
                )
            }

            if (table.mistakes.isNotEmpty()) {
                GrammarReferenceSection(
                    title = "Typische Fehler",
                    items = table.mistakes,
                    initiallyExpanded = false
                )
            }

            table.extraSections.forEach { section ->
                GrammarReferenceSection(
                    title = section.title,
                    items = section.items,
                    initiallyExpanded = false
                )
            }
        }
    }
}

private fun grammarLogicalPages(tables: List<GrammarTable>): List<List<GrammarTable>> {
    val pages = mutableListOf<List<GrammarTable>>()
    var index = 0
    while (index < tables.size) {
        val table = tables[index]
        if (table.title.contains("Adjektivdeklination")) {
            val group = tables
                .drop(index)
                .takeWhile { it.title.contains("Adjektivdeklination") }
            pages.add(group)
            index += group.size
        } else {
            pages.add(listOf(table))
            index++
        }
    }
    return pages
}

@Composable
private fun GrammarReferenceSection(
    title: String,
    items: List<String>,
    initiallyExpanded: Boolean,
    italic: Boolean = false
) {
    var expanded by remember(title, items) { mutableStateOf(initiallyExpanded) }

    Column(modifier = Modifier.padding(top = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse $title" else "Expand $title",
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                items.forEach { item ->
                    Text(
                        text = "• $item",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
