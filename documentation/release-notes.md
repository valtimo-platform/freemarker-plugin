# Release notes

Overzicht van wijzigingen per versie van de Freemarker-plugin.

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
