package com.cjburkey.itcs3112;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * The schedule for a single course.
 */
public class ClassSchedule {

    /**
     * This course's name.
     */
    public final String name;

    private final ArrayList<ScheduledItem> scheduledItemList = new ArrayList<>();

    public ClassSchedule(String name) {
        this.name = name;
    }

    /**
     * Adds an item with the provided name and due date to this course.
     *
     * @param name The name of the item.
     * @param dueDate The due date of this item.
     */
    public void addItem(String name, Date dueDate) {
        scheduledItemList.add(new ScheduledItem(name, dueDate));
    }

    /**
     * Remove the scheduled item at the provided index.
     *
     * @param id The index of the item to remove from the schedule.
     * @return Whether the item was successfully removed.
     */
    public boolean removeItem(int id) {
        if (id < 0 || id >= scheduledItemList.size()) {
            return false;
        }

        return scheduledItemList.remove(id) != null;
    }

    /**
     * Get a reference to the item at the provided index.
     *
     * @param id The index from which to retrieve the item.
     * @return The item at the index, or {@code null} if out of bounds.
     */
    public ScheduledItem getItem(int id) {
        if (id < 0 || id >= scheduledItemList.size()) {
            return null;
        }
        return scheduledItemList.get(id);
    }

    /**
     * Get an unmodifiable list of the items from this course.
     * This list includes items due in the past and future.
     *
     * @return The list of items in this course.
     */
    public List<ScheduledItem> getItems() {
        return Collections.unmodifiableList(scheduledItemList);
    }

    /**
     * Get an unmodifiable list of past-due schedule items.
     *
     * @return The list of items that were due before now.
     */
    public List<ScheduledItem> getPastDue() {
        return getItems().stream().filter(ScheduledItem::isPastDue).toList();
    }

    /**
     * Get an unmodifiable list of schedule items that are due in the future.
     *
     * @return The list of items that are due in the future.
     */
    public List<ScheduledItem> getFutureDue() {
        return getItems().stream().filter(i -> !i.isPastDue()).toList();
    }

}
