import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * QuizSpiel – Konsolenbasiertes Multiple-Choice-Quizspiel.
 *
 * <p>Dieses Programm liest Fragen aus einer Textdatei, stellt sie dem Spieler
 * in zufälliger Reihenfolge vor und speichert das Ergebnis dauerhaft in einer
 * Highscore-Datei. Alle Ein- und Ausgaben erfolgen über die Konsole.</p>
 *
 * <p>Verwendung:</p>
 * <pre>
 *   javac -encoding UTF-8 src/QuizSpiel.java -d out
 *   java -cp out QuizSpiel
 * </pre>
 *
 * @version 1.0
 */
public class QuizSpiel {

    // =========================================================
    //  KONSTANTEN
    // =========================================================

    /** Pfad zur Fragendatei (relativ zum Arbeitsverzeichnis). */
    private static final String FRAGEN_DATEI = "data/fragen.txt";

    /** Pfad zur Highscore-Datei (relativ zum Arbeitsverzeichnis). */
    private static final String HIGHSCORE_DATEI = "data/highscore.txt";

    /** Maximale Anzahl Fragen pro Spielrunde. */
    private static final int MAX_FRAGEN = 10;

    /** Punkte je richtig beantworteter Frage. */
    private static final int PUNKTE_PRO_FRAGE = 1;

    /** Anzahl der angezeigten Plätze in der Highscore-Liste. */
    private static final int TOP_LISTE = 5;

    /**
     * Gemeinsamer Scanner für die gesamte Laufzeit des Programms.
     * Ein einziger Scanner verhindert Ressourcen-Konflikte beim Lesen von System.in.
     */
    private static final Scanner EINGABE = new Scanner(System.in, StandardCharsets.UTF_8);

    // =========================================================
    //  INTERNE HILFSKLASSEN
    // =========================================================

    /**
     * Repräsentiert eine einzelne Quiz-Frage mit vier Antwortmöglichkeiten.
     */
    static class Frage {

        /** Der Fragetext. */
        String frageText;

        /** Die vier Antwortoptionen (Index 0–3 entspricht Antwort 1–4). */
        String[] antworten;

        /**
         * Der nullbasierte Index der richtigen Antwort.
         * (0 = Antwort 1, 1 = Antwort 2, …)
         */
        int richtigeAntwortIndex;

        /**
         * Erstellt eine neue Frage.
         *
         * @param frageText            Der Fragetext.
         * @param antworten            Array mit genau 4 Antwortoptionen.
         * @param richtigeAntwortIndex Nullbasierter Index der richtigen Antwort (0–3).
         */
        Frage(String frageText, String[] antworten, int richtigeAntwortIndex) {
            this.frageText = frageText;
            this.antworten = antworten;
            this.richtigeAntwortIndex = richtigeAntwortIndex;
        }
    }

    /**
     * Repräsentiert einen einzelnen Highscore-Eintrag mit Name und Punktzahl.
     * Implementiert {@link Comparable} für die automatische Sortierung.
     */
    static class HighscoreEintrag implements Comparable<HighscoreEintrag> {

        /** Der Name des Spielers. */
        String name;

        /** Die erreichte Punktzahl. */
        int punkte;

        /**
         * Erstellt einen neuen Highscore-Eintrag.
         *
         * @param name   Name des Spielers.
         * @param punkte Erreichte Punkte.
         */
        HighscoreEintrag(String name, int punkte) {
            this.name = name;
            this.punkte = punkte;
        }

        /**
         * Vergleicht zwei Einträge absteigend nach Punkten.
         * Höhere Punktzahl kommt zuerst (kleinerer Rang).
         *
         * @param other Der andere Eintrag.
         * @return Negativer Wert wenn {@code this} mehr Punkte hat, positiver wenn weniger.
         */
        @Override
        public int compareTo(HighscoreEintrag other) {
            return Integer.compare(other.punkte, this.punkte); // absteigend
        }
    }

    // =========================================================
    //  HAUPTPROGRAMM
    // =========================================================

    /**
     * Einstiegspunkt des Programms. Initialisiert die Hauptschleife und
     * zeigt das Hauptmenü an, bis der Benutzer das Programm beendet.
     *
     * @param args Kommandozeilenargumente (werden nicht verwendet).
     */
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║      Willkommen bei JavaQuiz!    ║");
        System.out.println("╚══════════════════════════════════╝");

