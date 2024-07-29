package org.juancatalan.edgepaircoverage;

import org.jgrapht.graph.DirectedPseudograph;
import org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1415.Convocatoria2;
import org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1819.Convocatoria1;
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

class GetControlFlowGraphTest {
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
        transformer.addNodoLinenumber(idMetodo);
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

    void verificarGrafo(Class clase, String nombreMetodo, DirectedPseudograph<Integer, BooleanEdge> grafoEsperado){
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

        assertFalse(metodo.isEmpty(), "El método no existe en esa clase");

        InsnList listaInstrucciones = metodo.get().instructions;
        AddPrintConditionsTransformer transformer = AddPrintConditionsTransformer.getInstance();
        transformer.addNodoToIntger(idMetodo);
        transformer.addNodoLinenumber(idMetodo);
        DirectedPseudograph<AbstractInsnNode, BooleanEdge> controlFlowGraph = transformer.getControlFlowGraph(listaInstrucciones, idMetodo);
        DirectedPseudograph<Integer, BooleanEdge> controlFlowGraphInt = AddPrintConditionsTransformer.transformGraphToInteger(idMetodo, controlFlowGraph);

        System.out.println("Grafo esperado");
        System.out.println(grafoEsperado);
        System.out.println("Grafo obtenido");
        System.out.println(controlFlowGraphInt);


        assertEquals(grafoEsperado.vertexSet().size(), controlFlowGraphInt.vertexSet().size(), "El grafo obtenido no tiene el número de vertices esperado");
        assertEquals(grafoEsperado.edgeSet().size(), controlFlowGraphInt.edgeSet().size(), "El grafo obtenido no tiene el número de aristas esperado");

        AdjacencyMatrix adjacencyMatrix = getAdjacencyMatrix(controlFlowGraphInt);
        AdjacencyMatrix adjacencyMatrixEsperada = getAdjacencyMatrix(grafoEsperado);

        assertTrue(adjacencyMatrix.isAPermutationOf(adjacencyMatrixEsperada), "Los grafos no coinciden");
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
        verificarNumVerticesAristas(Main.class, "pruebaFor", 3, 3);
    }

