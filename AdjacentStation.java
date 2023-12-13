/**
 * Adjacent Station which holds an ID and a weight(time) relative to a source station
 */

public class AdjacentStation {
    private int destination;
    private int weight;

    public AdjacentStation(int destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }

    public int getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }
}
