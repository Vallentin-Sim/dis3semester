package lektion7;

import java.util.*;
class DijkstraExample {
    static class Edge {
        String target;
        int weight;
        Edge(String target, int weight) {
            this.target = target;
            this.weight = weight;
        }
    }
    public static Map<String, Integer> dijkstra(Map<String, List<Edge>> graph, String start) {
        // Korteste afstande (default = ∞)
        Map<String, Integer> dist = new HashMap<>();
        for (String node : graph.keySet()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(start, 0);
        // PriorityQueue til at vælge node med korteste afstand
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());
        pq.add(new AbstractMap.SimpleEntry<>(start, 0));
        while (!pq.isEmpty()) {
            String current = pq.poll().getKey();
            for (Edge edge : graph.get(current)) {
                int newDist = dist.get(current) + edge.weight;
                if (newDist < dist.get(edge.target)) {
                    dist.put(edge.target, newDist);
                    pq.add(new AbstractMap.SimpleEntry<>(edge.target, newDist));
                }
            }
        }
        return dist;
    }
    public static void main(String[] args) {
        // Definer grafen
        Map<String, List<Edge>> graph = new HashMap<>();
        graph.put("A", Arrays.asList(new Edge("B", 6), new Edge("D", 1)));
        graph.put("D", Arrays.asList(new Edge("E", 1), new Edge("B", 2)));
        graph.put("E", Arrays.asList(new Edge("B", 2), new Edge("C", 5)));
        graph.put("B", Arrays.asList(new Edge("E", 2), new Edge("C", 5), new Edge("D", 2)));
        graph.put("C", Arrays.asList(new Edge("E", 5)));
//Todo udfyld værdierne for de andre knuder.
        // Kør Dijkstra fra A
        Map<String, Integer> result = dijkstra(graph, "A");


// Print resultaterne
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            System.out.println("Korteste afstand fra A til " + entry.getKey() + " = " +
                    entry.getValue());
        }
    }
}