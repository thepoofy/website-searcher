package com.thepoofy.website_searcher;

import com.thepoofy.website_searcher.models.Job;

/**
 * Executes jobs issues by the JobManager
 * 
 * @author wvanderhoef
 */
public class JobWorker implements Runnable {

    private final JobManager jobs;
    private final String     sequence;

    public JobWorker(JobManager jobManager, String sequence) {

        this.jobs = jobManager;
        this.sequence = sequence;
    }

    @Override
    public void run() {

        boolean isFinished = false;

        while (!isFinished) {

            try {

                //request job from job pool
                Job j = this.jobs.getNext();


                // execute job
                boolean result = this.fetchUrl().contains(sequence);


                // report back result
                jobs.complete(j.getUuid(), result);


            } catch (JobUnavailableException e) {
                isFinished = true;
            }
        }
    }

    private String fetchUrl() {
        // TODO
        return "";
    }
}
