import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        int minimalKnapsackMaxWeight = 165;
        String minimalKnapsackWeightsFilePath = "minimal_weights.txt";
        String minimalKnapsackValuesFilePath = "minimal_values.txt";
        int mainKnapsackMaxWeight = 6404180;
        String mainKnapsackWeightsFilePath = "weights.txt";
        String mainKnapsackValuesFilePath = "values.txt";

        try {
            GeneticKnapsackProblem geneticKnapsackProblem =
                    new GeneticKnapsackProblem(mainKnapsackMaxWeight, 2000);
            geneticKnapsackProblem.setWeightsFilePath(mainKnapsackWeightsFilePath);
            geneticKnapsackProblem.setValuesFilePath(mainKnapsackValuesFilePath);
            geneticKnapsackProblem.setPopulationSize(500);
            geneticKnapsackProblem.setChildrenSize(5000);
            geneticKnapsackProblem.setCrossoverPointsCount(2);
            geneticKnapsackProblem.loadKnapsackItems();
            geneticKnapsackProblem.solve();
        }
        catch (Exception ex){
            System.out.println("Exception: " + ex.getMessage());
        }

        //KnapSackProblem.start(null);
    }
}
