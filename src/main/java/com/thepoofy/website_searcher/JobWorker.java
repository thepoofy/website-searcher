package com.thepoofy.website_searcher;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import com.thepoofy.website_searcher.models.Job;

/**
 * Executes Jobs issues by the JobManager
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

        // Not finished until a JobUnavailableException is thrown
        while (!isFinished) {

            try {

                //request job from job pool
                Job j = this.jobs.getNext();

                // report back result
                jobs.complete(j.getUuid(), processJob(j));

            } catch (JobUnavailableException e) {
                isFinished = true;

                System.out.println("\tNo more jobs.");
            }
        }

        System.out.println("\tworker exiting");
    }

    /**
     * Process the job, returning a true or false.
     * 
     * We could be more intelligent here and implement a retry strategy on HTTP errors.
     * 
     * @param j
     * @return
     */
    private boolean processJob(Job j) {

        try {

            // get page contents
            String pageContents = this.fetchUrl(j);

            // RULE #1
            // simple regex to check for a search term exists on the page
            Pattern p = Pattern.compile(sequence, Pattern.CASE_INSENSITIVE);
            return p.matcher(pageContents).find();

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return false;

    }

    /**
     * 
     * @param j
     * @return
     * @throws IOException
     */
    private String fetchUrl(Job j) throws IOException {

        String uri = j.getMozData().getUrl();

        ClientConfig config = new ClientConfig();
        config.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        config.property(ClientProperties.READ_TIMEOUT, 1000);

        Client client = ClientBuilder.newClient(config);

        Response response = client
                .target("http://" + uri)
                .request()
                .get();


        System.out.println("URL: " + uri + "response: " + response.getStatus());

        return resolveResponse(client, response, 0);
    }

    /**
     * Return response, recursively follow redirects (3xx response codes) until either 5 redirects or we get a 2xx.
     * 
     * Throw an IOException for 4xx and 5xx in addition to excessive redirects.
     * 
     * @param client
     * @param response
     * @param redirectCount
     * @return
     * @throws IOException
     */
    private String resolveResponse(Client client, Response response, int redirectCount) throws IOException {

        if (response.getStatus() / 100 == 2) {

            return response.readEntity(String.class);
        } else if (response.getStatus() / 100 == 3 && redirectCount < 5) {

            // follow up to 5 redirects
            redirectCount++;
            return resolveResponse(client, client.target(response.getLocation()).request().get(), redirectCount);

        } else {

            throw new IOException(response.getLocation() + " unavailable response: " + response.getStatus());
        }
    }
}
