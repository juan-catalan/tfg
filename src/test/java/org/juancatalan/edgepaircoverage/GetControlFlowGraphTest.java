package org.juancatalan.edgepaircoverage;

import org.jgrapht.graph.DirectedPseudograph;
import org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1415.Convocatoria2;
import org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1819.Convocatoria1;
import org.juancatalan.edgepaircoverage.graphs.BooleanEdge;
import org.juancatalan.edgepaircoverage.graphs.EdgeType;
import org.juancatalan.edgepaircoverage.verifiers.IsomorfismoGrafosVerifier;
import org.juancatalan.edgepaircoverage.verifiers.NumVerticesAristasVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class GetControlFlowGraphTest {
    @Test
    void testPruebaFor() {
        NumVerticesAristasVerifier.verify(Main.class, "pruebaFor", 3, 3, false);
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
        IsomorfismoGrafosVerifier.verify(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1617.Convocatoria1.class, "buscar", grafo);
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
        IsomorfismoGrafosVerifier.verify(Convocatoria2.class, "imagen", grafo);
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
        IsomorfismoGrafosVerifier.verify(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1516.Convocatoria1.class, "esPrimo", grafo);
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
        IsomorfismoGrafosVerifier.verify(Convocatoria1.class, "buscar", grafo);
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
        IsomorfismoGrafosVerifier.verify(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1920.Convocatoria1.class, "minimo", grafo);
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
        IsomorfismoGrafosVerifier.verify(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso2122.Convocatoria1.class, "replaceDigits", grafo);
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
        IsomorfismoGrafosVerifier.verify(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso2223.Convocatoria1.class, "calcularPuntuacion", grafo);
    }
}