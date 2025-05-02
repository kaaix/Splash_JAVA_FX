/**
 * Représente une commande saisie par l’utilisateur, décomposée
 * en un type (CommandType) et une liste d’arguments.
 */
package Modeles.core;

import java.util.Arrays;
import java.util.List;

public class Command {
    
    private CommandType type;
    private List<String> args;


    private Command(CommandType type, List<String> args) {
        this.type = type;
        this.args = args;
    }

    /**
     * Parse la ligne de commande et construit une instance de Command.
     *
     * @param input la chaîne brute saisie (ex. "GO NORTH")
     * @return une nouvelle Command avec son type et ses arguments
     * @throws IllegalArgumentException si le type de commande n’est pas reconnu
     */
    public static Command FromString(String input) {
        String upper = input.toUpperCase();
        String[] tokens = upper.split(" ");
        CommandType resType = CommandType.valueOf(tokens[0]);

        String[] argsArr = input.split(" ");
        List<String> args = Arrays.asList(argsArr).subList(1, argsArr.length);
        return new Command(resType, args);
    }

    /**
     * Retourne le type de la commande (HELP, LOOK, QUIT, GO).
     *
     * @return le CommandType de cette commande
     */
    public CommandType getType() {
        return this.type;
    }

    /**
     * Renvoie l’argument à l’indice spécifié.
     *
     * @param i index de l’argument (0-based)
     * @return l’argument sous forme de chaîne
     * @throws IndexOutOfBoundsException si l’indice est invalide
     */
    public String get(int i) {
        return this.args.get(i);
    }

    /**
     * Indique le nombre d’arguments attachés à la commande.
     *
     * @return nombre d’arguments
     */
    public int length() {
        return this.args.size();
    }

}
