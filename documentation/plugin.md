# Freemarker library

Contains three plugins which all use Freemarker.

## Plugin 1: Mail Template Plugin

For creating HTML mail templates with Freemarker.

### Plugin actions

| Action                | Key                     | Description                                                                                                                            |
|-----------------------|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| Generate Mail File    | `generate-mail-file`    | Generates HTML based on a mail template and saves it as a temporary file. The resource ID is stored in the specified process variable. |
| Generate Mail Content | `generate-mail-content` | Generates HTML text based on a mail template and saves the text directly in a process variable.                                        |

### Action properties

- `mailTemplateKey` — The key of the mail template to use for generation.
- `processVariableName` — The name of the process variable where the result will be stored.

## Plugin 2: Text Template Plugin

For creating text templates with Freemarker. These text templates can be used in a value-resolver. For example:
`template:my-template`.

https://github.com/user-attachments/assets/3b27631d-bbda-406e-b6b7-f6e5be21f9b9

### Plugin actions

| Action             | Key                  | Description                                                                                                                       |
|--------------------|----------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| Generate Text File | `generate-text-file` | Generates text based on a template and saves it as a temporary file. The resource ID is stored in the specified process variable. |

### Action properties

- `textTemplateKey` — The key of the text template to use for generation.
- `processVariableName` — The name of the process variable where the resource ID will be stored.

### Value resolver

The Text Template Plugin registers a value resolver with the prefix `template`. This allows text templates to be
used anywhere value resolvers are supported. For example, `template:my-template` will resolve the template with
key `my-template` using the current document and process variables as data.

## Plugin 3: Document Generator Plugin

For creating PDF and CSV documents.

https://github.com/user-attachments/assets/7f67de58-3d46-49fb-ba3a-d713e858ec1b

### Plugin actions

| Action       | Key            | Description                                                                                                                                                 |
|--------------|----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Generate PDF | `generate-pdf` | Generates a PDF based on an HTML/Freemarker template and saves it as a temporary file. The resource ID is stored in the specified process variable.         |
| Generate CSV | `generate-csv` | Generates a CSV based on a tab-delimited Freemarker template and saves it as a temporary file. The resource ID is stored in the specified process variable. |

### Action properties

- `templateKey` — The key of the template to use for generation.
- `processVariableName` — The name of the process variable where the resource ID will be stored.

# Dependencies

In order to use the plugins, the module needs to be added as a dependency. The
following can be added to your project, depending on whether Maven or Gradle is used:

## Backend

The following Gradle dependency can be added to your `build.gradle` file:

```kotlin
dependencies {
    implementation("com.ritense.valtimoplugins:freemarker:8.1.0")
}
```

The most recent version can be found [here](https://mvnrepository.com/artifact/com.ritense.valtimoplugins/freemarker).

## Frontend

The following dependency can be added to your `package.json` file:

```json
{
    "dependencies": {
        "@valtimo-plugins/freemarker": "8.1.0"
    }
}
```

The most recent version can be
found [here](https://www.npmjs.com/package/@valtimo-plugins/freemarker?activeTab=versions).

In order to use the plugins in the frontend, the following must be added to your `app.module.ts`:

```typescript
import {
  DocumentGeneratorPluginModule, documentGeneratorPluginSpecification, // Only needed for the document-generator plugin
  MailTemplatePluginModule, mailTemplatePluginSpecification, // Only needed for the mail-template plugin
  TextTemplatePluginModule, textTemplatePluginSpecification // Only needed for the text-template plugin
} from '@valtimo-plugins/freemarker';

@NgModule({
  imports: [
    DocumentGeneratorPluginModule,
    MailTemplatePluginModule,
    TextTemplatePluginModule,
  ],
  providers: [
    {
      provide: PLUGIN_TOKEN,
      useValue: [
        documentGeneratorPluginSpecification,
        mailTemplatePluginSpecification,
        textTemplatePluginSpecification,
      ]
    }
  ]
})
```
