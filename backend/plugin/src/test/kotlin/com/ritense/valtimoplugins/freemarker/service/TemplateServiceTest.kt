/*
 * Copyright 2015-2023 Ritense BV, the Netherlands.
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

package com.ritense.valtimoplugins.freemarker.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.ritense.document.domain.impl.JsonDocumentContent
import com.ritense.document.domain.impl.JsonSchemaDocument
import com.ritense.document.domain.impl.JsonSchemaDocumentDefinitionId
import com.ritense.valtimo.contract.case_.CaseDefinitionId
import com.ritense.valtimoplugins.freemarker.autoconfiguration.TemplateAutoConfiguration
import com.ritense.valtimoplugins.freemarker.repository.JsonSchemaDocumentRepositoryStreaming
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.stream.Stream
import kotlin.test.assertEquals

class TemplateServiceTest {
    private lateinit var jsonSchemaDocumentRepositoryStreaming: JsonSchemaDocumentRepositoryStreaming
    private lateinit var templateService: TemplateService
    private lateinit var document: JsonSchemaDocument

    @BeforeEach
    fun setUp() {
        jsonSchemaDocumentRepositoryStreaming = mock()
        templateService =
            TemplateService(
                templateRepository = mock(),
                objectMapper = ObjectMapper(),
                valueResolverService = mock(),
                freemarkerConfiguration = TemplateAutoConfiguration().freemarkerConfiguration(),
                caseDefinitionChecker = mock(),
                buildingBlockDefinitionChecker = mock(),
                jsonSchemaDocumentRepositoryStreaming = jsonSchemaDocumentRepositoryStreaming,
            )
        document = documentOf("""{"firstName":"Klaas"}""")
    }

    @Test
    fun `should render a template that lists docs`() {
        // Build the documents up front: creating mocks inside whenever(..) nests stubbing.
        val documents = Stream.of(documentOf("""{"firstName":"Anna"}"""), documentOf("""{"firstName":"Bram"}"""))
        whenever(jsonSchemaDocumentRepositoryStreaming.streamAllByBluePrintKey(BLUEPRINT_KEY)).thenReturn(documents)

        val result =
            templateService.generate(
                templateName = "docs.csv",
                templateContent = "<#list docs as doc>\${doc.firstName}\n</#list>",
                document = document,
            )

        assertEquals("Anna\nBram\n", result)
        verify(jsonSchemaDocumentRepositoryStreaming).streamAllByBluePrintKey(BLUEPRINT_KEY)
    }

    @Test
    fun `should not stream documents when the template does not reference docs`() {
        val result =
            templateService.generate(
                templateName = "doc.txt",
                templateContent = "\${doc.firstName}",
                document = document,
            )

        assertEquals("Klaas", result)
        verify(jsonSchemaDocumentRepositoryStreaming, never()).streamAllByBluePrintKey(any())
    }

    private fun documentOf(content: String): JsonSchemaDocument =
        mock<JsonSchemaDocument>().also {
            whenever(it.definitionId()).thenReturn(DEFINITION_ID)
            whenever(it.content()).thenReturn(JsonDocumentContent(content))
        }

    companion object {
        private val DEFINITION_ID =
            JsonSchemaDocumentDefinitionId.forCase("profile", CaseDefinitionId("profile", "1.0.0"))
        private val BLUEPRINT_KEY = DEFINITION_ID.blueprintId().blueprintKey
    }
}
