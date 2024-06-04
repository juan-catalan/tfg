package org.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdjacencyMatrix {
    private class AdjacencyRow {
        public Map<Integer, Set<EdgeType>> adjacencies = new HashMap<>();
        public AdjacencyRow(){
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry entry : adjacencies.entrySet())
            {
                stringBuilder.append("\t" + entry.getKey() + ": " + entry.getValue() + "\n");
            }
            return stringBuilder.toString();
        }
    }

    private Map<Integer, AdjacencyRow> adjacenciesRow = new HashMap<>();

    private void initializeRow(Integer rowIndex){
        if (!adjacenciesRow.containsKey(rowIndex)) adjacenciesRow.put(rowIndex, new AdjacencyRow());
    }

    public void addAdjacencyToIndex(Integer from, Integer to, EdgeType edgeType){
        initializeRow(from);
        AdjacencyRow adjacencyRow = adjacenciesRow.get(from);
        if (!adjacencyRow.adjacencies.containsKey(to)){
            adjacencyRow.adjacencies.put(to, new HashSet<>());
        }
        Set<EdgeType> setEdges = adjacencyRow.adjacencies.get(to);
        setEdges.add(edgeType);
        adjacencyRow.adjacencies.put(to, setEdges);
    }

    public boolean isPermutation(AdjacencyMatrix other){
        return true;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry entry : adjacenciesRow.entrySet())
        {
            stringBuilder.append(entry.getKey() + ":\n" + entry.getValue());
        }
        return stringBuilder.toString();
    }
}
