public class GAConfigs {
    private static GAConfigs single_instance = null;
    public int populationSize;
    public int crossoverPointsCount;
    public int maxGenerationNumber;
    public int childrenSize;
    public boolean scalingFitnessesInChoosingParents;
    public boolean scalingFitnessesInSurvivorsSelection;
    public int survivorsSelectionMode;

    private GAConfigs() {
        populationSize = 100;
        crossoverPointsCount = 1;
        maxGenerationNumber = 500;
        childrenSize = 500;
        scalingFitnessesInChoosingParents = false;
        scalingFitnessesInSurvivorsSelection = false;
        int survivorsSelectionMode = GeneticKnapsackProblem.MU_AND_LAMBDA_MODE;
    }

    public static GAConfigs getInstance() {
        if (single_instance == null)
            single_instance = new GAConfigs();

        return single_instance;
    }

    public boolean menu(){
        StylishPrinter.println("\nGA Configs:", StylishPrinter.BOLD_RED);
        System.out.println("1: Population size: " + populationSize);
        System.out.println("2: Children Size: " + childrenSize);
        System.out.println("3: Number of crossover points: " + crossoverPointsCount);
        System.out.println("4: Max generation number: " + maxGenerationNumber);
        System.out.println("5: Scaling fitnesses in choosing parents: " + scalingFitnessesInChoosingParents);
        System.out.println("6: Scaling fitnesses in survivors selection: " + scalingFitnessesInSurvivorsSelection);
        System.out.println("7: Survivors selection mode: " +
                ((survivorsSelectionMode == GeneticKnapsackProblem.MU_AND_LAMBDA_MODE)
                        ? "µ and λ" : "µ + λ"));
        System.out.println("8: Back");

        System.out.print("\nEnter your settings number: ");
        int choice = SBProScanner.inputInt(1, 8);

        if(choice == 1){
            System.out.print("\nEnter population size: ");
            populationSize = SBProScanner.inputInt(2, 10000);
        }
        else if(choice == 2){
            System.out.print("\nEnter children size: ");
            childrenSize = SBProScanner.inputInt(2, 10000);
        }
        else if(choice == 3){
            System.out.print("\nEnter number of crossover points: ");
            crossoverPointsCount = SBProScanner.inputInt(1, 8);
        }
        else if(choice == 4){
            System.out.print("\nEnter max generation number: ");
            maxGenerationNumber = SBProScanner.inputInt(1, 100000);
        }
        else if(choice == 5){
            System.out.print("\nScaling fitnesses in choosing parents (1:Yes | 2:No): ");
            choice = SBProScanner.inputInt(1, 2);
            scalingFitnessesInChoosingParents = choice == 1;
        }
        else if(choice == 6){
            System.out.print("\nScaling fitnesses in survivors selection (1:Yes | 2:No): ");
            choice = SBProScanner.inputInt(1, 2);
            scalingFitnessesInSurvivorsSelection = choice == 1;
        }
        else if(choice == 7){
            System.out.print("\nSurvivors selection mode (1:µ and λ | 2:µ + λ): ");
            choice = SBProScanner.inputInt(1, 2);
            survivorsSelectionMode = choice == 1 ? GeneticKnapsackProblem.MU_AND_LAMBDA_MODE
                    : GeneticKnapsackProblem.MU_PLUS_LAMBDA_MODE;
        }
        else return false;
        return true;
    }
}
