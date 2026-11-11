import java.io.*;
import java.util.*;

public class QuizSpiel {

    // GLOBALE VARIABLEN
    static String FRAGEN_DATEI = "data/fragen.txt";
    static String HIGHSCORE_DATEI = "data/highscore.txt";
    static Scanner scanner = new Scanner(System.in);

    // =========================================================
    // 1. AMINE: HAUPTMENÜ & STEUERUNG
    // =========================================================
    public static void main(String[] args) {
        System.out.println("=== LERNQUIZ ZUR PRUEFUNGSVORBEREITUNG ===");

        boolean laeuft = true;
        while (laeuft) {
            System.out.println("\n--- HAUPTMENÜ ---");
            System.out.println("1) Neues Quiz starten");
            System.out.println("2) Highscores anzeigen");
            System.out.println("3) Beenden");
            System.out.print("Deine Auswahl: ");

            String eingabe = scanner.nextLine();

            if (eingabe.equals("1")) {
                spieleQuiz();
            } else if (eingabe.equals("2")) {
                zeigeHighscores();
            } else if (eingabe.equals("3")) {
                System.out.println("Auf Wiedersehen!");
                laeuft = false;
            } else {
                System.out.println("Falsche Eingabe! Bitte 1, 2 oder 3 wählen.");
            }
        }
    }

    // =========================================================
    // 2. PRAXEDE: SPIELLOGIK & FACHAUSWAHL
    // =========================================================
    public static void spieleQuiz() {
        // Mahmouds Methode aufrufen
        List<String> alleFragen = ladeFragen();

        if (alleFragen.size() == 0) {
            System.out.println("Fehler: Keine Fragen gefunden.");
            return;
        }

        // Fächer herausfinden (ohne doppelte Nennungen)
        List<String> faecher = new ArrayList<>();
        for (int i = 0; i < alleFragen.size(); i++) {
            String zeile = alleFragen.get(i);
            String fach = zeile.split(";")[0];
            if (!faecher.contains(fach)) {
                faecher.add(fach);
            }
        }

        // User Story 1: Fach auswählen
        System.out.println("\n--- FACH AUSWÄHLEN ---");
        for (int i = 0; i < faecher.size(); i++) {
            System.out.println((i + 1) + ") " + faecher.get(i));
        }
        System.out.print("Wähle eine Nummer: ");

        int fachWahl = 0;
        try {
            fachWahl = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Ungültige Eingabe!");
            return;
        }

        if (fachWahl < 1 || fachWahl > faecher.size()) {
            System.out.println("Ungültige Zahl!");
            return;
        }

        String gewaehltesFach = faecher.get(fachWahl - 1);

        // Fragen für das gewählte Fach filtern
        List<String> gefilterteFragen = new ArrayList<>();
        for (int i = 0; i < alleFragen.size(); i++) {
            String zeile = alleFragen.get(i);
            if (zeile.startsWith(gewaehltesFach + ";")) {
                gefilterteFragen.add(zeile);
            }
        }

        // Namen abfragen
        System.out.print("\nDein Name: ");
        String name = scanner.nextLine();
        if (name.equals("")) {
            name = "Anonym";
        }

        int punkte = 0;

        System.out.println("\nViel Erfolg, " + name + "!");

        // Fragen stellen
        for (int i = 0; i < gefilterteFragen.size(); i++) {
            String zeile = gefilterteFragen.get(i);
            String[] teile = zeile.split(";");

            // teile[0] = Fach, teile[1] = Frage, teile[2-5] = Antworten, teile[6] = Richtige
            String frage = teile[1];
            String antwort1 = teile[2];
            String antwort2 = teile[3];
            String antwort3 = teile[4];
            String antwort4 = teile[5];
            int richtige = Integer.parseInt(teile[6]);

            System.out.println("\nFrage " + (i + 1) + ": " + frage);
            System.out.println("1) " + antwort1);
            System.out.println("2) " + antwort2);
            System.out.println("3) " + antwort3);
            System.out.println("4) " + antwort4);
            System.out.print("Deine Antwort (1-4): ");

            int antwort = 0;
            try {
                antwort = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Ungültige Eingabe! Zählt als falsch.");
            }

            if (antwort == richtige) {
                System.out.println("Richtig!");
                punkte++;
            } else {
                System.out.println("Falsch! Richtig wäre: " + richtige);
            }
        }

        // User Story 4: Ergebnis
        System.out.println("\n=== ERGEBNIS ===");
        System.out.println("Spieler: " + name);
        System.out.println("Punkte: " + punkte + " von " + gefilterteFragen.size());

        // Mahmouds Methode aufrufen
        speichereHighscore(name, punkte);
    }

    // =========================================================
    // 3. MAHMOUD: DATEIEN LESEN UND SCHREIBEN
    // =========================================================
    public static List<String> ladeFragen() {
        List<String> fragen = new ArrayList<>();
        try {
            FileReader fr = new FileReader(FRAGEN_DATEI);
            BufferedReader br = new BufferedReader(fr);

            String zeile = br.readLine();
            while (zeile != null) {
                if (zeile.length() > 0 && !zeile.startsWith("#")) {
                    fragen.add(zeile);
                }
                zeile = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Fehler beim Lesen der Datei: " + e.getMessage());
        }
        return fragen;
    }

    public static void speichereHighscore(String name, int punkte) {
        try {
            // "true" bedeutet: Daten hinten anhängen, nicht überschreiben
            FileWriter fw = new FileWriter(HIGHSCORE_DATEI, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(name + ";" + punkte);
            bw.newLine();
            bw.close();

            System.out.println("Highscore gespeichert!");
        } catch (Exception e) {
            System.out.println("Fehler beim Speichern: " + e.getMessage());
        }
    }

    // =========================================================
    // 4. MELINA: HIGHSCORES ANZEIGEN UND SORTIEREN
    // =========================================================
    public static void zeigeHighscores() {
        List<String> zeilen = new ArrayList<>();

        try {
            FileReader fr = new FileReader(HIGHSCORE_DATEI);
            BufferedReader br = new BufferedReader(fr);

            String zeile = br.readLine();
            while (zeile != null) {
                if (zeile.length() > 0) {
                    zeilen.add(zeile);
                }
                zeile = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Noch keine Highscores vorhanden.");
            return;
        }

        // Ganz einfache Sortierung (Bubble Sort) - ohne komplizierte Technik!
        for (int i = 0; i < zeilen.size(); i++) {
            for (int j = 0; j < zeilen.size() - 1 - i; j++) {
                // Punkte auslesen
                int punkte1 = Integer.parseInt(zeilen.get(j).split(";")[1]);
                int punkte2 = Integer.parseInt(zeilen.get(j + 1).split(";")[1]);

                // Wenn der nächste mehr Punkte hat, tauschen wir
                if (punkte1 < punkte2) {
                    String temp = zeilen.get(j);
                    zeilen.set(j, zeilen.get(j + 1));
                    zeilen.set(j + 1, temp);
                }
            }
        }

        System.out.println("\n--- TOP 5 HIGHSCORES ---");
        for (int i = 0; i < zeilen.size(); i++) {
            if (i == 5) break; // Nur die besten 5 zeigen

            String[] teile = zeilen.get(i).split(";");
            System.out.println((i + 1) + ". " + teile[0] + " - " + teile[1] + " Punkte");
        }
    }
}