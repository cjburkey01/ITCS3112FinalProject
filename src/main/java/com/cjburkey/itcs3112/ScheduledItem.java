package com.cjburkey.itcs3112;

import java.time.Instant;
import java.util.Date;

/**
 * A single item with a due date.
 */
public class ScheduledItem implements Comparable<ScheduledItem> {

    /**
     * The name of this assignment.
     */
    public final String name;

    /**
     * The date this assignment is due.
     */
    public Date dueDate;

    public ScheduledItem(String name, Date dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }

    /**
     * Check whether this item was due before now.
     *
     * @return Whether this item was due before now.
     */
    public boolean isPastDue() {
        Date now = Date.from(Instant.now());
        return dueDate.before(now);
    }

    @Override
    public int compareTo(ScheduledItem o) {
        return dueDate.compareTo(o.dueDate);
    }

}
