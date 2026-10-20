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

package com.ritense.valtimoplugins.freemarker.service

import com.ritense.authorization.AuthorizationContext.Companion.runWithoutAuthorization
import com.ritense.exporter.ExportService
import com.ritense.exporter.request.CaseDefinitionExportRequest
import com.ritense.importer.ImportService
import com.ritense.valtimo.contract.case_.CaseDefinitionId
import com.ritense.valtimoplugins.freemarker.BaseIntegrationTest
import com.ritense.valtimoplugins.freemarker.domain.ValtimoTemplate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@Transactional
class TemplateImportExportIT : BaseIntegrationTest() {
    @Autowired
    lateinit var exportService: ExportService

    @Autowired
    lateinit var importService: ImportService

    @Autowired
    lateinit var templateService: TemplateService

    @Test
    fun `should import the templates of an exported case definition`() {
        runWithoutAuthorization {
            val caseDefinitionId = CaseDefinitionId("profile", "1.0.0")
            val export = exportService.export(CaseDefinitionExportRequest(caseDefinitionId)).toByteArray()

            val importedId = importService.import(export.inputStream(), emptyList(), "profile-copy", "Copy")

            assertEquals(
                templateService.findTemplates(caseDefinitionId = caseDefinitionId).map { it.toTriple() }.toSet(),
                templateService.findTemplates(caseDefinitionId = importedId).map { it.toTriple() }.toSet(),
            )
        }
    }

    private fun ValtimoTemplate.toTriple() = Triple(key, type, content)
}
