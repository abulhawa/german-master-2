package com.germanverbmaster.android.data.repository

import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readLines

class BundledWordlistConsistencyTest {

    @Test
    fun `reflexive verbs use consistent sich marker and prefix`() {
        val lines = wordlistPath().readLines()
        assertTrue("CSV should include a header", lines.isNotEmpty())

        val emptyPrefixButWordHasSich = lines.withIndex().filter { (_, line) ->
            line.startsWith(",(sich) ") || line.startsWith(",\"(sich) ")
        }
        val prefixSichButWordMissingMarker = lines.withIndex().filter { (_, line) ->
            line.startsWith("sich,") &&
                !line.startsWith("sich,(sich) ") &&
                !line.startsWith("sich,\"(sich) ")
        }

        assertTrue(
            "Found rows with '(sich)' word marker but empty prefix: ${previewRows(emptyPrefixButWordHasSich)}",
            emptyPrefixButWordHasSich.isEmpty(),
        )
        assertTrue(
            "Found rows with prefix 'sich' but missing '(sich)' word marker: ${previewRows(prefixSichButWordMissingMarker)}",
            prefixSichButWordMissingMarker.isEmpty(),
        )
    }

    private fun wordlistPath(): Path {
        val rootDataPath = Paths.get("..", "data", "b2_wortliste.csv")
        if (Files.exists(rootDataPath)) return rootDataPath

        val subDataPath = Paths.get("data", "b2_wortliste.csv")
        if (Files.exists(subDataPath)) return subDataPath

        val directModulePath = Paths.get("src", "main", "assets", "b2_wortliste.csv")
        if (Files.exists(directModulePath)) return directModulePath

        val rootPath = Paths.get("app", "src", "main", "assets", "b2_wortliste.csv")
        if (Files.exists(rootPath)) return rootPath

        error("Could not locate b2_wortliste.csv from current working directory (${System.getProperty("user.dir")})")
    }

    private fun previewRows(rows: List<IndexedValue<String>>): String =
        rows.take(3).joinToString(" | ") { "${it.index + 1}: ${it.value}" }
}

