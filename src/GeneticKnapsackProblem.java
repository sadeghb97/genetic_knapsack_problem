import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GeneticKnapsackProblem {
    private int knapsackMaxWeight;
    private int populationSize = 100;
    Map<Integer, KnapSackItem> knapsackItems;
    List<Individual> population;
    List<Individual> children;
    private long populationLastRouletteEnd;
    private long competitorsLastRouletteEnd;
    private int crossoverPointsCount = 1;
    private int maxGenerationNumber;
    private int generationNumber;
    private int childrenSize;
    String weightsFilePath = "weights.txt";
    String valuesFilePath = "values.txt";
    Individual optimumIndividual = null;
    int basePopulationFitness;
    boolean scalingFitnesses = false;

    GeneticKnapsackProblem(int knapsackMaxWeight, int maxGenerationNumber){
        this.knapsackMaxWeight = knapsackMaxWeight;
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

    public String getWeightsFilePath() {
        return weightsFilePath;
    }

    public void setWeightsFilePath(String weightsFilePath) {
        this.weightsFilePath = weightsFilePath;
    }

    public String getValuesFilePath() {
        return valuesFilePath;
    }

    public void setValuesFilePath(String valuesFilePath) {
        this.valuesFilePath = valuesFilePath;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
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

    public void loadKnapsackItems() throws IOException {
        loadKnapsackItems(weightsFilePath, valuesFilePath);
    }

    public void printKnapsackItems(){
        System.out.println("\nKnapsack Items: ");
        for(int i=1; knapsackItems.size()>=i; i++)
            System.out.println("Item " + i + ": " + knapsackItems.get(i).toString());
        System.out.println();
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
                for (int j = 0; knapsackItems.size() > j; j++) {
                    representation.add(random.nextBoolean());
                }
                correct = getFitness(representation) > 0;
            }
            Individual individual = new Individual(representation);
            population.add(individual);
        }
    }

    public void calculatePopulationFitness(){
        basePopulationFitness = -1;
        for(int i=0; population.size()>i; i++){
            calcFitness(population.get(i));

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
                    + (scalingFitnesses ? population.get(i).scaledFitness : population.get(i).fitness)
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
                fitness += knapsackItems.get(Integer.valueOf(i + 1)).value;
                sumWeight += knapsackItems.get(Integer.valueOf(i + 1)).weight;
                if (sumWeight > knapsackMaxWeight) break;
            }
        }
        return sumWeight > knapsackMaxWeight ? 0 : fitness;
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
                (scalingFitnesses ? population.get(i).scaledFitness : population.get(i).fitness);
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
        int numberOfWastedChilds = 0;

        for(int i=0; children.size()>i; i++){
            calcFitness(children.get(i));
            children.get(i).scaledFitness = children.get(i).fitness - basePopulationFitness;
            if(children.get(i).scaledFitness < 0) children.get(i).scaledFitness = 0;

            if(optimumIndividual == null || children.get(i).fitness > optimumIndividual.fitness) {
                optimumIndividual = children.get(i);
                printLocalOptimumIndividual();
            }
            if(children.get(i).fitness == 0) numberOfWastedChilds++;
        }
        competitorsLastRouletteEnd = populationLastRouletteEnd;

        for(int i=0; children.size()>i; i++){
            children.get(i).rouletteStart = competitorsLastRouletteEnd + 1;
            children.get(i).rouletteEnd =
                children.get(i).rouletteStart
                    + (scalingFitnesses ? children.get(i).scaledFitness : children.get(i).fitness)
                    - 1;

            if(children.get(i).rouletteStart > children.get(i).rouletteEnd){
                children.get(i).rouletteStart = -1;
                children.get(i).rouletteEnd = -1;
            }
            else competitorsLastRouletteEnd = children.get(i).rouletteEnd;
        }

        /*System.out.println(
            "Generation " + generationNumber + " Wasted Childs: " + numberOfWastedChilds);*/
    }

    public void chooseSurvivors(){
        List<Individual> survivors = new ArrayList<>();
        long sumScaledFitness = 0;
        for(int i=0; population.size()>i; i++){
            sumScaledFitness +=
                (scalingFitnesses ? population.get(i).scaledFitness : population.get(i).fitness);
        }
        for(int i=0; children.size()>i; i++){
            sumScaledFitness +=
                (scalingFitnesses ? children.get(i).scaledFitness : children.get(i).fitness);
        }
        double distance = ((double) sumScaledFitness) / populationSize;
        double start = UsefulUtils.generateRandomDoubleNumber(0, distance);
        double pointer = start;

        int lastIndex = 0;
        while(pointer <= competitorsLastRouletteEnd){
            for(int i=lastIndex; (population.size() + children.size())>i; i++){
                lastIndex = i;
                Individual individual = i < population.size() ?
                        population.get(i) : children.get(i - population.size());

                if(pointer <= individual.rouletteEnd){
                    survivors.add(individual);
                    break;
                }
            }
            pointer += distance;
        }

        population = survivors;
        children = null;
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
        printFinalOptimumIndividual();
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