    @Test
    void testCurso1617Convocatoria1() {
        DirectedPseudograph<Integer, BooleanEdge> grafo = new DirectedPseudograph<>(BooleanEdge.class);
        // Añado los vertices
        grafo.addVertex(1);
        grafo.addVertex(2);
        grafo.addVertex(3);
        grafo.addVertex(4);
        grafo.addVertex(5);
        grafo.addVertex(6);
        // Añado las aristas
        grafo.addEdge(1,2, new BooleanEdge(EdgeType.DEFAULT));
        grafo.addEdge(2,3, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(2,4, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(3,2, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(3,4, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(4,5, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(4,6, new BooleanEdge(EdgeType.TRUE));
        // Compruebo que son grafos isomorfos
        verificarGrafo(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1617.Convocatoria1.class, "buscar", grafo);
    }

    @Test
    void testCurso1415Convocatoria2() {
        DirectedPseudograph<Integer, BooleanEdge> grafo = new DirectedPseudograph<>(BooleanEdge.class);
        /* ASI DEBERIA SER */
        // Añado los vertices
        grafo.addVertex(1);
        grafo.addVertex(2);
        grafo.addVertex(3);
        grafo.addVertex(4);
        grafo.addVertex(5);
        grafo.addVertex(6);
        // Añado las aristas
        grafo.addEdge(1,2, new BooleanEdge(EdgeType.DEFAULT));
        grafo.addEdge(2,3, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(2,3, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(3,3, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(3,4, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(4,5, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(4,6, new BooleanEdge(EdgeType.TRUE));
        // ASI ES SI TENEMOS EN CUENTA EL NODE PREDICADO EXTRA DE LA ASIGNACION BOOLEANA
        /*
        // Añado los vertices
        grafo.addVertex(1);
        grafo.addVertex(2);
        grafo.addVertex(3);
        grafo.addVertex(4);
        grafo.addVertex(5);
        grafo.addVertex(6);
        grafo.addVertex(7);
        // Añado las aristas
        grafo.addEdge(1,7, new BooleanEdge(EdgeType.DEFAULT));
        grafo.addEdge(7,2, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(7,2, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(2,3, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(2,3, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(3,3, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(3,4, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(4,5, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(4,6, new BooleanEdge(EdgeType.TRUE));
         */
        verificarGrafo(Convocatoria2.class, "imagen", grafo);
    }

    @Disabled("Las OR se implementan de otra manera en Bytecode")
    @Test
    void testCurso1516Convocatoria1() {
        DirectedPseudograph<Integer, BooleanEdge> grafo = new DirectedPseudograph<>(BooleanEdge.class);
        /* ASI DEBERIA SER */
        // Añado los vertices
        grafo.addVertex(1);
        grafo.addVertex(2);
        grafo.addVertex(3);
        grafo.addVertex(4);
        grafo.addVertex(5);
        grafo.addVertex(6);
        grafo.addVertex(7);
        // Añado las aristas
        grafo.addEdge(1,2, new BooleanEdge(EdgeType.DEFAULT));
        grafo.addEdge(2,3, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(2,7, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(3,4, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(3,7, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(4,5, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(4,7, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(5,6, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(6,7, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(6,5, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(6,7, new BooleanEdge(EdgeType.FALSE));
        // ASI ES SI TENEMOS EN CUENTA EL NODE PREDICADO EXTRA DE LA ASIGNACION BOOLEANA
        /*
        // Añado los vertices
        grafo.addVertex(1);
        grafo.addVertex(2);
        grafo.addVertex(3);
        grafo.addVertex(4);
        grafo.addVertex(5);
        grafo.addVertex(6);
        grafo.addVertex(7);
        // Añado las aristas
        grafo.addEdge(1,7, new BooleanEdge(EdgeType.DEFAULT));
        grafo.addEdge(7,2, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(7,2, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(2,3, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(2,3, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(3,3, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(3,4, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(4,5, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(4,6, new BooleanEdge(EdgeType.TRUE));
         */
        verificarGrafo(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1516.Convocatoria1.class, "esPrimo", grafo);
    }

    @Test
    void testCurso1819Convocatoria1() {
        DirectedPseudograph<Integer, BooleanEdge> grafo = new DirectedPseudograph<>(BooleanEdge.class);
        // Añado los vertices
        grafo.addVertex(1);
        grafo.addVertex(2);
        grafo.addVertex(3);
        grafo.addVertex(4);
        grafo.addVertex(5);
        grafo.addVertex(6);
        grafo.addVertex(7);
        grafo.addVertex(8);
        // Añado las aristas
        grafo.addEdge(1,2, new BooleanEdge(EdgeType.DEFAULT));
        grafo.addEdge(2,3, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(2,4, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(4,5, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(4,8, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(5,6, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(5,7, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(8,4, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(8,4, new BooleanEdge(EdgeType.FALSE));
        verificarGrafo(Convocatoria1.class, "buscar", grafo);
    }

    @Test
    void testCurso1920Convocatoria1() {
        DirectedPseudograph<Integer, BooleanEdge> grafo = new DirectedPseudograph<>(BooleanEdge.class);
        // Añado los vertices
        grafo.addVertex(1);
        grafo.addVertex(2);
        grafo.addVertex(3);
        grafo.addVertex(4);
        grafo.addVertex(5);
        grafo.addVertex(6);
        grafo.addVertex(7);
        grafo.addVertex(8);
        // Añado las aristas
        grafo.addEdge(1,2, new BooleanEdge(EdgeType.DEFAULT));
        grafo.addEdge(2,3, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(2,4, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(4,5, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(4,6, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(6,7, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(6,8, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(8,6, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(8,6, new BooleanEdge(EdgeType.FALSE));
        verificarGrafo(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1920.Convocatoria1.class, "minimo", grafo);
    }

    @Test
    void testCurso2122Convocatoria1() {
        DirectedPseudograph<Integer, BooleanEdge> grafo = new DirectedPseudograph<>(BooleanEdge.class);
        // Añado los vertices
        grafo.addVertex(1);
        grafo.addVertex(2);
        grafo.addVertex(3);
        grafo.addVertex(4);
        grafo.addVertex(5);
        // Añado las aristas
        grafo.addEdge(1,2, new BooleanEdge(EdgeType.DEFAULT));
        grafo.addEdge(2,3, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(2,4, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(4,2, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(4,5, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(5,2, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(5,2, new BooleanEdge(EdgeType.TRUE));
        verificarGrafo(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso2122.Convocatoria1.class, "replaceDigits", grafo);
    }

    @Test
    void testCurso2223Convocatoria1() {
        DirectedPseudograph<Integer, BooleanEdge> grafo = new DirectedPseudograph<>(BooleanEdge.class);
        // Añado los vertices
        grafo.addVertex(1);
        grafo.addVertex(2);
        grafo.addVertex(3);
        grafo.addVertex(4);
        grafo.addVertex(5);
        // Añado las aristas
        grafo.addEdge(1,2, new BooleanEdge(EdgeType.DEFAULT));
        grafo.addEdge(2,3, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(2,4, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(4,2, new BooleanEdge(EdgeType.TRUE));
        grafo.addEdge(4,5, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(5,2, new BooleanEdge(EdgeType.FALSE));
        grafo.addEdge(5,2, new BooleanEdge(EdgeType.TRUE));
        verificarGrafo(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso2223.Convocatoria1.class, "calcularPuntuacion", grafo);
    }
}