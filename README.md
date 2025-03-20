# Contacts Map App

Eine Open-Source Android-App zur Anzeige von Kontakten auf einer Karte. Die App verwendet ausschließlich Open-Source-Komponenten.

## Features

- Anzeigen von Gerätekontakten in einer Liste
- Anzeigen von Kontakten mit Standorten auf einer Karte
- Hinzufügen von Standorten zu Kontakten durch langes Tippen auf die Karte
- Volltextsuche in Kontakten
- Optimiert für Leistung und Erweiterbarkeit

## Verwendete Technologien

- **Architektur:** Clean Architecture mit MVVM-Pattern
- **UI:** Jetpack Compose für moderne, deklarative UI
- **Karten:** OpenStreetMap mit osmdroid (Open-Source-Alternative zu Google Maps)
- **Dependency Injection:** Koin (leichtgewichtige Alternative zu Dagger/Hilt)
- **Datenbank:** Room für lokale Speicherung
- **Asynchrone Verarbeitung:** Kotlin Coroutines & Flow
- **Berechtigungen:** Accompanist Permissions

## Projekt-Struktur

Das Projekt folgt Clean Architecture Prinzipien und ist in mehrere Module unterteilt:

- **data:** Repository-Implementierungen, Datenquellen und Mapper
- **domain:** Business Logic, Modelle und Use Cases 
- **ui:** ViewModels und Compose UI-Komponenten
- **di:** Dependency Injection Module

## Erweiterbarkeit

Die App wurde mit Erweiterbarkeit im Fokus entwickelt:

- Komponenten sind über Interfaces entkoppelt
- Die Karten-Komponente kann in anderen Apps wiederverwendet werden
- Die Clean Architecture erlaubt einfache Erweiterungen mit neuen Features

## Anforderungen

- Android SDK 26+ (Android 8.0 oder höher)
- Kotlin 1.9+
- Berechtigungen:
  - Kontakte lesen
  - Standort
  - Internetzugang (für Karten)

## Installation

1. Projekt von GitHub klonen
2. In Android Studio öffnen
3. Gradle-Sync durchführen
4. Auf Gerät oder Emulator ausführen

## Nutzung

1. App starten und Berechtigungen gewähren
2. Kontakte werden auf der Karte angezeigt (falls Standortdaten vorhanden)
3. Auf "Kontakte" tippen, um eine vollständige Liste anzuzeigen
4. Langes Tippen auf die Karte, um einem Kontakt einen Standort zuzuweisen

## Lizenz

Diese App ist Open Source unter der MIT-Lizenz.

## Beiträge

Beiträge sind willkommen! Bitte öffne ein Issue oder einen Pull Request für Verbesserungen oder Fehlerbehebungen. 