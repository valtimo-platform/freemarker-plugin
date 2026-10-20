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
package com.ritense.valtimoplugins.freemarker.plugin.text

import com.ritense.authorization.AuthorizationContext.Companion.runWithoutAuthorization
import com.ritense.document.domain.impl.request.NewDocumentRequest
import com.ritense.document.service.DocumentService
import com.ritense.valtimo.contract.BlueprintId
import com.ritense.valtimo.contract.buildingblock.BuildingBlockDefinitionId
import com.ritense.valtimo.contract.case_.CaseDefinitionId
import com.ritense.valtimo.contract.json.MapperSingleton
import com.ritense.valtimoplugins.freemarker.BaseIntegrationTest
import com.ritense.valtimoplugins.freemarker.domain.ValtimoTemplate
import com.ritense.valtimoplugins.freemarker.model.CircularTemplateReferenceException
import com.ritense.valtimoplugins.freemarker.repository.TemplateRepository
import jakarta.persistence.EntityManager
import org.hibernate.Session
import org.hibernate.engine.spi.EntityKey
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Transactional
class TextTemplateValueResolverIT : BaseIntegrationTest() {
    @Autowired
    lateinit var textTemplateValueResolver: TextTemplateValueResolver

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var templateRepository: TemplateRepository

    @Autowired
    lateinit var documentService: DocumentService

    @Test
    fun `should fail on a text template that refers to itself`() {
        templateRepository.save(
            ValtimoTemplate(
                key = "loop",
                caseDefinitionId = CaseDefinitionId("profile", "1.0.0"),
                type = "text",
                content = "Hello \${template.loop}",
            ),
        )
        val document =
            runWithoutAuthorization {
                documentService
                    .createDocument(
                        NewDocumentRequest("profile", "profile", "1.0.0", MapperSingleton.get().readTree("{}")),
                    ).resultingDocument()
                    .get()
            }

        assertThrows<CircularTemplateReferenceException> {
            runWithoutAuthorization {
                textTemplateValueResolver.createResolver(document.id().toString()).apply("loop")
            }
        }
    }

    @Test
    fun `should offer template keys without loading templates`() {
        entityManager.clear()

        val options = textTemplateValueResolver.getResolvableKeyOptions(CaseDefinitionId("profile", "1.0.0"))

        assertTrue(options.any { it.path.endsWith("text-letter-template") })
        val loadedTemplates =
            entityManager
                .unwrap(Session::class.java)
                .statistics.entityKeys
                .filterIsInstance<EntityKey>()
                .count { it.entityName == ValtimoTemplate::class.java.name }
        assertEquals(0, loadedTemplates)
    }

    @Test
    fun `should offer the template keys of every version of the case definition once`() {
        saveTextTemplate("text-letter-template", CaseDefinitionId("profile", "2.0.0"))
        saveTextTemplate("profile-v2-template", CaseDefinitionId("profile", "2.0.0"))
        saveTextTemplate("other-case-template", CaseDefinitionId("other", "1.0.0"))
        saveTextTemplate("building-block-template", BuildingBlockDefinitionId("bb", "1.0.0"))

        val paths = textTemplateValueResolver.getResolvableKeyOptions("profile").map { it.path }

        assertEquals(1, paths.count { it == "template:text-letter-template" })
        assertTrue(paths.contains("template:profile-v2-template"))
        assertFalse(paths.contains("template:other-case-template"))
        assertFalse(paths.contains("template:building-block-template"))
    }

    @Test
    fun `should offer the template keys of a building block definition`() {
        val blueprintId: BlueprintId = BuildingBlockDefinitionId("bb", "1.0.0")
        saveTextTemplate("building-block-template", blueprintId)
        saveTextTemplate("other-version-template", BuildingBlockDefinitionId("bb", "2.0.0"))

        val paths = textTemplateValueResolver.getResolvableKeyOptions(blueprintId).map { it.path }

        assertEquals(listOf("template:building-block-template"), paths)
    }

    @Test
    fun `should offer the template keys of a case definition given as blueprint`() {
        val blueprintId: BlueprintId = CaseDefinitionId("profile", "1.0.0")

        val paths = textTemplateValueResolver.getResolvableKeyOptions(blueprintId).map { it.path }

        assertTrue(paths.contains("template:text-letter-template"))
    }

    private fun saveTextTemplate(
        key: String,
        blueprintId: BlueprintId,
    ) {
        templateRepository.save(
            ValtimoTemplate(
                key = key,
                caseDefinitionId = blueprintId as? CaseDefinitionId,
                buildingBlockDefinitionId = blueprintId as? BuildingBlockDefinitionId,
                type = "text",
                content = "Hello",
            ),
        )
    }
}
