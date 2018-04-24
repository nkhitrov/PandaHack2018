
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVreader {
    BufferedReader br = null;
    String line = "";
    final String csvFile;

    public CSVreader(String csvFile) {
        this.csvFile = csvFile;
    }

    public ArrayList<String> scanFile() {
        ArrayList<String> list = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }


}