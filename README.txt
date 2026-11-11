====================================================================
  JavaQuiz – Konsolenbasiertes Multiple-Choice-Quizspiel
====================================================================

PROJEKTNAME:    JavaQuiz
VERSION:        1.0
AUTOR:          Hochschul-Gruppenarbeit
SPRACHE:        Deutsch
KODIERUNG:      UTF-8

KURZBESCHREIBUNG
----------------
JavaQuiz ist ein kleines, konsolenbasiertes Multiple-Choice-Quizspiel,
das vollständig in Java geschrieben ist. Es liest Fragen aus einer
Textdatei, stellt diese dem Spieler in zufälliger Reihenfolge vor
und speichert das Ergebnis dauerhaft in einer Highscore-Datei.
Das Spiel demonstriert den Einsatz von Datei-Ein- und -Ausgabe
(java.io.*) in einem praxisnahen Kontext.


VORAUSSETZUNGEN
---------------
- Java Development Kit (JDK) Version 17 oder höher
- Betriebssystem: Windows, Linux oder macOS (plattformunabhängig)
- Keine externen Bibliotheken erforderlich


PROJEKTSTRUKTUR
---------------
JavaQuiz/
├── src/
│   └── QuizSpiel.java       (Hauptklasse – gesamter Quellcode)
├── data/
│   ├── fragen.txt           (Fragedatenbank im Textformat)
│   └── highscore.txt        (Highscore-Speicher, wird automatisch befüllt)
├── out/                     (Zielverzeichnis für kompilierte .class-Dateien)
└── README.txt               (diese Datei)


KOMPILIEREN UND STARTEN
------------------------

Schritt 1: Terminal/Eingabeaufforderung öffnen und in das Projektverzeichnis wechseln:
  cd Pfad/zu/JavaQuiz

Schritt 2: Kompilieren:
  javac -encoding UTF-8 src/QuizSpiel.java -d out

Schritt 3: Starten:
  java -cp out QuizSpiel

Hinweis: Das Programm muss aus dem Ordner "JavaQuiz/" gestartet werden,
damit die relativen Pfade zu "data/fragen.txt" und "data/highscore.txt"
korrekt aufgelöst werden.


DATEIFORMATE
------------

[fragen.txt]
  - Jede Zeile enthält eine Frage im folgenden Format (Semikolon-getrennt):
      Frage;Antwort1;Antwort2;Antwort3;Antwort4;RichtigeNummer
  - RichtigeNummer ist eine Zahl von 1 bis 4.
  - Zeilen, die mit "#" beginnen, sind Kommentare und werden ignoriert.
  - Leere Zeilen werden ignoriert.
  - Ungültige Zeilen (falsche Spaltenzahl, ungültige Zahl) werden
    übersprungen und eine Warnung auf der Konsole ausgegeben.
  - Kodierung: UTF-8

  Beispiel:
    # Dies ist ein Kommentar
    Was ist die Hauptstadt von Deutschland?;Berlin;München;Hamburg;Köln;1

[highscore.txt]
  - Jede Zeile enthält einen Eintrag im Format: Name;Punkte
  - Die Datei wird im Append-Modus beschrieben (alte Einträge bleiben erhalten).
  - Fehlende Datei wird automatisch angelegt.
  - Kodierung: UTF-8

  Beispiel:
    Max;3
    Anna;10


SPIELABLAUF (4 SCHRITTE)
------------------------
1. Hauptmenü erscheint – Auswahl mit Taste 1, 2 oder 3.
2. Bei Auswahl "1 – Neues Spiel": Name eingeben, Fragen werden geladen
   und gemischt (max. 10 Stück pro Runde).
3. Jede Frage wird mit 4 Antwortmöglichkeiten angezeigt.
   Antwort durch Eingabe der Zahl 1–4 bestätigen (Enter).
   - Richtig → ✅ Punkt wird gutgeschrieben.
   - Falsch oder ungültige Eingabe → ❌ Richtige Antwort wird angezeigt.
4. Am Ende der Runde erscheint das Ergebnis (Name, richtige Antworten,
   Gesamtpunkte). Das Ergebnis wird automatisch gespeichert.


TESTFÄLLE
---------
+----+-----------------------------------------------+---------------------------------------+
| Nr | Aktion                                        | Erwartetes Ergebnis                   |
+----+-----------------------------------------------+---------------------------------------+
|  1 | Menü → 1 → Name "Anna" → alle richtig        | 10 Punkte, Highscore gespeichert      |
|  2 | Antwort "9" eingeben                          | "Ungültige Eingabe" + weiter          |
|  3 | Antwort "abc" eingeben                        | "Ungültige Eingabe" + weiter          |
|  4 | Menü → 2                                      | Top-5-Liste, absteigend sortiert      |
|  5 | fragen.txt löschen → Menü → 1                | "FEHLER: Keine Fragen gefunden"       |
|  6 | 3 Spiele spielen → Menü → 2                  | Einträge absteigend sortiert          |
|  7 | Menü → 3                                      | Programm beendet sich sauber          |
+----+-----------------------------------------------+---------------------------------------+


BEKANNTE GRENZEN
----------------
- Nur Konsolenbetrieb – keine grafische Benutzeroberfläche (GUI).
- Keine Netzwerkfunktionen – kein Multiplayer oder Online-Highscore.
- Fragen können nicht über das Programm selbst hinzugefügt werden
  (manuelle Bearbeitung von fragen.txt erforderlich).
- Bei sehr langen Namen kann die Highscore-Tabelle visuell verrutschen.
- Keine Passwort-/Authentifizierungsfunktion: Jeder kann unter beliebigem
  Namen spielen.


ERWEITERUNGSIDEEN
-----------------
1. Schwierigkeitsgrade (Leicht/Mittel/Schwer) mit eigenen Fragekategorien.
2. Zeitlimit pro Frage (z.B. 30 Sekunden) mit automatischer Wertung.
3. Grafische Oberfläche (JavaFX oder Swing).
4. Online-Highscore per HTTP-Verbindung zu einem Server.
5. Admin-Modus zum Hinzufügen, Bearbeiten und Löschen von Fragen
   direkt aus dem Programm heraus.


====================================================================
  Ende der Dokumentation
====================================================================
