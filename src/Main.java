public class Main {
    private static ProblemData problemData = ProblemData.getInstance();
    private static GAConfigs gaConfigs = GAConfigs.getInstance();
    private static GeneticKnapsackProblem geneticKnapsackProblem;

    public static void main(String[] args) {
        try { problemData.loadKnapsackData(ProblemData.MAIN_PROBLEM_DATA); }
        catch (Exception ex){
            System.out.println("\n" + ex.getMessage());
        }

        while (mainMenu());
    }

    public static boolean mainMenu(){
        StylishPrinter.println("\nMenu:", StylishPrinter.BOLD_RED);
        System.out.println("1: Load Items");
        System.out.println("2: Show Items");
        System.out.println("3: Genetic Algorithm Settings");
        System.out.println("4: Genetic Solve");
        System.out.println("5: Dynamic Programming Solve");
        System.out.println("6: Exit");
        System.out.print("\nEnter Your Choice: ");
        int choice = SBProScanner.inputInt(1, 6);

        if(choice==1) loadItems();
        else if(choice == 2) problemData.printKnapsackItems();
        else if(choice == 3) while (gaConfigs.menu());
        else if(choice == 4) geneticSolve();
        else if(choice == 5) DynamicKnapsackProblem.start();
        else if(choice == 6) return false;
        return true;
    }

    private static void loadItems() {
        StylishPrinter.println("\nLoad Items:", StylishPrinter.BOLD_RED);
        System.out.println("1: Load Minimal Data Set");
        System.out.println("2: Load Main Data Set");
        System.out.println("3: Back");
        System.out.print("\nEnter Your Choice: ");
        int choice = SBProScanner.inputInt(1, 5);

        try {
            if (choice == 1) problemData.loadKnapsackData(ProblemData.MINIMAL_PROBLEM_DATA);
            else if (choice == 2) problemData.loadKnapsackData(ProblemData.MAIN_PROBLEM_DATA);
        }
        catch (Exception ex){
            System.out.println("\n" + ex.getMessage());
        }
    }

    private static void geneticSolve() {
        try {
            geneticKnapsackProblem = new GeneticKnapsackProblem();
            geneticKnapsackProblem.setPopulationSize(gaConfigs.populationSize);
            geneticKnapsackProblem.setChildrenSize(gaConfigs.childrenSize);
            geneticKnapsackProblem.setCrossoverPointsCount(gaConfigs.crossoverPointsCount);
            geneticKnapsackProblem.setMaxGenerationNumber(gaConfigs.maxGenerationNumber);
            geneticKnapsackProblem.setScalingFitnessesInChoosingParents(
                    gaConfigs.scalingFitnessesInChoosingParents);
            geneticKnapsackProblem.setScalingFitnessesInSurvivorsSelection(
                    gaConfigs.scalingFitnessesInSurvivorsSelection);
            geneticKnapsackProblem.setSurvivorsSelectionMode(gaConfigs.survivorsSelectionMode);
            geneticKnapsackProblem.solve();
        }
        catch (Exception ex){
            System.out.println("\nException: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
