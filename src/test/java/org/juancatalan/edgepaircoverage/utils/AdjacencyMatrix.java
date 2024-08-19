package org.juancatalan.edgepaircoverage.utils;

import org.jgrapht.graph.DirectedPseudograph;
import org.juancatalan.edgepaircoverage.graphs.BooleanEdge;
import org.juancatalan.edgepaircoverage.graphs.EdgeType;

import java.util.*;

public class AdjacencyMatrix implements Cloneable {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AdjacencyRow that)) return false;
            return Objects.equals(adjacencies, that.adjacencies);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(adjacencies);
        }
    }

    private Map<Integer, AdjacencyRow> adjacenciesRow = new HashMap<>();

    public static AdjacencyMatrix getAdjacencyMatrixFromGraph(DirectedPseudograph<Integer, BooleanEdge> graph){
        AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix();
        for (Integer vertex: graph.vertexSet()){
            adjacencyMatrix.addRowByIndex(vertex);
            for (Integer otherVertex: graph.vertexSet()){
                Set<BooleanEdge> edges = graph.getAllEdges(vertex, otherVertex);
                for (BooleanEdge edge: edges){
                    adjacencyMatrix.addAdjacencyToIndex(vertex, otherVertex, edge.getType());
                }
            }
        }
        return adjacencyMatrix;
    }

    private void initializeRow(Integer rowIndex){
        if (!adjacenciesRow.containsKey(rowIndex)) adjacenciesRow.put(rowIndex, new AdjacencyRow());
    }

    public void addRowByIndex(Integer from){
        initializeRow(from);
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


    public boolean isAPermutationOf(AdjacencyMatrix other){
        Set<AdjacencyMatrix> adjacencyMatrixSet = getAllPermutations();
        return adjacencyMatrixSet.contains(other);
    }

    private Set<AdjacencyMatrix> getAllPermutations(){
        Set<Integer> rowsIndex = adjacenciesRow.keySet();
        Set<AdjacencyMatrix> adjacencyMatrixSet = getAllPermutations(rowsIndex);
        adjacencyMatrixSet.add(this);
        return adjacencyMatrixSet;
    }

    private Set<AdjacencyMatrix> getAllPermutations(Set<Integer> indexToPermutate){
        if (indexToPermutate.isEmpty()){
            return Collections.emptySet();
        }
        else {
            Set<AdjacencyMatrix> matrices = new HashSet<>();
            // Selecciono el indice actual a permutar
            Integer[] indexArray = indexToPermutate.toArray(new Integer[indexToPermutate.size()]);
            Integer actualIndex = indexArray[0];
            // Separo el resto de indices
            Set<Integer> newIndexToPermutate = new HashSet<>();
            newIndexToPermutate.addAll(indexToPermutate);
            newIndexToPermutate.remove(actualIndex);
            // Permuto mi indice actual con el resto y para cada genero nuevas permutaciones
            for (Integer i: indexToPermutate){
                AdjacencyMatrix newAdjacencyMatrix = this.clone();
                if (actualIndex != i){
                    newAdjacencyMatrix.swap(actualIndex, i);
                    // Añado la nueva permutacion
                    matrices.add(newAdjacencyMatrix);
                }
                // Genero recursivamente nuevas permutaciones para cada indice
                Set<AdjacencyMatrix> newSet = newAdjacencyMatrix.getAllPermutations(newIndexToPermutate);
                matrices.addAll(newSet);
            }
            return matrices;
        }
    }

    private void swap(Integer from, Integer to){
        AdjacencyMatrix oldMatrix = this.clone();
        adjacenciesRow.clear();
        for (Map.Entry<Integer, AdjacencyRow> entry : oldMatrix.adjacenciesRow.entrySet()) {
            addRowByIndex(entry.getKey());
            for (Map.Entry<Integer, Set<EdgeType>> entry2 : entry.getValue().adjacencies.entrySet()) {
                Integer actualFrom = entry.getKey();
                Integer actualTo = entry2.getKey();
                if (actualFrom == from){
                    actualFrom = to;
                }
                else if (actualFrom == to){
                    actualFrom = from;
                }
                if (actualTo == to){
                    actualTo = from;
                }
                else if (actualTo == from){
                    actualTo = to;
                }
                for (EdgeType edge: entry2.getValue()){
                    addAdjacencyToIndex(actualFrom, actualTo, edge);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdjacencyMatrix that)) return false;
        return Objects.equals(adjacenciesRow, that.adjacenciesRow);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(adjacenciesRow);
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

    @Override
    public AdjacencyMatrix clone() {
        AdjacencyMatrix newMatrix = new AdjacencyMatrix();
        for (Map.Entry<Integer, AdjacencyRow> entry : adjacenciesRow.entrySet()) {
            newMatrix.addRowByIndex(entry.getKey());
            for (Map.Entry<Integer, Set<EdgeType>> entry2 : entry.getValue().adjacencies.entrySet()) {
                for (EdgeType edge: entry2.getValue()){
                    newMatrix.addAdjacencyToIndex(entry.getKey(), entry2.getKey(), edge);
                }
            }
        }
        return newMatrix;
    }

}
