public class KnapSackItem {
    int id;
    int weight;
    int value;

    KnapSackItem(int id, int weight, int value){
        this.id = id;
        this.weight = weight;
        this.value = value;
    }

    @Override
    public String toString() {
        return "{id: " + id + ", weight: " + weight + ", value: " + value + "}";
    }
}
