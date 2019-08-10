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

    public static void main(String args[]) {

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
                // System.out.println(records.get(i).get(6));
                double throughput = Double.parseDouble(records.get(i).get(6));
                if (maximum_throughput < throughput) {
                    maximum_throughput = throughput;
                    maximum_index = i;
                }

            }
            System.out.println("Maximum Throughput : " + maximum_throughput);
            System.out.println("No of workers : " + records.get(maximum_index).get(1));
            System.out.println("No of Partial Siddhi Apps: " + records.get(maximum_index).get(2));




            ProcessBuilder pb = new ProcessBuilder("bash","/home/winma/Documents/BashClient/ML/PredictionsReader/src/auto_deploy.sh", records.get(maximum_index).get(1));
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }

//            ProcessBuilder pb = new ProcessBuilder("ipython","python","/home/winma/Documents/BashClient/ML/PredictionsReader/src/Predicting_Number_of_Workers_updatedDataset-KFold.py");
//            Process p = pb.start();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line = null;
//            while ((line = reader.readLine()) != null)
//            {
//                System.out.println(line);
//            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
