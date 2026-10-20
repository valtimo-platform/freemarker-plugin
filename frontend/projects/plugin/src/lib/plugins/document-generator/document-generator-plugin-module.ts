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

import { NgModule } from "@angular/core";
import { Router } from "@angular/router";
import { DocumentGeneratorConfigurationComponent } from "./components/document-generator-configuration/document-generator-configuration.component";
import { CommonModule } from "@angular/common";
import { PluginManagementService, PluginTranslatePipeModule } from "@valtimo/plugin";
import {
  CarbonListModule,
  ConfirmationModalModule,
  EditorModule,
  FormModule,
  InputModule as ValtimoInputModule,
  ParagraphModule,
  RadioModule,
  RenderInPageHeaderDirective,
  SelectModule,
} from "@valtimo/components";
import {
  ButtonModule,
  DialogModule,
  DropdownModule,
  IconModule,
  InputModule,
  LoadingModule,
  ModalModule,
  NotificationModule,
  TabsModule,
} from "carbon-components-angular";
import { BUILDING_BLOCK_MANAGEMENT_TAB_TOKEN, CASE_MANAGEMENT_TAB_TOKEN } from "@valtimo/shared";
import { pluginTabEnabled$ } from "../shared/plugin-tab/plugin-tab-enabled";
import { DocumentTemplateListComponent } from "./components/document-template-list/document-template-list.component";
import { TranslateModule } from "@ngx-translate/core";
import { ReactiveFormsModule } from "@angular/forms";
import { TemplateManagementRoutingModule } from "./document-generator-management-routing.module";
import { GenerateCsvComponent } from "./components/generate-csv/generate-csv.component";
import { GeneratePdfComponent } from "./components/generate-pdf/generate-pdf.component";

@NgModule({
  declarations: [GenerateCsvComponent, GeneratePdfComponent, DocumentGeneratorConfigurationComponent],
  imports: [
    CommonModule,
    PluginTranslatePipeModule,
    TemplateManagementRoutingModule,
    FormModule,
    ParagraphModule,
    SelectModule,
    RadioModule,
    ConfirmationModalModule,
    TranslateModule,
    ReactiveFormsModule,
    CarbonListModule,
    EditorModule,
    ValtimoInputModule,
    ButtonModule,
    DialogModule,
    DropdownModule,
    IconModule,
    InputModule,
    LoadingModule,
    ModalModule,
    NotificationModule,
    RenderInPageHeaderDirective,
    TabsModule,
  ],
  exports: [GenerateCsvComponent, GeneratePdfComponent],
  providers: [
    {
      provide: CASE_MANAGEMENT_TAB_TOKEN,
      useFactory: (pluginManagementService: PluginManagementService, router: Router) => ({
        translationKey: "Document template",
        component: DocumentTemplateListComponent,
        tabRoute: "document-template",
        enabled$: pluginTabEnabled$(pluginManagementService, router, "document-generator", "document-template"),
      }),
      deps: [PluginManagementService, Router],
      multi: true,
    },
    {
      provide: BUILDING_BLOCK_MANAGEMENT_TAB_TOKEN,
      useFactory: (pluginManagementService: PluginManagementService, router: Router) => ({
        translationKey: "Document template",
        component: DocumentTemplateListComponent,
        tabRoute: "document-template",
        enabled$: pluginTabEnabled$(pluginManagementService, router, "document-generator", "document-template"),
      }),
      deps: [PluginManagementService, Router],
      multi: true,
    },
  ],
})
export class DocumentGeneratorPluginModule {}
