# Release notes

Overzicht van wijzigingen per versie van de Freemarker-plugin.

## 8.6.0

Een waarde die een mail- of PDF-sjabloon invult, zoals een naam uit het dossier, verschijnt nu als gewone tekst.
Een naam als `Jansen & Zn.` laat het genereren van een PDF niet meer mislukken, en een ingevulde waarde kan geen opmaak,
links of afbeeldingen meer aan een mail of PDF toevoegen. Let op: vult een sjabloon bewust HTML in, bijvoorbeeld de
tekst van een rich-text-veld, dan staat die HTML nu letterlijk in de mail of PDF. Schrijf zo'n waarde in de
sjablooneditor als `${doc.toelichting?no_esc}`. Sjablonen die al `?html` gebruiken, werken zoals voorheen: daarin
wordt geen enkele waarde vanzelf omgezet. Haal `?html` weg om ook die sjablonen te beschermen.

Een CSV-export mislukt niet meer door een waarde met een regeleinde, tab of aanhalingsteken, zoals een adres over
meerdere regels. Zo'n waarde blijft in haar eigen kolom staan, en kolommen met dezelfde naam houden elk hun eigen
waarde. Let op: een backslash (`\`) die je zelf in een CSV-sjabloon typt, heeft nu een betekenis: `\t` wordt een tab
en `\n` een nieuwe regel. Typ `\\` voor een backslash. Een nieuw CSV-sjabloon en de uitleg in de editor gebruiken nu
tabs als scheidingsteken, net als de gegenereerde CSV.

De sjablooneditors zijn alleen nog te openen voor beheerders. Het tabblad 'Viewer' van een mailsjabloon voert geen
scripts uit het sjabloon meer uit. Bij het opslaan van een sjabloon blijven de overige instellingen van het sjabloon
bewaard. Een sjabloonsleutel mag letters, cijfers, `-` en `_` bevatten, ook hoofdletters.

Het kruimelpad in een sjablooneditor brengt je terug naar het tabblad met de sjablonen, in plaats van naar het eerste
tabblad van de zaak. Mislukt het verwijderen of aanmaken van een sjabloon, dan blijft het scherm bruikbaar in plaats
van eindeloos te laden. Een voorbeeld dat niet gemaakt kan worden, toont een foutmelding in plaats van een kapot bestand. In de
configuratie van een actie blijft het veld voor het sjabloon bruikbaar als de lijst met sjablonen niet geladen kan
worden. De lijst met tekstsjablonen heeft geen lege kolom 'Read only' meer.

Bij het kiezen van een waarde via `template:` staan nu alleen de tekstsjablonen van de eigen zaakdefinitie of het eigen
bouwblok, elk één keer. Een tekstsjabloon dat via `template:` naar zichzelf verwijst, geeft een duidelijke foutmelding.
Bij het importeren van een zaakdefinitie of bouwblok kan een sjabloon alleen nog naar een sjabloonbestand verwijzen
(`.ftl`, `.ftlh`, `.ftlx`, `.html`, `.htm`, `.xhtml`, `.txt`, `.csv` of `.tsv`).

## 8.5.9

De actie 'Generate Mail Content' is niet langer verouderd en kan weer gewoon gebruikt worden. Deze actie is handig als
iemand een mail eerst wil bekijken voordat die verstuurd wordt, bijvoorbeeld met de e-mailvoorbeeldcomponent. Voor mails
langer dan 4000 tekens is Valtimo 13.51.0 of hoger nodig.

## 8.5.8

Het genereren van documenten is sneller en betrouwbaarder bij zaaktypes met veel dossiers. Ook blijft het genereren van
een PDF niet meer eindeloos hangen als de server van een afbeelding, stylesheet of lettertype niet antwoordt. Een
sjabloon kan `docs` nu meerdere keren doorlopen, en `docs` combineren met waarden die opgezocht moeten worden.

De acties 'Generate PDF' en 'Generate CSV' loggen een waarschuwing als ze niet asynchroon draaien. Markeer zo'n
servicetaak als asynchroon (asyncBefore), zodat de gebruiker niet hoeft te wachten tot het document gegenereerd is.

Een sjabloonvoorbeeld met speciale tekens in de bestandsnaam wordt nu met de volledige naam gedownload. Een sjabloon
genereren op sleutel zonder procesvariabelen (`TemplateService.generate`) liep vast in een StackOverflowError.

## 8.5.7

Ondersteuning voor Valtimo 13.48.0.

## 8.5.6

Beveiligingsfix: FreeMarker bijgewerkt naar 2.3.35 (CVE-2026-84939).

## 8.5.5

Valtimo bijgewerkt naar versie 13.41.0.

## 8.5.4

Het genereren van een document was erg langzaam bij zaakdefinities met veel dossiers. Alle dossiers van het bouwblok
werden bij elke sjabloonverwerking uit de database gelezen, ook wanneer het sjabloon `docs` niet gebruikte. Dit gebeurt
nu alleen nog als het sjabloon daadwerkelijk naar `docs` verwijst.

## 8.5.3

De configuratie van een genereer-actie bleef eindeloos laden.

## 8.5.2

Valtimo bijgewerkt naar versie 13.40.0.

## 8.5.1

Beveiligingsfix (GHSA-j2cr-xx4w-m39c)

## 8.5.0

In de genereer-acties kan de sjabloon nu naast een keuzelijst ook via een vrije tekst/expressie of een value resolver
worden bepaald, zodat een generiek bouwblok met een variabele sjabloon mogelijk is. Bestaande configuraties blijven
ongewijzigd werken.

## 8.4.1

Valtimo bijgewerkt naar versie 13.34.0.

## 8.4.0

Nieuwe plugin-actie `Genereer Text` toegevoegd aan de Text-template plugin: het resultaat van een sjabloon wordt direct
als tekst in een procesvariabele opgeslagen, zonder tussenkomst van tijdelijke bestandsopslag.

## 8.3.6

Valtimo-versies worden niet langer afgedwongen op consumers van de plugin: de Valtimo dependency-BOM wordt nu alleen
tijdens compilatie en tests toegepast en niet meer gepubliceerd als afhankelijkheidsbeperking.

## 8.3.5

Ondergebracht in een eigen repository met voorbeeldapplicatie en aparte documentatie.

## 6.0.1

Verbeteringen aan de Freemarker-plugin.

## 6.0.0

Geschikt gemaakt voor een nieuwe Valtimo-versie.

## 1.0.0

Eerste publieke release: e-mail- en documentsjablonen genereren met FreeMarker.
