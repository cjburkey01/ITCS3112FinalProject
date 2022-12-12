package com.cjburkey.itcs3112.cmd;

import com.cjburkey.itcs3112.ClassSchedule;
import com.cjburkey.itcs3112.ScheduleHandler;
import com.cjburkey.itcs3112.ScheduledItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Implementations for the provided commands.
 */
public final class CmdsImpl {

    // -- MAIN COMMANDS -- //

    public static final class CmdHelp extends Command {

        public CmdHelp(ScheduleHandler scheduleHandler) {
            super("help", scheduleHandler, 0, "command name");
        }

        @Override
        public void execute(String[] args) {
            System.out.println("Global commands:");
            scheduleHandler.scheduleCommands.getCommands().forEach(CmdHelp::printCmd);

            System.out.println();
            System.out.println("Per-course commands:");
            System.out.println("(A course must be selected for these to work!)");
            scheduleHandler.courseCommands.getCommands().forEach(CmdHelp::printCmd);
        }

        private static void printCmd(Command cmd) {
            System.out.printf("  %s\n", cmd.name);
            System.out.printf("    Usage: %s\n", cmd.getUsageString());
            System.out.printf("    Description: %s\n", cmd.getDescription());
        }

        @Override
        public String getDescription() {
            return "Show the possible commands";
        }
    }

    /**
     * Command to create a course.
     */
    public static final class CmdAddCourse extends Command {

        public CmdAddCourse(ScheduleHandler scheduleHandler) {
            super("add-course", scheduleHandler, 1, "name");
        }

        @Override
        public void execute(String[] args) {
            // Try to add the course
            String name = args[0];
            if (scheduleHandler.addCourse(name) && scheduleHandler.selectCourse(name)) {
                System.out.printf("Created & selected course: \"%s\".\n", name);
            } else {
                System.err.printf("Course by name of \"%s\" already exists.\n", name);
            }
        }

        @Override
        public String getDescription() {
            return "Add a new course by the given name";
        }

    }

    /**
     * Command to remove a course.
     */
    public static final class CmdRemCourse extends Command {

        public CmdRemCourse(ScheduleHandler scheduleHandler) {
            super("rem-course", scheduleHandler, 1, "name");
        }

        @Override
        public void execute(String[] args) {
            // Try to remove the course
            String name = args[0];
            if (scheduleHandler.removeCourse(name)) {
                System.out.printf("Removed course: \"%s\".\n", name);
            } else {
                System.err.printf("No course by name \"%s\".\n", name);
            }
        }

        @Override
        public String getDescription() {
            return "Remove the course with the provided name";
        }

    }

    /**
     * Command to list courses and the number of assignments due in the past and present.
     */
    public static final class CmdListCourse extends Command {

        public CmdListCourse(ScheduleHandler scheduleHandler) {
            super("list-course", scheduleHandler, 0);
        }

        @Override
        public void execute(String[] args) {
            // Print courses
            System.out.println("Courses:");
            Collection<ClassSchedule> courses = scheduleHandler.getCourseSchedules();
            for (ClassSchedule course : courses) {
                System.out.printf("- %s (%s due, %s past-due)\n",
                        course.name, course.getFutureDue().size(), course.getPastDue().size());
            }
            if (courses.size() == 0) {
                System.out.println("  No courses! Use `add-course <name>` to create one.");
            }
        }

        @Override
        public String getDescription() {
            return "List all courses";
        }

    }

    /**
     * Command to select course.
     */
    public static final class CmdSelectCourse extends Command {

        public CmdSelectCourse(ScheduleHandler scheduleHandler) {
            super("select-course", scheduleHandler, 0, "name");
        }

        @Override
        public void execute(String[] args) {
            if (args.length > 0) {
                // Try to select the course
                String name = args[0];
                if (scheduleHandler.selectCourse(name)) {
                    System.out.printf("Selected course: \"%s\".\n", name);
                } else {
                    System.err.printf("No course by name \"%s\".\n", name);
                }
            } else {
                if (scheduleHandler.getCurrentSchedule().isPresent()) {
                    scheduleHandler.selectCourse(null);
                    System.out.println("Deselected course");
                } else {
                    System.out.println("No course selected");
                }
            }
        }

        @Override
        public String getDescription() {
            return "Select the course by the provided name, or deselect the current course if no name is provided";
        }

    }

    /**
     * Command to show all courses and their associated items.
     */
    public static final class CmdShow extends Command {

        public CmdShow(ScheduleHandler scheduleHandler) {
            super("show", scheduleHandler, 0);
        }

