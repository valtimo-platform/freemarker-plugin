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
import com.ritense.buildingblock.service.BuildingBlockManagementService
import com.ritense.buildingblock.service.CaseDefinitionBuildingBlockLinkService
import com.ritense.buildingblock.web.rest.dto.CreateBuildingBlockDefinitionDto
import com.ritense.buildingblock.web.rest.dto.CreateCaseDefinitionBuildingBlockLinkDto
import com.ritense.exporter.ExportService
import com.ritense.exporter.request.CaseDefinitionExportRequest
import com.ritense.importer.ImportService
import com.ritense.valtimo.contract.buildingblock.BuildingBlockDefinitionId
import com.ritense.valtimo.contract.case_.CaseDefinitionId
import com.ritense.valtimoplugins.freemarker.BaseIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.test.assertContains
import kotlin.test.assertEquals

@Transactional
class BuildingBlockTemplateImportExportIT : BaseIntegrationTest() {
    @Autowired
    lateinit var exportService: ExportService

    @Autowired
    lateinit var importService: ImportService

    @Autowired
    lateinit var templateService: TemplateService

    @Autowired
    lateinit var buildingBlockManagementService: BuildingBlockManagementService

    @Autowired
    lateinit var caseDefinitionBuildingBlockLinkService: CaseDefinitionBuildingBlockLinkService

    @Test
    fun `should export and import the templates of a building block used by an exported case definition`() {
        runWithoutAuthorization {
            val caseDefinitionId = CaseDefinitionId("profile", "1.0.0")
            val buildingBlockDefinitionId = BuildingBlockDefinitionId("template-export", "1.0.0")
            buildingBlockManagementService.create(
                CreateBuildingBlockDefinitionDto(buildingBlockDefinitionId.key, "1.0.0", "Template export", null),
            )
            templateService.saveTemplate(
                templateKey = "greeting",
                buildingBlockDefinitionId = buildingBlockDefinitionId,
                templateType = "text",
                metadata = emptyMap(),
                content = "Hello \${doc.firstName}",
            )
            caseDefinitionBuildingBlockLinkService.createLink(
                caseDefinitionId,
                CreateCaseDefinitionBuildingBlockLinkDto(buildingBlockDefinitionId.key, "1.0.0"),
            )

            val export = exportService.export(CaseDefinitionExportRequest(caseDefinitionId)).toByteArray()

            assertContains(
                zipEntryNames(export),
                "config/building-block/template-export/1-0-0/template/greeting-text.template.json",
            )

            // The building block already exists here, so import it under another key
            val importedId =
                importService.import(
                    replaceInZip(export, "template-export", "template-import").inputStream(),
                    emptyList(),
                    "profile-copy",
                    "Copy",
                )
            val importedTemplates =
                templateService.findTemplates(
                    buildingBlockDefinitionId = BuildingBlockDefinitionId("template-import", "1.0.0"),
                )

            assertEquals(CaseDefinitionId("profile-copy", "1.0.0"), importedId)
            assertEquals(
                listOf(Triple("greeting", "text", "Hello \${doc.firstName}")),
                importedTemplates.map { Triple(it.key, it.type, it.content) },
            )
        }
    }

    private fun zipEntryNames(zip: ByteArray): List<String> =
        ZipInputStream(zip.inputStream()).use { zis ->
            generateSequence { zis.nextEntry }.map { it.name }.toList()
        }

    private fun replaceInZip(
        zip: ByteArray,
        oldValue: String,
        newValue: String,
    ): ByteArray {
        val output = ByteArrayOutputStream()
        ZipInputStream(zip.inputStream()).use { zis ->
            ZipOutputStream(output).use { zos ->
                generateSequence { zis.nextEntry }.forEach { entry ->
                    zos.putNextEntry(ZipEntry(entry.name.replace(oldValue, newValue)))
                    val content = zis.readBytes().decodeToString()
                    zos.write(content.replace(oldValue, newValue).toByteArray())
                    zos.closeEntry()
                }
            }
        }
        return output.toByteArray()
    }
}
