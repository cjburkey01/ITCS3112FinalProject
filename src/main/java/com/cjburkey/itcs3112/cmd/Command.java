package com.cjburkey.itcs3112.cmd;

import com.cjburkey.itcs3112.ScheduleHandler;

/**
 * A command that the user may use.
 */
public abstract class Command {

    public final String name;
    public final int requiredArgs;
    public final String[] argNames;

    protected final ScheduleHandler scheduleHandler;

    public Command(String name, ScheduleHandler scheduleHandler, int requiredArgs, String... argNames) {
        this.name = name;
        this.requiredArgs = requiredArgs;
        this.argNames = argNames;
        this.scheduleHandler = scheduleHandler;
    }

    /**
     * Method called when this command is executed.
     *
     * @param args The provided whitespace-delimited arguments.
     */
    public abstract void execute(String[] args);

    /**
     * Get the description to be displayed when using the help command.
     *
     * @return This command's description.
     */
    public abstract String getDescription();

    /**
     * Get this command's usage string.
     *
     * @return User-facing command directions.
     */
    public String getUsageString() {
        StringBuilder str = new StringBuilder(name);
        for (int i = 0; i < argNames.length; i ++) {
            str.append(' ');

            boolean req = i < requiredArgs;
            str.append(req ? '<' : '[');
            str.append(argNames[i]);
            str.append(req ? '>' : ']');
        }
        return str.toString();
    }

}
