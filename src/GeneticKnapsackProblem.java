import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GeneticKnapsackProblem {
    private ProblemData problemData;
    private int populationSize = 100;
    List<Individual> population;
    List<Individual> children;
    List<Individual> competitors;
    private long populationLastRouletteEnd;
    private long competitorsLastRouletteEnd;
    private int crossoverPointsCount = 1;
    private int maxGenerationNumber = 200;
    private int generationNumber;
    private int childrenSize;
    Individual optimumIndividual = null;
    int basePopulationFitness;
    private boolean scalingFitnessesInChoosingParents = true;
    private boolean scalingFitnessesInSurvivorsSelection = false;
    public static final int MU_AND_LAMBDA_MODE = 1;
    public static final int MU_PLUS_LAMBDA_MODE = 2;
    private int survivorsSelectionMode = MU_AND_LAMBDA_MODE;

    GeneticKnapsackProblem(){
        problemData = ProblemData.getInstance();
    }

    public void setSurvivorsSelectionMode(int survivorsSelectionMode) {
        this.survivorsSelectionMode = survivorsSelectionMode;
    }

    public void setMaxGenerationNumber(int maxGenerationNumber) {
        this.maxGenerationNumber = maxGenerationNumber;
    }

    public void setChildrenSize(int childrenSize) {
        this.childrenSize = childrenSize;
    }

    public int getCrossoverPointsCount() {
        return crossoverPointsCount;
    }

    public void setCrossoverPointsCount(int crossoverPointsCount) {
        this.crossoverPointsCount = crossoverPointsCount;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public void setScalingFitnessesInChoosingParents(boolean scalingFitnessesInChoosingParents) {
        this.scalingFitnessesInChoosingParents = scalingFitnessesInChoosingParents;
    }

    public void setScalingFitnessesInSurvivorsSelection(boolean scalingFitnessesInSurvivorsSelection) {
        this.scalingFitnessesInSurvivorsSelection = scalingFitnessesInSurvivorsSelection;
    }

    public void generatePopulation(){
        generationNumber = 0;
        population = new ArrayList<>();
        optimumIndividual = null;
        basePopulationFitness = -1;
        Random random = new Random();

        for(int i=0; populationSize>i; i++){
            boolean correct = false;
            List representation = null;
            while(!correct) {
                representation = new ArrayList();
                for (int j = 0; problemData.knapsackItems.size() > j; j++) {
                    representation.add(random.nextBoolean());
                }
                correct = getFitness(representation) > 0;
            }
            Individual individual = new Individual(representation);
            population.add(individual);
        }
        System.out.println();
    }

    public void calculatePopulationFitness(){
        basePopulationFitness = -1;
        for(int i=0; population.size()>i; i++){
            if(population.get(i).fitness == 0) calcFitness(population.get(i));

            if(optimumIndividual == null || population.get(i).fitness > optimumIndividual.fitness) {
                optimumIndividual = population.get(i);
                printLocalOptimumIndividual();
            }

            if(basePopulationFitness == -1 || population.get(i).fitness < basePopulationFitness)
                basePopulationFitness = population.get(i).fitness;
        }
        basePopulationFitness -= 100;
        if(basePopulationFitness < 0) basePopulationFitness = 0;

        for(int i=0; population.size()>i; i++){
            population.get(i).scaledFitness = population.get(i).fitness - basePopulationFitness;
        }

        populationLastRouletteEnd = -1;
        for(int i=0; population.size()>i; i++){
            population.get(i).rouletteStart = populationLastRouletteEnd + 1;
            population.get(i).rouletteEnd =
                population.get(i).rouletteStart
                    + (scalingFitnessesInChoosingParents ? population.get(i).scaledFitness : population.get(i).fitness)
                    - 1;

            if(population.get(i).rouletteStart > population.get(i).rouletteEnd){
                population.get(i).rouletteStart = -1;
                population.get(i).rouletteEnd = -1;
            }
            else populationLastRouletteEnd = population.get(i).rouletteEnd;
        }

        if(generationNumber == 0) printPopulation();
    }

    private void calcFitness(Individual individual){
        individual.fitness = getFitness(individual.representation);
    }

    private int getFitness(List representationList){
        int fitness = 0;
        int sumWeight = 0;
        for (int i = 0; representationList.size() > i; i++) {
            if ((boolean) representationList.get(i)) {
                fitness += problemData.knapsackItems.get(Integer.valueOf(i + 1)).value;
                sumWeight += problemData.knapsackItems.get(Integer.valueOf(i + 1)).weight;
                if (sumWeight > problemData.knapsackMaxWeight) break;
            }
        }
        return sumWeight > problemData.knapsackMaxWeight ? 0 : fitness;
    }

    private List<Individual> chooseParentsWithRouletteWheel() throws Exception{
        List<Individual> parents = new ArrayList<>();
        if(population.size() == 0) throw new Exception("Empty Population!");
        if(population.size() == 1){
            parents.add(population.get(0));
            parents.add(population.get(0));
            return parents;
        }

        Random random = new Random();
        long firstPointer =
                ThreadLocalRandom.current().nextLong(populationLastRouletteEnd + 1);
        Individual firstParent = null;
        for(int i=0; population.size()>i; i++){
            if(firstPointer <= population.get(i).rouletteEnd){
                firstParent = population.get(i);
                break;
            }
        }

        long firstParentRouletteRange = firstParent.rouletteEnd - firstParent.rouletteStart;
        long secondPointer =
                ThreadLocalRandom.current().nextLong(populationLastRouletteEnd + 1 -
                    firstParentRouletteRange);
        if(secondPointer >= firstParent.rouletteStart) secondPointer += firstParentRouletteRange;

        Individual secondParent = null;
        for(int i=0; population.size()>i; i++){
            if(secondPointer <= population.get(i).rouletteEnd){
                secondParent = population.get(i);
                break;
            }
        }

        parents.add(firstParent);
        parents.add(secondParent);
        return parents;
    }

    public void generateChildrenWithRouletteWheel() throws Exception {
        int targetChildrenSize = childrenSize;
        children = new ArrayList<>();
        while(children.size() < targetChildrenSize){
            List<Individual> parents = chooseParentsWithRouletteWheel();
            children.addAll(parents.get(0).crossover(parents.get(1), crossoverPointsCount));
        }
    }

    public void generateChildrenWithSUS(){
        int targetChildrenSize = childrenSize;
        long sumScaledFitness = 0;
        for(int i=0; population.size()>i; i++) {
            sumScaledFitness +=
                (scalingFitnessesInChoosingParents ? population.get(i).scaledFitness : population.get(i).fitness);
        }
        double distance = ((double) sumScaledFitness) / targetChildrenSize;
        double start = UsefulUtils.generateRandomDoubleNumber(0, distance);
        double pointer = start;

        HashMap<Integer, SUSParent> susParentsMap = new HashMap<>();
        int lastIndex = 0;
        while(pointer <= populationLastRouletteEnd){
            for(int i=lastIndex; population.size()>i; i++){
                if(pointer <= population.get(i).rouletteEnd){
                    lastIndex = i;
                    if(!susParentsMap.containsKey(Integer.valueOf(lastIndex))) {
                        SUSParent susParent = new SUSParent();
                        susParent.index = lastIndex;
                        susParent.number = 1;
                        susParentsMap.put(lastIndex, susParent);
                    }
                    else susParentsMap.get(Integer.valueOf(lastIndex)).number++;
                    break;
                }
            }
            pointer += distance;
        }

        List<SUSParent> susParents = new ArrayList();
        for(int i=0; population.size()>i; i++){
            if(susParentsMap.containsKey(Integer.valueOf(i)))
                susParents.add(susParentsMap.get(Integer.valueOf(i)));
        }
        susParentsMap = null;

        Random random = new Random();
        children = new ArrayList<>();
        List<SUSParent> deletedSusParents = new ArrayList();
        while(susParents.size() > 0){
            int firstRandomIndex;
            int secondRandomIndex;
            boolean secondIsDeleted = false;

            if(susParents.size() == 1 && deletedSusParents.size() > 0){
                firstRandomIndex = 0;
                secondIsDeleted = true;
                secondRandomIndex = random.nextInt(deletedSusParents.size());
            }
            else if(susParents.size() == 1){
                firstRandomIndex = 0;
                secondRandomIndex = 0;
            }
            else {
                for(int i=1; susParents.size()>i; i++){
                    if(susParents.get(i).number > susParents.get(0).number){
                        SUSParent tempSUSParent = susParents.get(0);
                        susParents.set(0, susParents.get(i));
                        susParents.set(i, tempSUSParent);
                    }
                }

                firstRandomIndex = 0;
                secondRandomIndex = random.nextInt(susParents.size() - 1) + 1;
            }

            Individual firstParent = population.get(susParents.get(firstRandomIndex).index);
            Individual secondParent = secondIsDeleted ?
                    population.get(deletedSusParents.get(secondRandomIndex).index) :
                    population.get(susParents.get(secondRandomIndex).index);
            List<Individual> crossChilds = firstParent.crossover(secondParent, crossoverPointsCount);
            children.addAll(crossChilds);
            crossChilds = null;

            susParents.get(firstRandomIndex).number--;
            if(!secondIsDeleted) susParents.get(secondRandomIndex).number--;
            for(int i=0; susParents.size()>i; i++){
                if(susParents.get(i).number < 1){
                    deletedSusParents.add(susParents.remove(i));
                    i--;
                }
            }
        }
    }

    public void preparingCompetitors(){
        competitors = new ArrayList<>();
        competitors.addAll(population);
        competitors.addAll(children);
        int numberOfWastedChilds = 0;

        for(int i=0; competitors.size()>i; i++){
            if(competitors.get(i).fitness == 0) calcFitness(competitors.get(i));
            competitors.get(i).scaledFitness = competitors.get(i).fitness - basePopulationFitness;
            if(competitors.get(i).scaledFitness < 0) competitors.get(i).scaledFitness = 0;

            if(optimumIndividual == null || competitors.get(i).fitness > optimumIndividual.fitness) {
                optimumIndividual = competitors.get(i);
                printLocalOptimumIndividual();
            }
            if(competitors.get(i).fitness == 0) numberOfWastedChilds++;
        }
        competitorsLastRouletteEnd = -1;

        IndividualsComparator individualsComparator =
                new IndividualsComparator(IndividualsComparator.SORT_BY_SCALED_FITNESS);
        individualsComparator.setOrderMode(IndividualsComparator.ORDER_DESCENDING);
        Collections.sort(competitors, individualsComparator);

        for(int i=0; competitors.size()>i; i++){
            competitors.get(i).rouletteStart = competitorsLastRouletteEnd + 1;
            competitors.get(i).rouletteEnd =
                    competitors.get(i).rouletteStart
                    + (scalingFitnessesInSurvivorsSelection ? competitors.get(i).scaledFitness : competitors.get(i).fitness)
                    - 1;

            if(competitors.get(i).rouletteStart > competitors.get(i).rouletteEnd){
                competitors.get(i).rouletteStart = -1;
                competitors.get(i).rouletteEnd = -1;
            }
            else competitorsLastRouletteEnd = competitors.get(i).rouletteEnd;
        }
    }

    public void chooseSurvivors(){
        List<Individual> survivors = new ArrayList<>();

        long sumScaledFitness = 0;
        for(int i=0; competitors.size()>i; i++){
            sumScaledFitness +=
                (scalingFitnessesInSurvivorsSelection ? competitors.get(i).scaledFitness : competitors.get(i).fitness);
        }

        double distance = ((double) sumScaledFitness) / populationSize;
        double start = UsefulUtils.generateRandomDoubleNumber(0, distance);
        double pointer = start;

        int lastIndex = 0;
        while(pointer <= competitorsLastRouletteEnd){
            for(int i=lastIndex; competitors.size()>i; i++){
                lastIndex = i;
                Individual individual = competitors.get(i);

                if(pointer <= individual.rouletteEnd){
                    survivors.add(individual);
                    break;
                }
            }
            pointer += distance;
        }

        population = survivors;
        children = null;
        competitors = null;
        generationNumber++;
        basePopulationFitness = -1;
    }

    public void printLocalOptimumIndividual(){
        System.out.println(
            "GN: " + generationNumber +
            " | Optimum Individual Fitness: " + optimumIndividual.fitness
        );
    }

    public void printFinalOptimumIndividual(){
        System.out.println();
        System.out.println("Final Optimum Individual: ");
        System.out.println(optimumIndividual.toString());
        System.out.println("Fitness: " + optimumIndividual.fitness);
        System.out.println();
    }

    public void solve() throws Exception {
        generatePopulation();
        while(maxGenerationNumber > generationNumber){
            calculatePopulationFitness();
            generateChildrenWithRouletteWheel();
            preparingCompetitors();
            chooseSurvivors();
            if((generationNumber % 200) == 0)
                System.out.println("Generation " + generationNumber + " Done!");
        }

        sortPopulation(IndividualsComparator.SORT_BY_FITNESS);
        printPopulation();
        printFinalOptimumIndividual();
    }

    public void sortPopulation(int sortMode){
        IndividualsComparator individualsComarator = new IndividualsComparator(sortMode);
        Collections.sort(population, individualsComarator);
    }

    public void printPopulation(){
        System.out.println("\nPopulation In Generate " + generationNumber + ":");
        for(int i=0; population.size()>i; i++)
            System.out.println("Individual " + i + ": " + population.get(i).toString());
        System.out.println();
    }

    class SUSParent {
        int index;
        int number;
    }
}
