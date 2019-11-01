import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProblemData {
    public Map<Integer, KnapSackItem> knapsackItems;
    public int knapsackMaxWeight;
    boolean hasData = false;
    private static ProblemData single_instance = null;

    private static int minimalKnapsackMaxWeight = 165;
    private static String minimalKnapsackWeightsFilePath = "minimal_weights.txt";
    private static String minimalKnapsackValuesFilePath = "minimal_values.txt";
    private static int mainKnapsackMaxWeight = 6404180;
    private static String mainKnapsackWeightsFilePath = "weights.txt";
    private static String mainKnapsackValuesFilePath = "values.txt";
    public static final int MINIMAL_PROBLEM_DATA = 1;
    public static final int MAIN_PROBLEM_DATA = 2;

    private ProblemData() {
        knapsackItems = new HashMap<>();
        hasData = false;
    }

    public static ProblemData getInstance() {
        if (single_instance == null)
            single_instance = new ProblemData();

        return single_instance;
    }

    public void loadKnapsackItems(String weightsFilePath, String valuesFilePath) throws IOException {
        String weightsContents = UsefulUtils.readAllFile(weightsFilePath);
        String valuesContents = UsefulUtils.readAllFile(valuesFilePath);
        String[] weightPieces = weightsContents.split("\n");
        String[] valuesPieces = valuesContents.split("\n");

        knapsackItems = new HashMap();
        for(int i=0; weightPieces.length>i; i++){
            KnapSackItem knapSackItem = new KnapSackItem(
                    i+1,
                    Integer.valueOf(weightPieces[i].trim()),
                    Integer.valueOf(valuesPieces[i].trim()));

            knapsackItems.put(i+1, knapSackItem);
        }

        printKnapsackItems();
    }

    public void loadKnapsackData(int dataset) throws IOException {
        if(dataset == MINIMAL_PROBLEM_DATA) {
            loadKnapsackItems(minimalKnapsackWeightsFilePath, minimalKnapsackValuesFilePath);
            knapsackMaxWeight = minimalKnapsackMaxWeight;
        }
        else {
            loadKnapsackItems(mainKnapsackWeightsFilePath, mainKnapsackValuesFilePath);
            knapsackMaxWeight = mainKnapsackMaxWeight;
        }
        hasData = true;
    }

    public void printKnapsackItems(){
        System.out.println("\nKnapsack Items: ");
        for(int i=1; knapsackItems.size()>=i; i++)
            System.out.println("Item " + i + ": " + knapsackItems.get(i).toString());
        System.out.println();
    }
}
