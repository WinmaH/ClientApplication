import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Reader {
    public static String COMMA_DELIMITER = ",";
    static double maximum_throughput = 0;
    static int maximum_index = -1;
    //read the number of  partial siddhi apps from configuration files
    static int no_of_partial_siddhi_apps = 2;


    public static void main(String args[]) {

            // read the ML predictions
        try {
            List<List<String>> records = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("./Sample_Output.csv"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(COMMA_DELIMITER);
                    records.add(Arrays.asList(values));
                }
            }

            for (int i = 1; i < records.size(); i++) {
                double throughput = Double.parseDouble(records.get(i).get(6));
                if (maximum_throughput < throughput) {
                    maximum_throughput = throughput;
                    maximum_index = i;
                }

            }
            System.out.println("Maximum Throughput : " + maximum_throughput);
            System.out.println("No of workers : " + records.get(maximum_index).get(1));
            System.out.println("No of Partial Siddhi Apps: " + records.get(maximum_index).get(2));


            // run the deploy.sh files

            ProcessBuilder pb = new ProcessBuilder("bash", "/home/winma/Documents/BashClient/ML/PredictionsReader/src/auto_deploy.sh", records.get(maximum_index).get(1));
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }


            // sleep for some time till the deployment process completes(1 min)

            Thread.sleep(1000);

            // while true loop for continuously check whether their are empty workers

            while (true) {
                // check whether all the partial siddhi apps are deployed
                ProcessBuilder pb_new = new ProcessBuilder("bash", "/home/winma/Documents/BashClient/ML/PredictionsReader/src/observe_siddhi_apps_are_deployed.sh", records.get(maximum_index).get(1));
                Process p_new = pb_new.start();
                BufferedReader reader_new = new BufferedReader(new InputStreamReader(p_new.getInputStream()));
                String line_new ;
                String app_count = null;
                while ((line_new = reader_new.readLine()) != null) {
                    if (line_new != null){
                        app_count = line_new;
                    }

                }
                Thread.sleep(20000);
                System.out.println("Thread Sleeping ................");
                int siddhi_apps_count = 0;
                siddhi_apps_count = Integer.parseInt(app_count);


                if (siddhi_apps_count == no_of_partial_siddhi_apps) {
                    // call the undeploy script
                    System.out.println("Calling the undeploy file .................");
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
