package org.juancatalan.edgepaircoverage;

import org.jgrapht.graph.DirectedPseudograph;
import org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1415.Convocatoria2;
import org.juancatalan.edgepaircoverage.utils.AdjacencyMatrix;
import org.juancatalan.edgepaircoverage.verifiers.NumVerticesAristasVerifier;
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
        NumVerticesAristasVerifier.verify(Convocatoria2.class, "imagen", 6, 7, false);
    }

    @Test
    void testAsignacionBooleanaSimple() throws NoSuchMethodException {
        NumVerticesAristasVerifier.verify(this.getClass(), "pruebaAsignacionBooleanaSimple", 3, 3, false);
    }

    @Test
    void testAsignacionBooleanaConAnd() throws NoSuchMethodException {
        NumVerticesAristasVerifier.verify(this.getClass(), "pruebaAsignacionBooleanaConAnd", 3, 3, false);
    }

    @Test
    void testAsignacionBooleanaConOr() throws NoSuchMethodException {
        NumVerticesAristasVerifier.verify(this.getClass(), "pruebaAsignacionBooleanaConOr", 3, 3, false);
    }

    @Test
    void testAsignacionBooleanaConTripleOr() throws NoSuchMethodException {
        NumVerticesAristasVerifier.verify(this.getClass(), "pruebaAsignacionBooleanaConTripleOr", 3, 3, false);
    }

    @Disabled("Aun no funciona para combinacion de puertas logicas")
    @Test
    void testAsignacionBooleanaCombinada() throws NoSuchMethodException {
        NumVerticesAristasVerifier.verify(this.getClass(), "pruebaAsignacionBooleanaCombinandoOrAnd", 3, 3, false);
    }
}