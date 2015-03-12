package com.thepoofy.website_searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.thepoofy.website_searcher.csv.reader.MozReader;
import com.thepoofy.website_searcher.csv.reader.MozReaderFactory;
import com.thepoofy.website_searcher.csv.writer.MozWriterFactory;
import com.thepoofy.website_searcher.models.MozData;
import com.thepoofy.website_searcher.models.MozResults;

/**
 * Main class for running the website-searcher.
 * 
 * Ideally this would be written as a 2 program/application system 
 * with 1 Manager instance and N Worker instances but rules stated to write "a program".
 * 
 * The JobManager operates as control which knowledge of which websites have been searched and which haven't.
 * It doles out a job to each worker as they request a unit of work via {@link JobManager#getNext()}.  
 * 
 * When the worker is done it responds to with {@link JobManager#complete(java.util.UUID, Boolean)}.
 * This allows the JobManager to reissue work to Workers if one were to fail to complete.
 * 
 * This application currently has the flaw that if the final thread is interrupted the final job won't complete.
 * In a true production system a 3rd party should be querying the JobManager(s) to see if more Workers need to be started.
 * 
 * 
 * I have provided a timed status update thread which queries the JobManager for metrics as a simple monitor of work.
 * 
 * 
 * To run "mvn exec:java" from /website-searcher.  Requires java 7 and maven
 * Default parameters for execution are defined in pom.xml
 * 
 * @author wvanderhoef
 */
public class Application {

    private static final String OUTPUT_FILE_NAME = "results.txt";

    private static Timer        statusTimer      = new Timer();

    public static void main(String[] args) {

        System.out.println("Application started.");
        if (args.length != 3) {
            System.err.println("usage: fileName searchPhrase concurrency");
        }
        try {
            Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("concurrency parameter must be an integer");
            return;
        }

        // accept by parameter
        final String inputFile = args[0];
        final String searchPhrase = args[1];
        final int numWorkers = Integer.parseInt(args[2]);


        MozReader reader = new MozReaderFactory().instanceOf();

        Collection<MozData> data = null;
        try {
            data = reader.fromFile(inputFile);
        } catch (IOException e) {

            // print failure and exit
            e.printStackTrace(System.err);

            return;
        }

        /*
         * The job manager maintains all the state of what jobs have been issued and their results.
         * Upon completion of all jobs this listener will be executed and results of all jobs will be submitted.
         */
        JobManager manager = new JobManager(data, new OnJobsCompleteListener() {

            @Override
            public void onComplete(List<MozResults> resultsList) {

                statusTimer.cancel();

                /*
                 * This callback is made in the worker thread through the JobManager.  
                 * It is the JobManager's responsibility to ensure this is called only one time as multiple calls will cause an IOException.
                 * To ensure only the first caller can write to the file we can synchronize to this class to provide a second layer of security.
                 */
                synchronized (Application.class) {
                    if (!resultsList.isEmpty()) {
                        try {

                            // sort the list back into the same order they were read
                            Collections.sort(resultsList);

                            // RULE #4
                            // write the results to the file
                            new MozWriterFactory().instanceOf().toFile(OUTPUT_FILE_NAME, resultsList);

                            // update console
                            System.out.println("results written.");

                        } catch (IOException ioe) {

                            // an IOException here indicates an issue writing to the file 
                            ioe.printStackTrace(System.err);
                            return;
                        }

                    } else {
                        System.err.println("No results found.");
                    }
                }
            }
        });


        List<Thread> threadList = Collections.synchronizedList(new ArrayList<Thread>());

        // RULE #2 and #3 Concurrent but no more than 20 HTTP requests at a time
        // 20 is set by param argument in pom.xml
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

        // report out status every 10 seconds
        statusTimer.scheduleAtFixedRate(new StatusTask(manager, threadList), 0L, (1000 * 10));

        // wait for all the worker threads to complete
        for (Thread t : threadList) {
            try {
                t.join();
            } catch (InterruptedException ie) {
                System.out.println("A thread died, oh noes.");
            }
        }

        System.out.println("Application finished.");
    }


    /**
     * StatusTask prints to console key metrics for the JobManager and Workers
     * 
     * @author wvanderhoef
     */
    static class StatusTask extends TimerTask {

        private final JobManager   manager;
        private final List<Thread> workersList;

        public StatusTask(JobManager manager, List<Thread> workers) {
            this.manager = manager;
            this.workersList = workers;
        }

        @Override
        public void run() {

            System.out.println("Queued   Jobs: " + manager.getQueueSize());
            System.out.println("Pending  Jobs: " + manager.getIssuedJobsSize());
            System.out.println("Complete Jobs: " + manager.getResultsSize());

            int threadCount = 0;
            for (Thread t : workersList) {
                if (t.isAlive()) {
                    ++threadCount;
                }
            }

            System.out.println("Live  Threads: " + threadCount);

        }
    }
}
