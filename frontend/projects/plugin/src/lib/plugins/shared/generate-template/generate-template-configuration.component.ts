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

import {Directive, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FunctionConfigurationComponent, FunctionConfigurationData, PluginTranslationService} from '@valtimo/plugin';
import {
    BlueprintContext,
    RadioValue,
    SelectItem,
    ValuePathSelectorPrefix,
    ValuePathSelectorService,
    ValuePathType,
} from '@valtimo/components';
import {TranslateService} from '@ngx-translate/core';
import {
    BehaviorSubject,
    combineLatest,
    distinctUntilChanged,
    filter,
    map,
    Observable,
    of,
    shareReplay,
    skip,
    Subject,
    Subscription,
    switchMap,
    take,
    takeUntil,
    tap,
} from 'rxjs';
import {ActivatedRoute} from '@angular/router';
import {
    BuildingBlockManagementParams,
    CaseManagementParams,
    getBuildingBlockManagementRouteParams,
    ManagementContext,
    Page,
} from '@valtimo/shared';
import {FreemarkerTemplateManagementService} from '../../../services';
import {TemplateListItem} from '../../../models';

export type TemplateKeyInputType = 'selection' | 'text' | 'value-resolver';

/** The management context the template list is scoped to, once resolved from route/context inputs. */
type ResolvedTemplateContext =
    | {managementContext: 'case'; caseParams: CaseManagementParams}
    | {managementContext: 'buildingBlock'; buildingBlockParams: BuildingBlockManagementParams};

/**
 * Shared base for all "generate template content" plugin actions. It renders a radio toggle that
 * lets the user choose how the template key is provided:
 *  - selection: a dropdown of existing templates (the original behaviour)
 *  - text: a free text input for a literal key or a value-resolver expression (e.g. pv:/doc:)
 *  - value-resolver: a guided value-path-selector for doc:/case: fields
 *
 * The resulting value is stored under {@link keyFieldName}. Because the Valtimo plugin framework
 * resolves placeholder expressions in action properties at runtime, the backend keeps receiving a
 * plain template key string and needs no changes.
 */
