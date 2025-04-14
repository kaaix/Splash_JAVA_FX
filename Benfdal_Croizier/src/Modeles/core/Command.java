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

    public static Command FromString(String input) {
        String upper = input.toUpperCase();
        String[] tokens = upper.split(" ");
        CommandType resType = CommandType.valueOf(tokens[0]);

        String[] argsArr = input.split(" ");
        List<String> args = Arrays.asList(argsArr).subList(1, argsArr.length);
        return new Command(resType, args);
    }

    public CommandType getType() {
        return this.type;
    }

    public String get(int i) {
        return this.args.get(i);
    }

    public int length() {
        return this.args.size();
    }

}
