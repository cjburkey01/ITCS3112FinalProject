package com.cjburkey.itcs3112;

import com.cjburkey.itcs3112.cmd.CommandHandler;
import com.cjburkey.itcs3112.cmd.CmdsImpl;

import java.util.*;

public class ScheduleHandler {

    // The currently selected course
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<ClassSchedule> currentSchedule = Optional.empty();

    // Command handlers
    public final CommandHandler scheduleCommands = new CommandHandler();
    public final CommandHandler courseCommands = new CommandHandler();

    // Map from course names to their respective schedules
    private final HashMap<String, ClassSchedule> courseSchedules = new HashMap<>();

    public ScheduleHandler() {
        // Main commands
        scheduleCommands.addCommand(new CmdsImpl.CmdHelp(this));
        scheduleCommands.addCommand(new CmdsImpl.CmdAddCourse(this));
        scheduleCommands.addCommand(new CmdsImpl.CmdRemCourse(this));
        scheduleCommands.addCommand(new CmdsImpl.CmdListCourse(this));
        scheduleCommands.addCommand(new CmdsImpl.CmdSelectCourse(this));
        scheduleCommands.addCommand(new CmdsImpl.CmdShow(this));

        // Course commands
        courseCommands.addCommand(new CmdsImpl.CmdAddItem(this));
        courseCommands.addCommand(new CmdsImpl.CmdRemItem(this));
        courseCommands.addCommand(new CmdsImpl.CmdShowItem(this));
        courseCommands.addCommand(new CmdsImpl.CmdListItem(this));
    }

    /**
     * Get an unmodifiable collection of the course schedules.
     *
     * @return The collection of course schedules.
     */
    public Collection<ClassSchedule> getCourseSchedules() {
        return Collections.unmodifiableCollection(courseSchedules.values());
    }

    /**
     * Select the course by the provided name.
     *
     * @param name The name of the course to select, or {@code null} to deselect.
     * @return Whether there is a course by the provided name.
     */
    public boolean selectCourse(String name) {
        if (name != null) {
            // Get the course by the provided name
            ClassSchedule schedule = courseSchedules.get(name);
            if (schedule == null) {
                return false;
            }

            // Update the current course
            currentSchedule = Optional.of(schedule);
        } else {
            // Deselect
            currentSchedule = Optional.empty();
        }

        return true;
    }

    /**
     * Create a new course schedule.
     *
     * @param name The name of the course.
     * @return Whether the course was created. If {@code false}, a course by that name has already been created.
     */
    public boolean addCourse(String name) {
        // Return false if the course is already registered
        if (courseSchedules.containsKey(name)) {
            return false;
        }

        // Otherwise, create & insert a new course schedule.
        courseSchedules.put(name, new ClassSchedule(name));
        return true;
    }

    /**
     * Remove the course by the provided name.
     *
     * @param name The name of the course to remove.
     * @return Whether the course was removed. If {@code false}, there was not a course registered with the provided name.
     */
    public boolean removeCourse(String name) {
        return courseSchedules.remove(name) != null;
    }

    /**
     * Attempts to execute the command in the input.
     *
     * @param input The user's input.
     * @return Whether there is a command by the name provided in the input.
     */
    public boolean execute(String input) {
        // First, try to execute a course command
        if (currentSchedule.isPresent() && courseCommands.execute(input)) {
            return true;
        }

        // If it wasn't one of them, try a schedule command
        return scheduleCommands.execute(input);
    }

    /**
     * Get the currently selected course.
     *
     * @return An optional-wrapped course.
     */
    public Optional<ClassSchedule> getCurrentSchedule() {
        return currentSchedule;
    }

}
