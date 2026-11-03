package usp.ReadWriteImplementation.Action;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

public class PopulateArray {
    String path;
    List<String> lines;

    public PopulateArray(String path, List<String> lines) {
        this.path = path;
        this.lines = lines;
    }

    public void populate(){
        try(BufferedReader reader = new BufferedReader(new FileReader(path))){
            String line;

            while((line = reader.readLine()) != null){
                lines.add(line);
            }

        } catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
