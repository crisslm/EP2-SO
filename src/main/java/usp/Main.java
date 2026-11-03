package usp;

import usp.ReadWriteImplementation.Action.PopulateArray;
import usp.ReadWriteImplementation.Entities.Data;
import usp.ReadWriteImplementation.Graph.GraphView;
import usp.ReadWriteImplementation.Implementation.Readers_and_Writers_Implementation;
import usp.SingleLockImplementation.Graph2.GraphView2;
import usp.SingleLockImplementation.Implementation.Single_Lock_implementation;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> lines = new ArrayList<>();
        String path = "src/main/java/usp/bd.txt";
        PopulateArray populateArray = new PopulateArray(path, lines);
        populateArray.populate();

        Readers_and_Writers_Implementation implementation = new Readers_and_Writers_Implementation();

        List<Data> data = new ArrayList<Data>();

        System.out.println("RUNNING IMPLEMENTATION 1:\n");
        for(int i=0;i<=100;i++){
            int soma = 0;
            for(int j=0;j<50;j++){
                soma += implementation.RWImplementation(100-i, i, lines);
            }
            double total = soma / 50.0;
            System.out.printf("Average time with %d writers and %d reader: %.2fms\n", 100-i, i, total);

            String field = i + "/" + String.valueOf(100 - i);
            Data newData = new Data(field, total);
            data.add(newData);
        }

        System.out.println("RUNNING IMPLEMENTATION 2:\n");
        ArrayList<String> lines2 = new ArrayList<>();
        String path2 = "src/main/java/usp/bd.txt";
        PopulateArray populateArray2 = new PopulateArray(path2, lines2);
        populateArray2.populate();

        Single_Lock_implementation single_lock = new Single_Lock_implementation();

        List<Data> data2 = new ArrayList<Data>();

        for(int i=0;i<=100;i++){
            int soma2 = 0;
            for(int j=0;j<50;j++){
                soma2 += single_lock.SLImplementation(100-i, i, lines2);
            }
            double total2 = soma2 / 50.0;
            System.out.printf("Average time with %d writers and %d readers: %.2fms\n", 100-i, i, total2);

            String field = i + "/" + String.valueOf(100 - i);
            Data newData2 = new Data(field, total2 );
            data2.add(newData2);
        }
        GraphView.run(data);
        GraphView2.run(data2);
    }
}
