package com.thepoofy.website_searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thepoofy.website_searcher.csv.reader.MozReader;
import com.thepoofy.website_searcher.csv.reader.MozReaderFactory;
import com.thepoofy.website_searcher.csv.writer.MozWriterFactory;
import com.thepoofy.website_searcher.models.MozData;
import com.thepoofy.website_searcher.models.MozResults;

public class Application {

    private static final String OUTPUT_FILE_NAME = "results.txt";
    
    public static void main(String[] args) {

        System.out.println("Application started.");
        if (args.length != 3) {
            System.err.println("usage: fileName searchPhrase concurrency");
        }

        // TODO accept by parameter
        final String inputFile = args[0];
        final String searchPhrase = args[1];
        final int numWorkers = Integer.parseInt(args[2]);

        

        MozReader reader = new MozReaderFactory().instanceOf();

        Collection<MozData> data = null;
        try {
            data = reader.fromFile(inputFile);
        } catch (IOException e) {

            // print failure and exit
            System.err.println("Unable to load file." + e.getMessage());
            return;
        }

        /*
         * The job manager maintains all the state of what jobs have been issued and their results.
         * Upon completion of all jobs this listener will be executed and results of all jobs will be submitted.
         */
        JobManager manager = new JobManager(data, new OnJobsCompleteListener() {

            @Override
            public void onComplete(List<MozResults> resultsList) {
                // TODO write out to a file
                
                try {
                    new MozWriterFactory().instanceOf().toFile(OUTPUT_FILE_NAME, resultsList);
                } catch (IOException ioe) {
                    System.err.println("Unable to print results to file");
                    return;
                }
                
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

        for (Thread t : threadList) {
            try {
                t.join();
            } catch (InterruptedException ie) {
                System.out.println("A thread died, oh noes.");
            }
        }
        
        System.out.println("Application finished.");
    }


}
