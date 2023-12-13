import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
    // Map representing the adjacency list of the graph. 
    // Key: Station number. Value: List of adjacent stations with their respective weights.
    private Map<Integer, LinkedList<AdjacentStation>> adjacencyList;

    // Constructs an empty graph
    public Graph() {
        adjacencyList = new HashMap<>();
    }

    // Adds a new station (vertex) to the graph. Initializes its adjacency list if not already present.
    public void addStation(int station) {
        if (!adjacencyList.containsKey(station)) {
            adjacencyList.put(station, new LinkedList<>());
        }
    }

    // Returns the number of stations (vertices) in the graph.
    public int numberOfStations() {
        return adjacencyList.size();
    }

    // Adds an edge between two stations. Also specifies the weight of the edge.
    public void addNeighbor(int station1, int station2, int weight) {
        // Create an adjacent station object representing the edge and its weight.
        AdjacentStation nextStation = new AdjacentStation(station2, weight);

        // Add this adjacent station to the list of neighbors of the source station.
        adjacencyList.get(station1).add(nextStation);

    }

    // Retrieves a list of all adjacent neighbors (and their weights) of a given station.
    public List<AdjacentStation> getNeighbors(int station) {
        return adjacencyList.getOrDefault(station, new LinkedList<>());
    }

    // Returns a set of all station numbers present in the graph.
    public Set<Integer> getStations() {
        return adjacencyList.keySet();
    }

    // Prints a textual representation of the graph (Version 1).
    public String printMap() {
        String output = "";
        for (Integer i : getStations()) {
            output += i.toString() + ": ";
            LinkedList<AdjacentStation> L = adjacencyList.get(i);
            for (AdjacentStation station : L) { // .lengthof LL inside the i
                output += "(" + i + ", " + station.getDestination() + "), ";
            }
            output += "\n";
        }
        return output;
    }

    // Prints a textual representation of the graph (Version 2) - more compact format.
    public String printMapV2() {
        String output = "";
        for (Integer i : getStations()) {
            output += i.toString() + ": [";
            LinkedList<AdjacentStation> L = adjacencyList.get(i);
            for (AdjacentStation station : L) {
                output += station.getDestination() + ", ";
            }
            output += "]\n";
        }
        return output;
    }
}
