public class Main {

    public static void main(String[] args) {
        boolean isMinimal = false;
        String weightsFilePath;
        String valuesFilePath;
        int kw;

        if(isMinimal){
            weightsFilePath = Problem.minimalKnapsackWeightsFilePath;
            valuesFilePath = Problem.minimalKnapsackValuesFilePath;
            kw = Problem.minimalKnapsackMaxWeight;
        }
        else {
            weightsFilePath = Problem.mainKnapsackWeightsFilePath;
            valuesFilePath = Problem.mainKnapsackValuesFilePath;
            kw = Problem.mainKnapsackMaxWeight;
        }

        try {
            GeneticKnapsackProblem geneticKnapsackProblem =
                    new GeneticKnapsackProblem(kw, 500);
            geneticKnapsackProblem.setWeightsFilePath(weightsFilePath);
            geneticKnapsackProblem.setValuesFilePath(valuesFilePath);
            geneticKnapsackProblem.setPopulationSize(500);
            geneticKnapsackProblem.setChildrenSize(2000);
            geneticKnapsackProblem.setCrossoverPointsCount(6);
            geneticKnapsackProblem.loadKnapsackItems();
            geneticKnapsackProblem.solve();
        }
        catch (Exception ex){
            System.out.println("Exception: " + ex.getMessage());
            ex.printStackTrace();
        }

        //KnapSackProblem.start(null);
    }
}
