# Freemarker library

Contains three plugins which all use Freemarker.

Mail and PDF templates escape every value they insert as HTML, so `Smith & Sons` stays readable and a value can't add
markup to the mail or PDF. To insert a value that is HTML on purpose, write `${value?no_esc}`. Templates that already
escape with `?html` or `<#escape>` keep working as before, but then none of their values is escaped automatically and
they can't use `?no_esc`; remove `?html` and `<#escape>` to have the template escaped. CSV templates escape tabs, line
breaks, quotes and backslashes in a value, so it stays in one field. A backslash written in the CSV template itself
starts an escape sequence (`\t` is a tab, `\n` a line break, `\r` a carriage return, `\b` a backspace, `\f` a form
feed, `\\` a backslash). A single backslash at the end of a line joins it to the next line, and at the end of the
template it makes the CSV generation fail. Text templates don't escape.

## Plugin 1: Mail Template Plugin

For creating HTML mail templates with Freemarker.

### Plugin actions

| Action                | Key                     | Description                                                                                                                            |
|-----------------------|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| Generate Mail File    | `generate-mail-file`    | Generates HTML based on a mail template and saves it as a temporary file. The resource ID is stored in the specified process variable. |
| Generate Mail Content | `generate-mail-content` | Generates HTML text based on a mail template and saves the text directly in a process variable.                                        |

Use **Generate Mail Content** when someone needs to check a mail before it is sent. The mail text stays available for as
long as the process runs. You can show it to a user with the
[e-mail preview component](https://docs.valtimo.nl/configuration-guides/cases/forms/email-preview-component) and send
it afterwards with the SMTP plugin (set its content to `pv:<processVariableName>`).

**Generate Mail File** is less suited for this: its temporary file is cleaned up after a while and may already be gone
when someone opens the task. Mails longer than 4000 characters need Valtimo 13.51.0 or later.

### Action properties

- `mailTemplateKey` — The key of the mail template to use for generation. In the configuration screen the key can be
  provided in three ways: by selecting an existing template, by entering free text or a value-resolver expression (e.g.
  `pv:myKey` or `doc:/path`), or by selecting a document/case field via a value resolver.
- `processVariableName` — The name of the process variable where the result will be stored.

## Plugin 2: Text Template Plugin

For creating text templates with Freemarker. These text templates can be used in a value-resolver. For example:
`template:my-template`.

https://github.com/user-attachments/assets/3b27631d-bbda-406e-b6b7-f6e5be21f9b9

### Plugin actions

| Action             | Key                  | Description                                                                                                                       |
|--------------------|----------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| Generate Text File | `generate-text-file` | Generates text based on a template and saves it as a temporary file. The resource ID is stored in the specified process variable. |
| Generate Text      | `generate-text`      | Generates text based on a template and stores the text directly in the specified process variable.                                |

### Action properties

- `textTemplateKey` — The key of the text template to use for generation. In the configuration screen the key can be
  provided in three ways: by selecting an existing template, by entering free text or a value-resolver expression (e.g.
  `pv:myKey` or `doc:/path`), or by selecting a document/case field via a value resolver.
- `processVariableName` — The name of the process variable where the result will be stored (resource ID for `generate-text-file`, generated text for `generate-text`).
  Generated text longer than 4000 characters needs Valtimo 13.51.0 or later.

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

- `templateKey` — The key of the template to use for generation. In the configuration screen the key can be provided in
  three ways: by selecting an existing template, by entering free text or a value-resolver expression (e.g. `pv:myKey` or
  `doc:/path`), or by selecting a document/case field via a value resolver.
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
