package com.cjburkey.itcs3112;

import java.util.Scanner;

/**
 * The main class.
 */
public class CJsSchedules {

    private static final String PROMPT = ">> ";

    /**
     * The main schedule handler for the application.
     */
    public final ScheduleHandler scheduleHandler = new ScheduleHandler();

    /**
     * Hijack the main thread and start the application.
     */
    public void start() {
        System.out.println("Welcome to CJ's Schedule System!");
        System.out.print(PROMPT);

        // Auto-close try blocks for the win
        try (Scanner scanner = new Scanner(System.in)) {
            String input;
            // Keep looping
            while ((input = scanner.nextLine()) != null) {
                input = input.trim();

                // Exit command
                if (input.equals("exit")) {
                    break;
                }

                // Try to execute the provided input
                if (!scheduleHandler.execute(input)) {
                    System.err.println("Unknown command");
                }

                System.out.print(PROMPT);
            }
        }
    }

    // -- STATIC -- //

    public static void main(String[] args) {
        // Initialize & start the application
        CJsSchedules system = new CJsSchedules();
        system.start();
    }

}