@Directive()
export abstract class GenerateTemplateConfigurationComponent
    implements FunctionConfigurationComponent, OnInit, OnDestroy {
    @Input() save$!: Observable<void>;
    @Input() disabled$!: Observable<boolean>;
    @Input() pluginId!: string;
    @Input() context$!: Observable<[ManagementContext, CaseManagementParams]>;
    @Input() prefillConfiguration$!: Observable<FunctionConfigurationData>;
    @Output() valid: EventEmitter<boolean> = new EventEmitter<boolean>();
    @Output() configuration: EventEmitter<FunctionConfigurationData> = new EventEmitter<FunctionConfigurationData>();

    /** The config property name the chosen template key is stored under (e.g. 'textTemplateKey'). */
    abstract readonly keyFieldName: string;
    /** Default value for the process variable name input. */
    abstract readonly defaultProcessVariableName: string;
    /** Translation key for the action description paragraph. */
    abstract readonly descriptionKey: string;
    /** Translation key for the template key field label. */
    abstract readonly keyTitleKey: string;
    /** Translation key for the template key field tooltip (selection mode). */
    abstract readonly keyTooltipKey: string;

    /** Loads the available templates for the resolved management context. */
    protected abstract fetchTemplates(
        caseParam: CaseManagementParams | null,
        buildingBlock: BuildingBlockManagementParams | null,
    ): Observable<Page<TemplateListItem>>;

    readonly ValuePathSelectorPrefix = ValuePathSelectorPrefix;
    readonly valuePathSelectorPrefixes = [ValuePathSelectorPrefix.DOC, ValuePathSelectorPrefix.CASE];

    protected readonly buildingBlockParams$ = getBuildingBlockManagementRouteParams(this.route);
    protected readonly formValue$ = new BehaviorSubject<FunctionConfigurationData | null>(null);
    protected readonly valid$ = new BehaviorSubject<boolean>(false);
    protected readonly _subscriptions = new Subscription();
    protected readonly _destroy$ = new Subject<void>();

    /** The management context the template list is scoped to, or null when there is none. */
    protected resolvedContext$!: Observable<ResolvedTemplateContext | null>;

    readonly loading$ = new BehaviorSubject<boolean>(true);
    readonly templateItems$ = new BehaviorSubject<Array<SelectItem>>([]);
    readonly selectedInputType$ = new BehaviorSubject<TemplateKeyInputType>('selection');
    /**
     * The input type the radio group starts on. Resolved once, before the form is rendered, so the
     * radio is not reset while the user is interacting with it.
     */
    readonly defaultInputType$ = new BehaviorSubject<TemplateKeyInputType>('selection');

    readonly caseDefinitionKey$ = new BehaviorSubject<string | null>(null);
    readonly caseDefinitionVersionTag$ = new BehaviorSubject<string | null>(null);
    readonly buildingBlockDefinitionKey$ = new BehaviorSubject<string | null>(null);
    readonly buildingBlockDefinitionVersionTag$ = new BehaviorSubject<string | null>(null);

    /**
     * The resolvable value-resolver keys (doc:/case: fields) for the active context, shown as a
     * dropdown in 'value-resolver' mode. This deliberately does not use the value-path-selector
     * component, because that always renders an extra manual/dropdown toggle whose manual mode would
     * duplicate the 'text' input mode.
     */
    readonly valueResolverItems$: Observable<Array<SelectItem>> = combineLatest([
        this.caseDefinitionKey$,
        this.caseDefinitionVersionTag$,
        this.buildingBlockDefinitionKey$,
        this.buildingBlockDefinitionVersionTag$,
    ]).pipe(
        map(([caseKey, caseTag, bbKey, bbTag]) => this.buildBlueprintContext(caseKey, caseTag, bbKey, bbTag)),
        filter((context): context is BlueprintContext => !!context),
        switchMap(context =>
            this.valuePathSelectorService.getResolvableKeysForContext(
                this.valuePathSelectorPrefixes,
                [],
                context,
                ValuePathType.FIELD,
            ),
        ),
        map(items => items.map(item => ({id: item.path, text: item.path}))),
    );

    readonly inputTypeRadioValues$: Observable<Array<RadioValue>> = this.translateService.stream('key').pipe(
        map(() => [
            {value: 'selection', title: this.pluginTranslationService.instant('inputTypeSelection', this.pluginId)},
            {value: 'text', title: this.pluginTranslationService.instant('inputTypeText', this.pluginId)},
            {value: 'value-resolver', title: this.pluginTranslationService.instant('inputTypeValueResolver', this.pluginId)},
        ]),
    );

    constructor(
        protected readonly templateService: FreemarkerTemplateManagementService,
        protected readonly route: ActivatedRoute,
        protected readonly translateService: TranslateService,
        protected readonly pluginTranslationService: PluginTranslationService,
        protected readonly valuePathSelectorService: ValuePathSelectorService,
    ) {
    }

    ngOnInit(): void {
        // depends on the context$ input, so it cannot be built as a field initialiser
        this.resolvedContext$ = this.createResolvedContext();
        this.openSaveSubscription();
        this.initInputType();
        this.initTemplateItems();
    }

    ngOnDestroy(): void {
        this._subscriptions.unsubscribe();
        this._destroy$.next();
        this._destroy$.complete();
    }

    formValueChange(formValue: FunctionConfigurationData): void {
        this.formValue$.next(formValue);
        this.handleValid(formValue);

        if (formValue.templateKeyInputType) {
            this.selectedInputType$.next(formValue.templateKeyInputType);
        }
    }

    /** Reads the prefilled template key for the active key field name. */
    prefillKey(prefill: FunctionConfigurationData | null): string | undefined {
        return prefill ? prefill[this.keyFieldName] : undefined;
    }

    private handleValid(formValue: FunctionConfigurationData): void {
        const valid = !!(formValue[this.keyFieldName] && formValue.processVariableName);

        this.valid$.next(valid);
        this.valid.emit(valid);
    }

    private openSaveSubscription(): void {
        const saveSubscription = this.save$?.subscribe(() => {
            combineLatest([this.formValue$, this.valid$])
                .pipe(take(1))
                .subscribe(([formValue, valid]) => {
                    if (valid) {
                        this.configuration.emit(formValue!);
                    }
                });
        });
        this._subscriptions.add(saveSubscription);
    }

    /**
     * Picks the input type the form opens on: the saved one when the configuration is being edited,
     * otherwise the template dropdown when there are templates to choose from, and the free text
     * input when there are none — an empty, required dropdown would be a dead end.
     *
     * This waits for the first loaded template list rather than for the resolved context, so a
     * context that resolves in a later tick (route params of a case or building block) cannot be
     * mistaken for 'no context'.
     */
    private initInputType(): void {
        const inputTypeSubscription = combineLatest([
            (this.prefillConfiguration$ ?? of(null)).pipe(take(1)),
            // templateItems$ is seeded with an empty list; skip it and take the first loaded one
            this.templateItems$.pipe(skip(1), take(1)),
        ])
            .pipe(take(1))
            .subscribe(([prefill, templateItems]) => {
                const inputType =
                    (prefill?.['templateKeyInputType'] as TemplateKeyInputType) ||
                    (templateItems.length ? 'selection' : 'text');

                this.defaultInputType$.next(inputType);
                this.selectedInputType$.next(inputType);
            });
        this._subscriptions.add(inputTypeSubscription);
    }

    /** Loads the template dropdown items for the resolved context; empty when there is no context. */
    private initTemplateItems(): void {
        this.resolvedContext$
            .pipe(
                switchMap(params => {
                    if (!params) {
                        return of(null);
                    }

                    return params.managementContext === 'case'
                        ? this.fetchTemplates(params.caseParams, null)
                        : this.fetchTemplates(null, params.buildingBlockParams);
                }),
                map(results =>
                    results?.content.map(template => ({
                        id: template.key,
                        text: template.key,
                    })) || [],
                ),
                takeUntil(this._destroy$),
            )
            .subscribe(results => {
                // the items are published before loading is cleared, so the form is rendered with the
                // input type that initInputType() derives from them
                this.templateItems$.next(results);
                this.loading$.next(false);
            });
    }

    /**
     * Resolves the management context the template list is scoped to. Both sources always emit — a
     * resolved context or null — so that a missing context is handled instead of stalling the view:
     * `context$` emits a tuple with empty case params when the surrounding route is not case
     * management, and the building block route params emit null when the route carries none. That is
     * what happens when the action is configured from the process builder (Admin > Processes), where
     * neither a case nor a building block definition is in scope.
     */
    private createResolvedContext(): Observable<ResolvedTemplateContext | null> {
        const caseContext$: Observable<ResolvedTemplateContext | null> = (this.context$ ?? of(null)).pipe(
            map(context => {
                if (!context) {
                    return null;
                }

                const [managementContext, caseParams] = context;

                return managementContext === 'case' && !!caseParams?.caseDefinitionKey
                    ? {managementContext: 'case' as const, caseParams}
                    : null;
            }),
        );

        const buildingBlockContext$: Observable<ResolvedTemplateContext | null> = this.buildingBlockParams$.pipe(
            map(buildingBlockParams =>
                buildingBlockParams?.buildingBlockDefinitionKey
                    ? {managementContext: 'buildingBlock' as const, buildingBlockParams}
                    : null,
            ),
        );

        return combineLatest([caseContext$, buildingBlockContext$]).pipe(
            map(([caseContext, buildingBlockContext]) => buildingBlockContext ?? caseContext),
            distinctUntilChanged((previous, current) => JSON.stringify(previous) === JSON.stringify(current)),
            tap(params => {
                if (params) {
                    this.captureContext(params);
                }
            }),
            shareReplay({bufferSize: 1, refCount: false}),
        );
    }

    private captureContext(params: ResolvedTemplateContext): void {
        if (params.managementContext === 'case') {
            this.caseDefinitionKey$.next(params.caseParams?.caseDefinitionKey ?? null);
            this.caseDefinitionVersionTag$.next(params.caseParams?.caseDefinitionVersionTag ?? null);
        } else {
            this.buildingBlockDefinitionKey$.next(params.buildingBlockParams?.buildingBlockDefinitionKey ?? null);
            this.buildingBlockDefinitionVersionTag$.next(params.buildingBlockParams?.buildingBlockDefinitionVersionTag ?? null);
        }
    }

    private buildBlueprintContext(
        caseKey: string | null,
        caseTag: string | null,
        bbKey: string | null,
        bbTag: string | null,
    ): BlueprintContext | null {
        if (bbKey && bbTag) {
            return {type: 'building-block', key: bbKey, versionTag: bbTag};
        }
        if (caseKey) {
            return {type: 'case', key: caseKey, versionTag: caseTag ?? undefined};
        }
        return null;
    }
}
