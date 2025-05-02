/**
 * Utilitaire pour gérer les scores de parties.
 * Sauvegarde chaque score (nom, temps) dans un fichier texte
 * et permet de récupérer les 5 meilleurs temps.
 */
package utils;

import java.io.*;
import java.util.*;

public class ScoreManager {
    private static final String SCORE_FILE = "Benfdal_Croizier/scores.txt";

    /**
     * Enregistre un nouveau score à la fin du fichier de scores.
     *
     * @param nom    le nom du joueur
     * @param temps  le temps réalisé en secondes
     */
    public static void saveScore(String nom, int temps) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE, true))) {
            writer.write(nom + "," + temps);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("❌ Impossible de sauvegarder le score : " + e.getMessage());
        }
    }

    /**
     * Lit tous les scores depuis le fichier et renvoie les 5 meilleurs (temps croissants).
     *
     * @return une liste de chaînes « nom - temps sec » pour les 5 meilleurs scores
     */
    public static List<String> getTop5() {
        List<String> lignes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lignes.add(line);
            }
        } catch (IOException e) {
            System.err.println("⚠️ Aucun score trouvé ou erreur de lecture.");
        }

        return lignes.stream()
                .map(l -> l.split(","))
                .filter(parts -> parts.length == 2)
                .map(parts -> new AbstractMap.SimpleEntry<>(parts[0], Integer.parseInt(parts[1])))
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .limit(5)
                .map(entry -> entry.getKey() + " - " + entry.getValue() + " sec")
                .toList();
    }
}
