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

import { Router } from "@angular/router";
import { PluginManagementService } from "@valtimo/plugin";
import { catchError, defer, map, Observable, of, startWith } from "rxjs";

/**
 * Whether the management tab of a plugin is shown: when the plugin is configured.
 *
 * The tab is shown right away when its own page is open. Otherwise, the tab only appears after the plugin
 * configurations have loaded, and in the meantime the tabs component selects the first tab, which navigates away
 * from the page. That happened when a breadcrumb or page reload opened the tab.
 */
function pluginTabEnabled$(
  pluginManagementService: PluginManagementService,
  router: Router,
  pluginDefinitionKey: string,
  tabRoute: string,
): Observable<boolean> {
  return defer(() =>
    pluginManagementService.getAllPluginConfigurations().pipe(
      map((pluginConfigs) => pluginConfigs.some((pluginConfig) => pluginConfig.pluginDefinition?.key === pluginDefinitionKey)),
      catchError(() => of(false)),
      startWith(router.url.split("?")[0].endsWith(`/${tabRoute}`)),
    ),
  );
}

export { pluginTabEnabled$ };
