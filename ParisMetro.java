import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ParisMetro {
    private Graph trainMap;

    public ParisMetro(String fileName) throws Exception, IOException {
        trainMap = new Graph();
        readMetro(fileName);
    }

    protected void readMetro(String fileName) throws FileNotFoundException {

        try (BufferedReader graphFile = new BufferedReader(new FileReader(fileName))) {
            // Read the first line to get the number of vertices and edges
            String line = graphFile.readLine();
            String[] numbers = line.split(" ");

            int numberOfVertices = Integer.parseInt(numbers[0]);
            int numberOfEdges = Integer.parseInt(numbers[1]);

            // Adding stations (vertices) to the graph
            for (int i = 0; i < numberOfVertices; i++) {
                String[] stationInfo = graphFile.readLine().split(" ");
                trainMap.addStation(Integer.parseInt(stationInfo[0]));
            }

            graphFile.readLine(); // Skip the '$' sign indicating end of vertex list

            // Adding edges between stations
            for (int i = 0; i < numberOfEdges; i++) { // format is 'v1 v2 w'
                String[] stationInfo = graphFile.readLine().split(" ");
                trainMap.addNeighbor(Integer.parseInt(stationInfo[0]), Integer.parseInt(stationInfo[1]),
                        Integer.parseInt(stationInfo[2]));
            }
        } catch (IOException e) {
            System.out.print("Caught IOException");
        }
    }

    // Prints a visual representation of the graph
    public void printMap() {
        System.out.println(trainMap.printMapV2());
    }

    /**************************************************************************
     * 
     * Question 1: printing all vertices belonging to the same line -- Uses DFS
     * 
     **************************************************************************/
    HashMap<Integer, Boolean> visited;

    public List<Integer> stationsBelonging(int station) {
        List<Integer> line = new ArrayList<>();
        visited = new HashMap<>();
        stationsBelonging(trainMap, station, line);
        return line;
    }

    public void stationsBelonging(Graph graph, int station, List<Integer> line) {
        if (visited.get(station) != null) {
            return;
        }

        visited.put(station, true);
        line.add(station);

        List<AdjacentStation> neighbors = trainMap.getNeighbors(station);
        for (AdjacentStation a : neighbors) {
            if (a.getWeight() != -1) {
                int s = a.getDestination(); // get station number for opposite station
                stationsBelonging(trainMap, s, line);
            }
        }
        return;
    }

    /**************************************************************************
     * 
     * Quesiton 2: finding the shortest path
     * Implements Dijkstra's Algorithm
     * 
     ***********************************************************************/

    public List<Integer> shortestPath(int start, int end) {
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> cloud = new HashMap<>();
        PriorityQueue<AdjacentStation> PQ = new PriorityQueue<>(new MyComparator());

        // Initialize distances from start station
        for (int i = 0; i < trainMap.numberOfStations(); i++) {
            int initialDistance = (i == start) ? 0 : Integer.MAX_VALUE;
            distances.put(i, initialDistance);
            PQ.offer(new AdjacentStation(i, initialDistance));
        }

        // Process each station, updating distances to neighbors
        while (!PQ.isEmpty()) {
            AdjacentStation station = PQ.poll();
            int stationNumber = station.getDestination();
            int stationDistance = distances.get(stationNumber);

            // Process neighbors
            List<AdjacentStation> neighbors = trainMap.getNeighbors(stationNumber);
            for (AdjacentStation a : neighbors) {
                int a_weight = (a.getWeight() == -1) ? 90 : a.getWeight(); // 90 seconds for walking edges
                int distance = a_weight + stationDistance;

                if (distance < distances.get(a.getDestination())) {
                    distances.put(a.getDestination(), distance);
                    cloud.put(a.getDestination(), stationNumber);
                    PQ.offer(new AdjacentStation(a.getDestination(), distance));
                }
            }
        }
        System.out.println("Time = " + distances.get(end));
        return buildPath(start, end, cloud);
    }

    // Reconstructs the shortest path from end to start station
    private List<Integer> buildPath(int start, int end, Map<Integer, Integer> cloud) {
        List<Integer> path = new LinkedList<>();
        Integer currentStation = end;
        while (currentStation != null) {
            path.add(0, currentStation);
            currentStation = cloud.get(currentStation);
        }
        return path;
    }

    // Comparator for prioritizing stations by their distance from the start station
    public class MyComparator implements Comparator<AdjacentStation> {

        public int compare(AdjacentStation p1, AdjacentStation p2) {
            Integer key1 = p1.getWeight();
            Integer key2 = p2.getWeight();
            return key1.compareTo(key2);
        }
    }

    /**************************************************************************
     * 
     * Quesiton 3: finding the shortest path considering malfunctioning line
     * 
     ***********************************************************************/
    public List<Integer> shortestPathMalfunction(int start, int end, int block) {

        List<Integer> malfunctioningLine = stationsBelonging(block);

        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> cloud = new HashMap<>();
        PriorityQueue<AdjacentStation> PQ = new PriorityQueue<>(new MyComparator());

        // Initialize distances
        for (int i = 0; i < trainMap.numberOfStations(); i++) {
            int initialDistance = (i == start) ? 0 : Integer.MAX_VALUE;
            distances.put(i, initialDistance);
            PQ.offer(new AdjacentStation(i, initialDistance));
        }

        while (!PQ.isEmpty()) {
            AdjacentStation station = PQ.poll();
            int stationNumber = station.getDestination();
            int stationDistance = distances.get(stationNumber);

            if (malfunctioningLine.contains(stationNumber)) {
                continue;
            }
            // Process neighbors
            List<AdjacentStation> neighbors = trainMap.getNeighbors(stationNumber);
            for (AdjacentStation a : neighbors) {
                if (malfunctioningLine.contains(a.getDestination())) {
                    continue;
                }

                int a_weight = (a.getWeight() == -1) ? 90 : a.getWeight();
                int distance = a_weight + stationDistance;

                if (distance < distances.get(a.getDestination())) {
                    distances.put(a.getDestination(), distance);
                    cloud.put(a.getDestination(), stationNumber);
                    PQ.offer(new AdjacentStation(a.getDestination(), distance));
                }

            }
        }
        System.out.println("Time = " + distances.get(end));
        return buildPath(start, end, cloud);
    }

    public static void main(String[] args) throws IOException, Exception {
        ParisMetro metro = new ParisMetro("metro.txt");
        //metro.printMap();

        if (args.length == 1) {
            String N1 = args[0];
            System.out.println("Input:\nN1 = " + N1 + "\nOutput: ");
            List<Integer> line = metro.stationsBelonging(Integer.parseInt(args[0]));
            System.out.print("Line: ");
            for (Integer i : line) {
                System.out.print(i + " ");
            }
            System.out.println();
        }

        else if (args.length == 2) {
            // shortest path
            String N1 = args[0];
            String N2 = args[1];
            System.out.println("Input:\nN1 = " + N1 + " N2 = " + N2 + "\nOutput: ");
            List<Integer> line = metro.shortestPath(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            System.out.print("Path: ");
            for (Integer i : line) {
                System.out.print(i + " ");
            }
            System.out.println();

        }

        else if (args.length == 3) {
            String N1 = args[0];
            String N2 = args[1];
            System.out.println("Input:\nN1 = " + N1 + " N2 = " + N2 + "\nOutput: ");
            List<Integer> line = metro.shortestPath(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            System.out.print("Path: ");
            for (Integer i : line) {
                System.out.print(i + " ");
            }
            System.out.println();

            // shortest path with road blocks
            String N3 = args[2];
            System.out.println("Input:\nN1 = " + N1 + " N2 = " + N2 + " N3 = " + N3 + "\nOutput: ");
            line = metro.shortestPathMalfunction(Integer.parseInt(N1), Integer.parseInt(N2),
                    Integer.parseInt(N3));
            System.out.print("Path: ");
            for (Integer i : line) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }
}
