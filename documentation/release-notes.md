# Release notes

Overzicht van wijzigingen per versie van de Freemarker-plugin.

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
