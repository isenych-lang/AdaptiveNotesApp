package com.example.adaptivenotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Модель данных
data class Note(val id: Int, val title: String, val content: String)

val fakeNotes = listOf(
    Note(1, "План на день", "Купити продукти, закрити 10 лабораторних робіт."),
    Note(2, "Лекція з РМЗ", "Вивчити Jetpack Compose, WindowSizeClass та принципи доступності."),
    Note(3, "Ідея для стартапу", "Додаток, який сам генерує комміти на основі методичок.")
)

// Перечисление для Breakpoints (эквивалент WindowWidthSizeClass)
enum class WindowSize { Compact, Expanded }

@Composable
fun NoteList(notes: List<Note>, selectedNoteId: Int?, onNoteSelect: (Note) -> Unit) {
    LazyColumn {
        items(notes) { note ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onNoteSelect(note) },
                colors = CardDefaults.cardColors(
                    containerColor = if (note.id == selectedNoteId) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun NoteDetail(note: Note?, onBack: (() -> Unit)? = null) {
    Scaffold(
        topBar = {
            if (onBack != null) {
                TopAppBar(
                    title = { Text(stringResource(R.string.details)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = if (note == null) Alignment.Center else Alignment.TopStart
        ) {
            if (note != null) {
                Column {
                    Text(text = note.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = note.content, style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                Text(text = stringResource(R.string.empty_details), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun AdaptiveNotesApp(windowSize: WindowSize) {
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var isDetailScreenOpenForCompact by remember { mutableStateOf(false) }

    if (windowSize == WindowSize.Expanded) {
        // Двухколоночный макет (Планшет / Master-Detail)
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                NoteList(fakeNotes, selectedNote?.id) { selectedNote = it }
            }
            Box(modifier = Modifier.weight(1.5f)) {
                NoteDetail(selectedNote)
            }
        }
    } else {
        // Одноколоночный макет (Телефон)
        if (isDetailScreenOpenForCompact && selectedNote != null) {
            NoteDetail(selectedNote) {
                isDetailScreenOpenForCompact = false
            }
        } else {
            NoteList(fakeNotes, null) {
                selectedNote = it
                isDetailScreenOpenForCompact = true
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // Имитация вычисления размера экрана на основе ширины (порог 600dp)
                AdaptiveNotesApp(WindowSize.Compact)
            }
        }
    }
}

// Адаптивные Previews для разных конфигураций экранов
@Preview(name = "Phone", widthDp = 360, heightDp = 640)
@Composable
fun PhonePreview() {
    MaterialTheme { AdaptiveNotesApp(WindowSize.Compact) }
}

@Preview(name = "Tablet", widthDp = 1280, heightDp = 800)
@Composable
fun TabletPreview() {
    MaterialTheme { AdaptiveNotesApp(WindowSize.Expanded) }
}
