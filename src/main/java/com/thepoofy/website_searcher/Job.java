package com.thepoofy.website_searcher;

import java.util.Date;
import java.util.UUID;

/**
 * 
 * @author wvanderhoef
 */
public class Job implements Comparable<Job> {
    private final UUID    uuid;
    private final MozData datum;
    private final Date    createdOn;
    private Boolean       isSuccessful;

    public Job(MozData datum) {
        this.uuid = UUID.randomUUID();
        this.datum = datum;
        this.createdOn = new Date();
    }

    public UUID getUuid() {
        return uuid;
    }

    public MozData getMozData() {
        return this.datum;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Boolean isSuccessful() {
        return isSuccessful;
    }

    public void setIsSuccessful(Boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    @Override
    public int compareTo(Job o) {

        if (o == null) {
            return -1;
        }

        // this is a very incomplete implementation
        // but for purposes of this exercise sorting by createdOn is sufficient
        return this.getCreatedOn().compareTo(o.getCreatedOn());
    }

}