        boolean laufe = true;
        while (laufe) {
            int auswahl = zeigeHauptmenue();
            switch (auswahl) {
                case 1 -> spieleQuiz();
                case 2 -> zeigeHighscores();
                case 3 -> {
                    System.out.println("\nAuf Wiedersehen! 👋");
                    laufe = false;
                }
                default -> System.out.println("  ❌ Ungültige Auswahl. Bitte 1, 2 oder 3 eingeben.");
            }
        }
    }

    // =========================================================
    //  HAUPTMENÜ
    // =========================================================

    /**
     * Zeigt das Hauptmenü an und liest die Auswahl des Benutzers ein.
     * Bei einer nicht-numerischen Eingabe wird -1 zurückgegeben,
     * sodass die Hauptschleife eine passende Fehlermeldung anzeigen kann.
     *
     * @return Die gewählte Menüoption (1, 2 oder 3), oder -1 bei ungültiger Eingabe.
     */
    static int zeigeHauptmenue() {
        System.out.println();
        System.out.println("┌─────────────────────────────────┐");
        System.out.println("│           HAUPTMENÜ             │");
        System.out.println("├─────────────────────────────────┤");
        System.out.println("│  1) Neues Spiel starten         │");
        System.out.println("│  2) Highscores anzeigen         │");
        System.out.println("│  3) Beenden                     │");
        System.out.println("└─────────────────────────────────┘");
        System.out.print("Ihre Auswahl: ");

        if (!EINGABE.hasNextLine()) {
            return 3; // Kein weiterer Input (z.B. bei Pipe-Ende) → sauber beenden
        }
        String eingabe = EINGABE.nextLine().trim();

        try {
            return Integer.parseInt(eingabe);
        } catch (NumberFormatException e) {
            return -1; // Ungültige Eingabe
        }
    }

    // =========================================================
    //  SPIELABLAUF
    // =========================================================

    /**
     * Führt eine vollständige Quiz-Runde durch:
     * Spielername abfragen, Fragen laden und mischen, Fragen stellen,
     * Ergebnis berechnen und dauerhaft speichern.
     *
     * <p>Bei Fehlern (z.B. fehlende Fragendatei oder keine gültigen Fragen)
     * wird eine verständliche Meldung ausgegeben und sicher ins Hauptmenü
     * zurückgekehrt, ohne das Programm zu beenden.</p>
     */
    static void spieleQuiz() {
        // --- Spielernamen abfragen ---
        System.out.print("\nBitte Ihren Namen eingeben: ");
        String spielerName;
        if (EINGABE.hasNextLine()) {
            spielerName = EINGABE.nextLine().trim();
        } else {
            spielerName = "Anonym";
        }
        if (spielerName.isEmpty()) {
            spielerName = "Anonym";
        }

        // --- Fragen laden ---
        List<Frage> alleFragen = ladeFragen(FRAGEN_DATEI);

        if (alleFragen.isEmpty()) {
            System.out.println("❌ FEHLER: Keine Fragen gefunden. Bitte prüfen Sie die Datei: " + FRAGEN_DATEI);
            return; // Zurück ins Hauptmenü
        }

        // --- Fragen mischen und auf MAX_FRAGEN begrenzen ---
        Collections.shuffle(alleFragen);
        List<Frage> rundenFragen = alleFragen.subList(0, Math.min(MAX_FRAGEN, alleFragen.size()));

        System.out.println();
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🎮 Spieler : " + spielerName);
        System.out.printf("📋 Fragen  : %d  |  Viel Erfolg!%n", rundenFragen.size());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        int punkteGesamt = 0;
        int richtigeAntworten = 0;

        // --- Fragen einzeln stellen ---
        for (int i = 0; i < rundenFragen.size(); i++) {
            Frage frage = rundenFragen.get(i);

            System.out.println();
            System.out.printf("Frage %d von %d: %s%n", i + 1, rundenFragen.size(), frage.frageText);
            for (int j = 0; j < frage.antworten.length; j++) {
                System.out.printf("  %d) %s%n", j + 1, frage.antworten[j]);
            }
            System.out.print("Ihre Antwort (1-4): ");

            // Antwort einlesen
            String antwortEingabe = EINGABE.hasNextLine() ? EINGABE.nextLine().trim() : "";
            int antwortNummer = -1;

            try {
                antwortNummer = Integer.parseInt(antwortEingabe);
            } catch (NumberFormatException e) {
                // Keine gültige Zahl – bleibt bei -1
            }

            boolean gueltigeEingabe = (antwortNummer >= 1 && antwortNummer <= 4);

            if (!gueltigeEingabe) {
                System.out.printf("⚠️  Ungültige Eingabe \"%s\" – wird als falsch gewertet.%n", antwortEingabe);
            }

            boolean korrekt = gueltigeEingabe && ((antwortNummer - 1) == frage.richtigeAntwortIndex);

            if (korrekt) {
                System.out.println("✅ Richtig!");
                richtigeAntworten++;
                punkteGesamt += PUNKTE_PRO_FRAGE;
            } else {
                System.out.printf("❌ Falsch! Richtige Antwort: %d) %s%n",
                        frage.richtigeAntwortIndex + 1,
                        frage.antworten[frage.richtigeAntwortIndex]);
            }
        }

        // --- Ergebnis anzeigen ---
        System.out.println();
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏆 SPIELENDE – Ihr Ergebnis:");
        System.out.printf("   Spieler           : %s%n", spielerName);
        System.out.printf("   Richtige Antworten: %d von %d%n", richtigeAntworten, rundenFragen.size());
        System.out.printf("   Punkte gesamt     : %d%n", punkteGesamt);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // --- Highscore dauerhaft speichern ---
        speichereHighscore(spielerName, punkteGesamt);
    }

    // =========================================================
    //  FRAGEN LADEN
    // =========================================================

    /**
     * Liest alle gültigen Fragen aus der angegebenen Datei (UTF-8) und
     * gibt sie als Liste zurück.
     *
     * <p>Erwartetes Dateiformat pro Zeile (Semikolon-getrennt):<br>
     * {@code Frage;Antwort1;Antwort2;Antwort3;Antwort4;RichtigeNummer(1-4)}</p>
     *
     * <p>Verarbeitungsregeln:</p>
     * <ul>
     *   <li>Zeilen, die mit {@code #} beginnen, werden als Kommentare ignoriert.</li>
     *   <li>Leere Zeilen werden ignoriert.</li>
     *   <li>Zeilen mit falscher Spaltenzahl → Warnung, Zeile übersprungen.</li>
     *   <li>Ungültige Antwort-Nummer → Warnung, Zeile übersprungen.</li>
     *   <li>Datei nicht vorhanden → Fehlermeldung, leere Liste zurückgeben.</li>
     * </ul>
     *
     * @param dateiname Pfad zur Fragendatei.
     * @return Liste aller erfolgreich geladenen {@link Frage}-Objekte. Bei
     *         Fehler oder keiner gültigen Frage eine leere Liste.
     */
    static List<Frage> ladeFragen(String dateiname) {
        List<Frage> fragen = new ArrayList<>();
        File datei = new File(dateiname);

        if (!datei.exists()) {
            System.out.println("❌ FEHLER: Fragendatei nicht gefunden: " + dateiname);
            return fragen;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(datei), StandardCharsets.UTF_8))) {

            String zeile;
            int zeilenNummer = 0;

            while ((zeile = reader.readLine()) != null) {
                zeilenNummer++;
                zeile = zeile.trim();

                // Kommentare und Leerzeilen überspringen
                if (zeile.isEmpty() || zeile.startsWith("#")) {
                    continue;
                }

                // Felder aufteilen (limit -1 → nachgestellte Leerfelder behalten)
                String[] felder = zeile.split(";", -1);

                // Validierung: genau 6 Felder erwartet
                if (felder.length != 6) {
                    System.out.printf("⚠️  Warnung (Zeile %d): Falsche Spaltenzahl (%d statt 6) – Zeile übersprungen.%n",
                            zeilenNummer, felder.length);
                    continue;
                }

                String frageText = felder[0].trim();
                String[] antworten = {
                    felder[1].trim(),
                    felder[2].trim(),
                    felder[3].trim(),
                    felder[4].trim()
                };

                // Antwort-Nummer parsen
                int richtigeNummer;
                try {
                    richtigeNummer = Integer.parseInt(felder[5].trim());
                } catch (NumberFormatException e) {
                    System.out.printf("⚠️  Warnung (Zeile %d): Ungültige Antwort-Nummer \"%s\" – Zeile übersprungen.%n",
                            zeilenNummer, felder[5].trim());
                    continue;
                }

                // Validierung: Nummer muss 1–4 sein
                if (richtigeNummer < 1 || richtigeNummer > 4) {
                    System.out.printf(
                            "⚠️  Warnung (Zeile %d): Antwort-Nummer %d liegt außerhalb des Bereichs 1-4 – Zeile übersprungen.%n",
                            zeilenNummer, richtigeNummer);
                    continue;
                }

                fragen.add(new Frage(frageText, antworten, richtigeNummer - 1));
            }

        } catch (IOException e) {
            System.out.println("❌ FEHLER beim Lesen der Fragendatei: " + e.getMessage());
        }

        System.out.printf("📂 %d Fragen erfolgreich geladen aus: %s%n", fragen.size(), dateiname);
        return fragen;
    }

    // =========================================================
    //  HIGHSCORE SPEICHERN
    // =========================================================

    /**
     * Speichert einen neuen Highscore-Eintrag am Ende der Highscore-Datei (Append-Modus).
     *
     * <p>Bestehende Einträge bleiben dabei erhalten. Falls der Ordner
     * {@code data/} noch nicht existiert, wird er automatisch erstellt.
     * Das Format eines Eintrags lautet: {@code Name;Punkte}</p>
     *
     * @param name   Name des Spielers (darf Leerzeichen enthalten, aber kein Semikolon).
     * @param punkte Erreichte Punktzahl (nicht-negativ).
     */
    static void speichereHighscore(String name, int punkte) {
        // Ordner anlegen, falls er nicht existiert
        File datei = new File(HIGHSCORE_DATEI);
        File ordner = datei.getParentFile();

        if (ordner != null && !ordner.exists()) {
            if (ordner.mkdirs()) {
                System.out.println("📁 Ordner \"data\" wurde automatisch erstellt.");
            } else {
                System.out.println("❌ FEHLER: Ordner \"data\" konnte nicht erstellt werden. Highscore nicht gespeichert.");
                return;
            }
        }

        // Eintrag anhängen (append = true erhält alle bisherigen Einträge)
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(datei, true), StandardCharsets.UTF_8))) {

            writer.write(name + ";" + punkte);
            writer.newLine();
            System.out.printf("💾 Highscore gespeichert: %s – %d Punkt(e).%n", name, punkte);

        } catch (IOException e) {
            System.out.println("❌ FEHLER beim Speichern des Highscores: " + e.getMessage());
        }
    }

    // =========================================================
    //  HIGHSCORES ANZEIGEN
    // =========================================================

    /**
     * Liest alle Highscore-Einträge aus der Highscore-Datei, sortiert sie
     * absteigend nach Punkten und gibt die besten {@value #TOP_LISTE} Einträge
     * formatiert auf der Konsole aus.
     *
     * <p>Verarbeitungsregeln:</p>
     * <ul>
     *   <li>Datei nicht vorhanden → Hinweis, kein Absturz.</li>
     *   <li>Leere Zeilen und Kommentarzeilen werden ignoriert.</li>
     *   <li>Ungültige Zeilen → Warnung, Zeile übersprungen.</li>
     * </ul>
     */
    static void zeigeHighscores() {
        System.out.println();
        System.out.println("┌──────────────────────────────────────┐");
        System.out.println("│       🏆  TOP-HIGHSCORES  🏆         │");
        System.out.println("└──────────────────────────────────────┘");

        File datei = new File(HIGHSCORE_DATEI);

        if (!datei.exists()) {
            System.out.println("ℹ️  Noch keine Highscores vorhanden. Spielen Sie erst eine Runde!");
            return;
        }

        List<HighscoreEintrag> eintraege = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(datei), StandardCharsets.UTF_8))) {

            String zeile;
            int zeilenNummer = 0;

            while ((zeile = reader.readLine()) != null) {
                zeilenNummer++;
                zeile = zeile.trim();

                // Leerzeilen und Kommentare überspringen
                if (zeile.isEmpty() || zeile.startsWith("#")) {
                    continue;
                }

                String[] felder = zeile.split(";", -1);

                if (felder.length != 2) {
                    System.out.printf("⚠️  Warnung (Highscore-Zeile %d): Ungültiges Format – übersprungen.%n",
                            zeilenNummer);
                    continue;
                }

                String spielerName = felder[0].trim();
                int punkteWert;

                try {
                    punkteWert = Integer.parseInt(felder[1].trim());
                } catch (NumberFormatException e) {
                    System.out.printf("⚠️  Warnung (Highscore-Zeile %d): Ungültige Punktzahl \"%s\" – übersprungen.%n",
                            zeilenNummer, felder[1].trim());
                    continue;
                }

                eintraege.add(new HighscoreEintrag(spielerName, punkteWert));
            }

        } catch (IOException e) {
            System.out.println("❌ FEHLER beim Lesen der Highscore-Datei: " + e.getMessage());
            return;
        }

        if (eintraege.isEmpty()) {
            System.out.println("ℹ️  Die Highscore-Datei enthält keine gültigen Einträge.");
            return;
        }

        // Absteigend nach Punkten sortieren
        Collections.sort(eintraege);

        // Top-Liste ausgeben
        int anzahlAusgabe = Math.min(TOP_LISTE, eintraege.size());
        System.out.printf("%-9s %-20s %s%n", "Rang", "Name", "Punkte");
        System.out.println("─".repeat(42));

        for (int i = 0; i < anzahlAusgabe; i++) {
            HighscoreEintrag eintrag = eintraege.get(i);
            String rang = switch (i) {
                case 0 -> "🥇  1.";
                case 1 -> "🥈  2.";
                case 2 -> "🥉  3.";
                default -> "    " + (i + 1) + ".";
            };
            System.out.printf("%-12s %-20s %d%n", rang, eintrag.name, eintrag.punkte);
        }

        System.out.println("─".repeat(42));
    }
}