        @Override
        public void execute(String[] args) {
            // Print courses
            System.out.println("Courses:");
            Collection<ClassSchedule> courses = scheduleHandler.getCourseSchedules();
            for (ClassSchedule course : courses) {
                System.out.printf("  %s:\n", course.name);

                // Print upcoming assignments in order from least to most recent
                System.out.println("    Upcoming:");
                List<ScheduledItem> items
                        = course.getFutureDue().stream().sorted().toList();
                for (ScheduledItem item : items) {
                    System.out.printf("    - [%s] %s\n", formatDate(item.dueDate), item.name);
                }
                if (items.size() == 0) {
                    System.out.println("      None!");
                }

                // Print past-due assignments in order from most to least recent
                System.out.println("    Past-due:");
                items = course.getPastDue().stream().sorted(Collections.reverseOrder()).toList();
                for (ScheduledItem item : items) {
                    System.out.printf("    - [%s] %s\n", formatDate(item.dueDate), item.name);
                }
                if (items.size() == 0) {
                    System.out.println("      None!");
                }
            }
            if (courses.size() == 0) {
                System.out.println("  No courses! Use `add-course <name>` to create one.");
            }
        }

        @Override
        public String getDescription() {
            return "Show all courses and their upcoming/past-due assignments";
        }

    }

    // -- COURSE COMMANDS -- //

    /**
     * Command to add an item to the currently selected course.
     */
    public static final class CmdAddItem extends CourseCommand {

        public CmdAddItem(ScheduleHandler scheduleHandler) {
            super("add-item", scheduleHandler, 2, "name", "due date in format yyyy-MM-dd");
        }

        @Override
        public void executeCourse(ClassSchedule currentCourse, String[] args) {
            // Try to add the item
            String name = args[0];
            final Date date;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(args[1]);
            } catch (ParseException e) {
                System.err.printf("Invalid date format (%s)\n", e.getMessage());
                return;
            }

            // (date will not be null by here)

            currentCourse.addItem(name, date);
            System.out.println("Item added");
        }

        @Override
        public String getDescription() {
            return "Add an item to the currently selected course";
        }
    }

    /**
     * Command similar to `show` but only shows items for the current course.
     */
    public static final class CmdShowItem extends CourseCommand {

        public CmdShowItem(ScheduleHandler scheduleHandler) {
            super("show-item", scheduleHandler, 0);
        }

        @Override
        public void executeCourse(ClassSchedule currentCourse, String[] args) {
            // Print course items
            System.out.println("Course items:");

            // Print upcoming assignments in order from least to most recent
            System.out.println("  Upcoming:");
            List<ScheduledItem> items
                    = currentCourse.getFutureDue().stream().sorted().toList();
            for (ScheduledItem item : items) {
                System.out.printf("  - [%s] %s\n", formatDate(item.dueDate), item.name);
            }
            if (items.size() == 0) {
                System.out.println("    None!");
            }

            // Print past-due assignments in order from most to least recent
            System.out.println("  Past-due:");
            items = currentCourse.getPastDue().stream().sorted(Collections.reverseOrder()).toList();
            for (ScheduledItem item : items) {
                System.out.printf("  - [%s] %s\n", formatDate(item.dueDate), item.name);
            }
            if (items.size() == 0) {
                System.out.println("    None!");
            }
        }

        @Override
        public String getDescription() {
            return "Show all items from the current course";
        }

    }

    /**
     * Lists all items for this course with their index numbers.
     */
    public static final class CmdListItem extends CourseCommand {

        public CmdListItem(ScheduleHandler scheduleHandler) {
            super("list-item", scheduleHandler, 0);
        }

        @Override
        public void executeCourse(ClassSchedule currentCourse, String[] args) {
            // Print course items
            System.out.println("Course items:");
            List<ScheduledItem> items = currentCourse.getItems();
            for (int i = 0; i < items.size(); i ++) {
                ScheduledItem item = items.get(i);
                System.out.printf("  (%s) [%s] %s\n", i, formatDate(item.dueDate), item.name);
            }
        }

        @Override
        public String getDescription() {
            return "List all items & their IDs for the current course";
        }

    }

    public static final class CmdRemItem extends CourseCommand {

        public CmdRemItem(ScheduleHandler scheduleHandler) {
            super("rem-item", scheduleHandler, 1, "id");
        }

        @Override
        public void executeCourse(ClassSchedule currentCourse, String[] args) {
            // Try to remove the item
            try {
                if (currentCourse.removeItem(Integer.parseInt(args[0]))) {
                    System.out.println("Removed item");
                } else {
                    System.err.println("Provided ID was out of range");
                }
            } catch (Exception e) {
                System.err.printf("Invalid ID (must be a number 0-%s, use list-item to see item IDs)\n", currentCourse.getItems().size());
            }
        }

        @Override
        public String getDescription() {
            return "Remove the item with the given ID number from the currently selected course";
        }

    }

    private static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date);
    }

}
