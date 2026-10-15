/*
 * Copyright 2015-2026 Ritense BV, the Netherlands.
 *
 * Licensed under EUPL, Version 1.2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ritense.valtimoplugins.freemarker.plugin.documentgenerator

import java.io.ByteArrayOutputStream
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DocumentGeneratorPluginTest {

    @Test
    fun `should generate a pdf`() {
        val plugin = DocumentGeneratorPlugin(mock(), mock(), mock())
        val out = ByteArrayOutputStream()

        plugin.generatePdf("<html><body><p>Hello world</p></body></html>", out)

        assertTrue(out.toString(Charsets.ISO_8859_1).startsWith("%PDF-"))
    }

    @Test
    fun `should write a csv as UTF-8`() {
        val plugin = DocumentGeneratorPlugin(mock(), mock(), mock())
        val out = ByteArrayOutputStream()

        plugin.generateCsv("name\tcity\nZoë\tŁódź\n", out)

        assertEquals("name\tcity\r\nZoë\tŁódź\r\n", out.toString(Charsets.UTF_8))
    }
}
