package controllers;

import job.Job;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created By sethsneddon on Sep, 10 2018.
 */
public class Job_Loader implements IJob_Loader {

    Job job;
//    private int jobType = 0;
    ArrayList<Long> coursesToScrape = new ArrayList<>();
    Collection<List<Long>> partitionedJobs;

    public Job_Loader(){
        job = new Job();
    }

//    public Collection<List<Long>> create_Job(){
//        declare_JobType();
//        load_Job();
//        return partitionedJobs;
//    }

    public Job create_Job(){
        declare_JobType();
        load_Job();
        return job;
    }

    @Override
    public void declare_JobType() {
        Scanner in = new Scanner(System.in);
        int i = 999;
        do {
            try {
                System.out.println("Specify Job Type:\n\n1 General Course-List Scrape\n2 Scrape Courses\n");
                i = in.nextInt();
                switch (i){
                    case 1:
                        job.setJob_CoS(false);
                        job.setJob_CourseList(true);
                        break;
                    case 2:
                        job.setJob_CoS(true);
                        job.setJob_CourseList(false);
                }
            } catch (InputMismatchException e) {
                System.err.println("Please only specify the integer associated with the job.");
            }
        } while (i == 999);
        //todo implement overwriteall or insertonly switch
    }

    @Override
    public void load_Job() {
        if (job.getJob_CourseList()) {
            initialize_ListJob();
        } else {
            initialize_CoSJob();
        }
    }


    @Override
    public void initialize_CoSJob() {
        Scanner in = new Scanner(System.in);
        String fileLocation;

        BufferedReader bufferedReader = null;

        StringBuffer cosPayload = new StringBuffer();

        while (bufferedReader == null) {
            try{
                System.out.println("Please enter path of file containing CoS(s) to scrape:");
                fileLocation = in.next();
                bufferedReader = new BufferedReader(new FileReader(fileLocation));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    coursesToScrape.add(Long.valueOf(line));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("There was a problem with your input file." +
                        "\nPlease only use a numeric value for each record on each line.");
                e.printStackTrace();
            }
        }
        balance_Job();
    }

    @Override
    public void initialize_ListJob() {
        // todo remove?
    }

    // todo maybe give ability
    @Override
    public void balance_Job() {
        int numberCores = Runtime.getRuntime().availableProcessors();
        if (job.getJob_CourseList()) {
            // todo call course-list scraper
        } else {
            int jobSizeTotal = coursesToScrape.size();
            System.out.println("Total courses to scrape: " + jobSizeTotal);
            int remainder = jobSizeTotal % numberCores;
            int rounded = jobSizeTotal / numberCores;
            int roundedAndRemainder = remainder + rounded;

            AtomicInteger counter = new AtomicInteger(0);
//            this.partitionedJobs = coursesToScrape.stream().collect(Collectors.groupingBy(
//                    it -> counter.getAndIncrement() / roundedAndRemainder)).values();
            job.setPartitionedJob(coursesToScrape.stream().collect(Collectors.groupingBy(
                    it -> counter.getAndIncrement() / roundedAndRemainder)).values());
        }
    }
}