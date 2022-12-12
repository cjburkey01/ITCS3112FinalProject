package com.cjburkey.itcs3112.cmd;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class CommandHandler {

    private final HashMap<String, Command> commands = new HashMap<>();

    /**
     * Add the provided command to this handler.
     *
     * @param command The command to add.
     */
    public void addCommand(Command command) {
        commands.put(command.name, command);
    }

    /**
     * Attempt to execute the provided input.
     *
     * @param input The input entered by the user.
     * @return Whether a command handled the input. If {@code false}, there wasn't a command by the name requested.
     */
    public boolean execute(String input) {
        // Split by whitespace
        String[] pieces = input.trim().split("\\s");

        // Make sure a command was provided.
        if (pieces.length < 1) {
            return false;
        }

        // Get the command and the arguments passed to it
        String command = pieces[0];
        String[] args = Arrays.copyOfRange(pieces, 1, pieces.length);

        // Get the command object or return `false` if the command wasn't found
        Command cmd = commands.get(command);
        if (cmd == null) {
            return false;
        }

        // Make sure the correct number of arguments are passed
        if (args.length < cmd.requiredArgs || args.length > cmd.argNames.length) {
            System.err.println("Usage: " + cmd.getUsageString());
        } else {
            // Execute the command
            cmd.execute(args);
        }

        // A command handled the input
        return true;
    }

    public Collection<Command> getCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

}
