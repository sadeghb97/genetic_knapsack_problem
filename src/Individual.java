import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Individual {
    List representation;
    int fitness = 0;
    int scaledFitness = 0;
    long rouletteStart = 0;
    long rouletteEnd = 0;

    Individual(List representation){
        this.representation = representation;
    }

    public List<Individual> crossover(Individual partner, int points){
        List out = new ArrayList<Individual>();

        Individual firstChild = new Individual(new ArrayList());
        Individual secondChild = new Individual(new ArrayList());
        int sliceLength = representation.size() / (points+1);
        if((representation.size() % (points + 1)) != 0) sliceLength++;

        for(int i=0; representation.size()>i; i++){
            boolean firstInheritanceFromThis = ((i / sliceLength) % 2) == 0;
            firstChild.representation.add(
                    firstInheritanceFromThis ? representation.get(i) : partner.representation.get(i));
            secondChild.representation.add(
                    firstInheritanceFromThis ? partner.representation.get(i) : representation.get(i));
        }

        Random random = new Random();
        if(random.nextDouble() < 0.4) firstChild.mutation();
        if(random.nextDouble() < 0.4) secondChild.mutation();

        out.add(firstChild);
        out.add(secondChild);
        return out;
    }

    public void mutation(){
        Random random = new Random();
        int mutationIndex = random.nextInt(representation.size());
        representation.set(mutationIndex, !((boolean) representation.get(mutationIndex)));

    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");

        for(int i=0; representation.size()>i; i++){
            if(i != 0) stringBuilder.append(", ");
            stringBuilder.append(String.valueOf(representation.get(i)));
        }
        stringBuilder.append("]");
        stringBuilder.append(" - " + fitness + " - " + scaledFitness);
        return stringBuilder.toString();
    }
}
