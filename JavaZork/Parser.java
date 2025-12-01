import java.util.Scanner;

public class Parser {
    private CommandWords commands;
    private Scanner reader;

    public Parser() {
        commands = new CommandWords();
        reader = new Scanner(System.in);
    }

    public Command getCommand() {
        System.out.print("> ");
        String inputLine = reader.nextLine();
        return getCommand(inputLine);
    }

    public Command getCommand(String inputLine) {

        String word1 = null;
        String word2 = null;

        Scanner tokenizer = new Scanner(inputLine.toLowerCase());
        if (tokenizer.hasNext()) {
            word1 = tokenizer.next();
            if (tokenizer.hasNext()) {
                word2 = "";
                while (tokenizer.hasNext()) {
                    word2 = word2.concat(tokenizer.next());
                    if (tokenizer.hasNext()) {
                        word2 = word2.concat(" ");
                    }
                }
            }
        }

        if (commands.isCommand(word1)) {
            return new Command(word1, word2);
        } else {
            return new Command(null, word2);
        }
    }

    public void showCommands() {
        commands.showAll();
    }
}
