/*
 * Copyright 2015-2022 Ritense BV, the Netherlands.
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

package com.ritense.freemarker.plugin.mail

import com.ritense.plugin.service.PluginService
import com.ritense.processdocument.service.ProcessDocumentService
import com.ritense.resource.service.TemporaryResourceStorageService
import com.ritense.freemarker.service.TemplateService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

@AutoConfiguration
class MailTemplateAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MailTemplatePluginFactory::class)
    fun mailTemplatePluginFactory(
        pluginService: PluginService,
        templateService: TemplateService,
        processDocumentService: ProcessDocumentService,
        storageService: TemporaryResourceStorageService,
    ): MailTemplatePluginFactory {
        return MailTemplatePluginFactory(
            pluginService,
            templateService,
            processDocumentService,
            storageService,
        )
    }

}
