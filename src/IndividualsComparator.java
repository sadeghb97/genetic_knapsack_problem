import java.util.Comparator;

public class IndividualsComparator implements Comparator<Individual> {
    public static final int SORT_BY_FITNESS = 1;
    public static final int SORT_BY_SCALED_FITNESS = 2;
    private int sortMode = SORT_BY_FITNESS;

    public static final int ORDER_ASCENDING = 1;
    public static final int ORDER_DESCENDING = 2;
    private int orderMode = ORDER_ASCENDING;

    IndividualsComparator(int sortMode){
        this.sortMode = sortMode;
    }

    public void setOrderMode(int orderMode) {
        this.orderMode = orderMode;
    }

    @Override
    public int compare(Individual x, Individual y) {
        if(sortMode == SORT_BY_FITNESS && orderMode == ORDER_ASCENDING)
            return Integer.compare(x.fitness, y.fitness);
        if(sortMode == SORT_BY_FITNESS && orderMode == ORDER_DESCENDING)
            return Integer.compare(y.fitness, x.fitness);
        if(sortMode == SORT_BY_SCALED_FITNESS && orderMode == ORDER_ASCENDING)
            return Integer.compare(x.scaledFitness, y.scaledFitness);
        return Integer.compare(y.scaledFitness, x.scaledFitness);
    }
}
