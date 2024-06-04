package org.example;

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
    }

    private Map<Integer, AdjacencyRow> adjacenciesRow = new HashMap<>();

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

    public boolean isPermutation(AdjacencyMatrix other){
        for(AdjacencyMatrix permutationAdjacencyMatrix: getAllPermutations()){
            if (permutationAdjacencyMatrix.equals(other)) return true;
        }
        return true;
    }

    public Set<AdjacencyMatrix> getAllPermutations(){
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
                // TODO: DESCOMENTAR CUANDO SWAP FUNCIONE
                Set<AdjacencyMatrix> newSet = newAdjacencyMatrix.getAllPermutations(newIndexToPermutate);
                matrices.addAll(newSet);
            }
            return matrices;
        }
    }

    private void swap(Integer from, Integer to){
        // Guardo y elimino la información actual
        AdjacencyRow adjacencyRowFrom = adjacenciesRow.get(from);
        adjacenciesRow.remove(from);
        AdjacencyRow adjacencyRowTo = adjacenciesRow.get(to);
        adjacenciesRow.remove(from);
        // La añado swapeandola
        adjacenciesRow.put(from, adjacencyRowTo);
        adjacenciesRow.put(to, adjacencyRowFrom);
        for (Map.Entry<Integer, AdjacencyRow> entry : adjacenciesRow.entrySet()) {
            // Si contienen referenencia al indice from
            if (entry.getValue().adjacencies.containsKey(from)){

            }
            // Si contienen referenencia al indice to
            if (entry.getValue().adjacencies.containsKey(to)){

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
