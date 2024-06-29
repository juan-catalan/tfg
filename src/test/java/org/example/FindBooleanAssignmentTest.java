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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.objectweb.asm.Opcodes.ASM4;

class FindBooleanAssignmentTest {
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
        AddPrintConditionsTransformer transformer = AddPrintConditionsTransformer.getInstance();
        transformer.addNodoToIntger(idMetodo);
        DirectedPseudograph<AbstractInsnNode, BooleanEdge> controlFlowGraph = transformer.getControlFlowGraph(listaInstrucciones, idMetodo);
        DirectedPseudograph<Integer, BooleanEdge> controlFlowGraphInt = transformer.transformGraphToInteger(idMetodo, controlFlowGraph);
        System.out.println("grafo");
        System.out.println(controlFlowGraphInt);

        assertEquals(numVertices, controlFlowGraphInt.vertexSet().size());
        assertEquals(numAristas, controlFlowGraphInt.edgeSet().size());
    }

    @Coverage2Edge
    private void pruebaAsignacionBooleanaSimple(int i){
        boolean multiplo2 = i%2 == 0;
        if (multiplo2){
            System.out.println("Multiplo de 2");
        }
        else {
            System.out.println("No multiplo de 2");
        }
    }

    @Coverage2Edge
    private void pruebaAsignacionBooleanaConAnd(int i){
        boolean multiplo6 = i%2 == 0 && i%3 == 0;
        if (multiplo6){
            System.out.println("Multiplo de 6");
        }
        else {
            System.out.println("No multiplo de 6");
        }
    }

    @Coverage2Edge
    private void pruebaAsignacionBooleanaConOr(int i){
        boolean multiplo2o3 = i%2 == 0 || i%3 == 0;
        if (multiplo2o3){
            System.out.println("Multiplo de 2 o 3");
        }
        else {
            System.out.println("No multiplo de 2 o 3");
        }
    }

    @Coverage2Edge
    private void pruebaAsignacionBooleanaConTripleOr(int i){
        boolean multiplo2o3 = i%2 == 0 || i%3 == 0 || i%5 == 0;
        if (multiplo2o3){
            System.out.println("Multiplo de 2 o 3");
        }
        else {
            System.out.println("No multiplo de 2 o 3");
        }
    }

    @Coverage2Edge
    private void pruebaAsignacionBooleanaCombinandoOrAnd(int i){
        boolean multiplo2o3 = i%2 == 0 && i%3 == 0 || i%5 == 0;
        if (multiplo2o3){
            System.out.println("Multiplo de 2 o 3");
        }
        else {
            System.out.println("No multiplo de 2 o 3");
        }
    }

    @Test
    void testFindBooleanAssignmentTest() throws NoSuchMethodException {
        verificarNumVerticesAristas(org.example.ejerciciosExamen.curso1415.Convocatoria2.class, "imagen", 6, 7);
    }

    @Test
    void testAsignacionBooleanaSimple() throws NoSuchMethodException {
        verificarNumVerticesAristas(this.getClass(), "pruebaAsignacionBooleanaSimple", 3, 3);
    }

    @Test
    void testAsignacionBooleanaConAnd() throws NoSuchMethodException {
        verificarNumVerticesAristas(this.getClass(), "pruebaAsignacionBooleanaConAnd", 3, 3);
    }

    @Test
    void testAsignacionBooleanaConOr() throws NoSuchMethodException {
        verificarNumVerticesAristas(this.getClass(), "pruebaAsignacionBooleanaConOr", 3, 3);
    }

    @Test
    void testAsignacionBooleanaConTripleOr() throws NoSuchMethodException {
        verificarNumVerticesAristas(this.getClass(), "pruebaAsignacionBooleanaConTripleOr", 3, 3);
    }

    @Disabled("Aun no funciona para combinacion de puertas logicas")
    @Test
    void testAsignacionBooleanaCombinada() throws NoSuchMethodException {
        verificarNumVerticesAristas(this.getClass(), "pruebaAsignacionBooleanaCombinandoOrAnd", 3, 3);
    }
}