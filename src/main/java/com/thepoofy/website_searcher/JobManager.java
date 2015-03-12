package com.thepoofy.website_searcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.thepoofy.website_searcher.models.Job;
import com.thepoofy.website_searcher.models.MozData;
import com.thepoofy.website_searcher.models.MozResults;

/**
 * JobManager is the control for all work.
 * 
 * @author wvanderhoef
 */
public class JobManager {

    private static final long            CLEANUP_TASK_DELAY    = 0L;
    private static final long            CLEANUP_TASK_INTERVAL = 1000 * 60;
    private static final long            JOB_DEADLINE          = 1000 * 60 * 2;

    private final Queue<MozData>         queue;
    private final Map<UUID, Job>         issuedJobs;
    private final List<MozResults>       results;
    private final Timer                  cleanupTimer;

    private final OnJobsCompleteListener listener;
    private final AtomicBoolean          isComplete;

    /**
     * 
     * @param mozDataList
     * @param listener To be called when all issued jobs have been executed
     */
    public JobManager(Collection<MozData> mozDataList, OnJobsCompleteListener listener) {

        if (mozDataList == null || mozDataList.isEmpty()) {
            throw new IllegalArgumentException("No Moz records provided to JobManager.");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Completion Listener must be registered.");
        }

        this.issuedJobs = new ConcurrentHashMap<>();
        this.results = Collections.synchronizedList(new ArrayList<MozResults>());

        // initialize a concurrency safe queue with all the Moz records 
        this.queue = new PriorityBlockingQueue<>(mozDataList.size());
        this.queue.addAll(mozDataList);

        // purge the list of issued jobs every so often
        this.cleanupTimer = new Timer();
        this.cleanupTimer.scheduleAtFixedRate(new CleanupTask(), CLEANUP_TASK_DELAY, CLEANUP_TASK_INTERVAL);

        this.listener = listener;
        this.isComplete = new AtomicBoolean(false);
    }

    /**
     * Request the next available job.
     * 
     * @return
     * @throws JobUnavailableException if all jobs have been issued
     */
    public Job getNext() throws JobUnavailableException {

        System.out.println("Requesting next job.");
        MozData nextUrl = queue.poll();

        if (nextUrl == null) {
            throw new JobUnavailableException();
        }

        Job j = new Job(nextUrl);
        issuedJobs.put(j.getUuid(), j);

        return j;
    }

    /**
     * Submit the result of a job.
     * 
     * @param uuid
     * @param isSuccessful
     */
    public void complete(UUID uuid, Boolean isSuccessful) {
        Job j = issuedJobs.remove(uuid);

        // check to see if the job took too long to complete and has been reissued
        if (j != null) {
            j.setIsSuccessful(isSuccessful);
            results.add(MozResults.instanceOf(j.getMozData(), isSuccessful));
        }

        // if there are no issued jobs or work left to be issued as a job then complete
        if (issuedJobs.isEmpty() && queue.isEmpty() && isComplete.compareAndSet(false, true)) {

            // stop the timers
            this.shutdown();
            listener.onComplete(this.results);
        }
    }


    /**
     * If a thread is killed by a chaos monkey or interrupted for whatever reason the Job must be reissued.
     * As a separate process JobManager reviews issued jobs and removes any which have taken too long to respond.
     * 
     * This is overkill for the project scope but crucial to the correctness of the design.
     */
    public void resetOverdueJobs() {

        Set<Entry<UUID, Job>> entries = issuedJobs.entrySet();


        Iterator<Entry<UUID, Job>> itr = entries.iterator();
        while (itr.hasNext()) {

            Entry<UUID, Job> entry = itr.next();
            Job job = entry.getValue();

            long dueTime = entry.getValue().getCreatedOn().getTime() + JOB_DEADLINE;

            if (new Date().after(new Date(dueTime))) {

                // put the MozData back into the jobs queue
                queue.add(job.getMozData());

                // remove the job from the issuedJobs map
                System.out.println("resetting job for " + job.getMozData().getUrl());
                itr.remove();
            }
        }
    }

    public int getQueueSize() {
        return queue.size();
    }

    public int getIssuedJobsSize() {
        return issuedJobs.size();
    }

    public int getResultsSize() {
        return results.size();
    }
    
    public void shutdown() {
        this.cleanupTimer.cancel();
    }

    /**
     * CleanupTask checks all issued jobs to see if any are overdue and
     * 
     * @author wvanderhoef
     */
    class CleanupTask extends TimerTask {

        @Override
        public void run() {
            resetOverdueJobs();
        }

    }
}
