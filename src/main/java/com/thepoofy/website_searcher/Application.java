package com.thepoofy.website_searcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Application {

    public static void main(String[] args) {

        // TODO accept by parameter
        final int numWorkers = 20;
        final String searchPhrase = "Welcome";

        String inputFile = "";
        String outputFile = "";

        MozReader reader = new MozReaderFactory().instanceOf();
        
        Collection<MozData> data = reader.fromFile(inputFile);

        /*
         * The job manager maintains all the state of what jobs have been issued and their results.
         * Upon completion of all jobs this listener will be executed and results of all jobs will be submitted.
         */
        JobManager manager = new JobManager(data, new OnJobsCompleteListener() {
            
            @Override
            public void onComplete(List<Job> resultsList) {
                // TODO write out to a file
            }
        });

        List<Thread> threadList = new ArrayList<>();
        
        for (int i = 0; i < numWorkers; ++i) {

            /*
             * Create a worker with a reference to the JobManager.
             * If the worker dies or is interrupted the job will be reissued to the next available worker after a timeout.
             * 
             * This design allows for a scalable number of workers.  
             * It is more effective when built using an auto-scaling group of microservices as clients.  
             * An auto-scaler can ask the JobManager if the queue of work isn't being executed fast enough to bring up more client workers. 
             * 
             */
            JobWorker worker = new JobWorker(manager, searchPhrase);

            Thread t = new Thread(worker);
            t.start();
            threadList.add(t);
        }

        for(Thread t : threadList) {
            try {
                t.join();
            }
            catch (InterruptedException ie) {
                System.out.println("A thread died, oh noes.");
            }
        }
    }


}
