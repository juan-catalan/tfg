package org.example;

import org.jgrapht.graph.DirectedPseudograph;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.ASM4;

class AddPrintConditionsTransformerTest {
    void verificarNumVerticesAristas(Class clase, String nombreMetodo, int numVertices, int numAristas){
        String idMetodo = clase.getName().concat(".").concat(nombreMetodo);

        ClassNode cn = new ClassNode(ASM4);
        ClassReader cr;
        try {
            cr = new ClassReader(clase.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cr.accept(cn, 0);
        Optional<MethodNode> metodo = cn.methods.stream().filter(m -> m.name.equals(nombreMetodo)).findFirst();

        assertFalse(metodo.isEmpty());


        InsnList listaInstrucciones = metodo.get().instructions;
        AddPrintConditionsTransformer transformer = new AddPrintConditionsTransformer(false);
        transformer.addNodoToIntger(idMetodo);
        DirectedPseudograph<AbstractInsnNode, BooleanEdge> controlFlowGraph = transformer.getControlFlowGraph(listaInstrucciones, idMetodo);
        DirectedPseudograph<Integer, BooleanEdge> controlFlowGraphInt = transformer.transformGraphToInteger(idMetodo, controlFlowGraph);
        System.out.println(controlFlowGraphInt);

        assertEquals(numVertices, controlFlowGraphInt.vertexSet().size());
        assertEquals(numAristas, controlFlowGraphInt.edgeSet().size());

        Integer vertex = controlFlowGraphInt.vertexSet().toArray(new Integer[controlFlowGraphInt.vertexSet().size()])[0];

        AdjacencyMatrix adjacencyMatrix = getAdjacencyMatrix(controlFlowGraphInt);
        AdjacencyMatrix adjacencyMatrixCopy = adjacencyMatrix.clone();
        System.out.println("----Matriz adyacencia-----");
        System.out.println(adjacencyMatrix);

        assertTrue(adjacencyMatrix.isAPermutationOf(adjacencyMatrixCopy), "Los grafos no coinciden");

        //Set<AdjacencyMatrix> permutaciones = adjacencyMatrixCopy.getAllPermutations();
        //System.out.println(permutaciones.size());
        //System.out.println(permutaciones);
        // assertTrue(adjacencyMatrix.isPermutation());
    }

    AdjacencyMatrix getAdjacencyMatrix(DirectedPseudograph<Integer, BooleanEdge> graph){
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

    @Test
    void testPruebaFor() {
        verificarNumVerticesAristas(org.example.Main.class, "pruebaFor", 3, 3);
    }

    //@Disabled
    @Test
    void testCurso1617Convocatoria1() {
        verificarNumVerticesAristas(org.example.ejerciciosExamen.curso1617.Convocatoria1.class, "buscar", 6, 7);
    }

    //@Disabled
    @Test
    void testCurso1415Convocatoria2() {
        verificarNumVerticesAristas(org.example.ejerciciosExamen.curso1415.Convocatoria2.class, "imagen", 7, 9);
    }
}