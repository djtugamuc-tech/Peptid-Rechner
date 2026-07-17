# Release & Play Store – Anleitung

## Fertige Artefakte

| Datei | Zweck |
|-------|-------|
| `dist/Peptid-Rechner-v1.0.apk` | **Direkt aufs Handy** (Sideload) – signiert, 1,6 MB |
| `dist/Peptid-Rechner-v1.0.aab` | **Zum Hochladen in den Play Store** (App Bundle) |

Die APK liegt zusätzlich auf dem Desktop: `~/Desktop/Peptid-Rechner-v1.0.apk`.

## Auf dein Handy installieren (ohne Play Store)

1. APK per USB, Google Drive, E-Mail oder Messenger aufs Handy übertragen.
2. Auf dem Handy die Datei öffnen.
3. Android fragt nach der Erlaubnis „Unbekannte Apps installieren" → für die verwendete App (z. B. Dateien/Chrome) erlauben.
4. Installieren, fertig. Die App ist mit deinem Release-Key signiert.

Per Kabel geht es auch direkt:
```
adb install dist/Peptid-Rechner-v1.0.apk
```

## Signatur

- Keystore: `keystore/peptidrechner-release.jks` (Alias `peptidrechner`)
- Passwörter: in `keystore.properties` (NICHT ins Git eingecheckt)
- Signatur-Schema: v2 (Play-kompatibel)
- Zertifikat SHA-256: `9e:36:d9:76:ac:44:21:c7:0d:33:c3:f8:a5:a7:e5:47:de:a1:02:ff:bb:6c:6f:5b:b1:a2:8e:0d:a7:fa:44:c1`

> ⚠️ **Keystore + Passwörter sicher aufbewahren!** Ohne diesen Schlüssel kannst du
> später keine Updates mehr signieren. Am besten mehrfach sichern.

## Neu bauen

```bash
./gradlew :app:assembleRelease   # APK  -> app/build/outputs/apk/release/
./gradlew :app:bundleRelease     # AAB  -> app/build/outputs/bundle/release/
```

## Im Google Play Store veröffentlichen

Das kann nur der Konto-Inhaber tun – Ablauf:

1. **Entwicklerkonto anlegen** unter https://play.google.com/console (einmalig 25 US$).
2. Neue App anlegen (Name, Sprache, „App", kostenlos/kostenpflichtig).
3. Unter **Produktion → Neues Release** die Datei `Peptid-Rechner-v1.0.aab` hochladen.
   - Play App Signing ist standardmäßig aktiv: Google verwaltet den finalen App-Signing-Key,
     dein `.jks` ist dann der **Upload-Key**.
4. **Store-Eintrag** ausfüllen: Kurz-/Langbeschreibung, min. 2 Screenshots, Feature-Grafik, Icon (512×512).
5. **Pflicht-Fragebögen**: Datenschutz (Datensicherheit), Inhaltsfreigabe/Altersfreigabe, Zielgruppe,
   Werbung (keine), **Datenschutzerklärung-URL** (Pflicht).
6. Zur **Prüfung einreichen**. Google prüft i. d. R. einige Tage.

### Wichtig zur Freigabe-Wahrscheinlichkeit

- **Target-API 35** ist erfüllt (Play-Pflicht für neue Apps). ✔
- **Gesundheits-/Medizin-Inhalte:** Google prüft Apps rund um Medikamente/Substanzen streng.
  Da einige Peptide nicht als Arzneimittel zugelassen sind, kann es zu Rückfragen oder Ablehnung
  kommen. Der eingebaute Disclaimer („reines Rechen-/Bildungswerkzeug, keine medizinische Beratung")
  hilft, ist aber keine Garantie. Ggf. Store-Beschreibung entsprechend neutral halten.
- Eine **Datenschutzerklärung** ist zwingend nötig (auch wenn die App alle Daten nur lokal speichert).

> „Play-Store-approved" im Sinne einer garantierten Freigabe kann niemand vorab zusichern –
> die App ist aber **technisch einreichfertig** signiert und konfiguriert.
