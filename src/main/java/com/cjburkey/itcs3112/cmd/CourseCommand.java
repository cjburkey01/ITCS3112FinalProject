package com.cjburkey.itcs3112.cmd;

import com.cjburkey.itcs3112.ClassSchedule;
import com.cjburkey.itcs3112.ScheduleHandler;

/**
 * A command that is executed on a currently selected course.
 */
public abstract class CourseCommand extends Command {

    public CourseCommand(String name, ScheduleHandler scheduleHandler, int requiredArgs, String... argNames) {
        super(name, scheduleHandler, requiredArgs, argNames);
    }

    @Override
    public final void execute(String[] args) {
        scheduleHandler.getCurrentSchedule().ifPresentOrElse(c -> executeCourse(c, args),
                () -> System.err.println("No currently selected course but we're trying to access a course command! This shouldn't be allowed!"));
    }

    /**
     * Method called when a course command is executed.
     *
     * @param currentCourse The currently selected course
     * @param args The arguments passed to the command.
     */
    public abstract void executeCourse(ClassSchedule currentCourse, String[] args);

}
