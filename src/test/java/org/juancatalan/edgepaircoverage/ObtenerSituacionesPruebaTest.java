package org.juancatalan.edgepaircoverage;

import org.jgrapht.graph.DirectedPseudograph;
import org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1415.Convocatoria2;
import org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1819.Convocatoria1;
import org.juancatalan.edgepaircoverage.verifiers.NumSituacionsPruebaVerifier;
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

class ObtenerSituacionesPruebaTest {
    @Test
    void testCurso1415Convocatoria2() {
        NumSituacionsPruebaVerifier.verify(Convocatoria2.class,
                "imagen", 10);
    }

    @Test
    void testCurso1516Convocatoria1() {
        NumSituacionsPruebaVerifier.verify(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1516.Convocatoria1.class,
                "esPrimo", 12);
    }

    @Test
    void testCurso1617Convocatoria1() {
        NumSituacionsPruebaVerifier.verify(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1617.Convocatoria1.class,
                "buscar", 10);
    }

    @Test
    void testCurso1819Convocatoria1() {
        NumSituacionsPruebaVerifier.verify(Convocatoria1.class,
                "buscar", 12);
    }

    @Test
    void testCurso1920Convocatoria1() {
        NumSituacionsPruebaVerifier.verify(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1920.Convocatoria1.class,
                "minimo", 11);
    }

    @Test
    void testCurso2122Convocatoria1() {
        NumSituacionsPruebaVerifier.verify(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso2122.Convocatoria1.class,
                "replaceDigits", 12);
    }

    @Test
    void testCurso2223Convocatoria1() {
        NumSituacionsPruebaVerifier.verify(org.juancatalan.edgepaircoverage.ejerciciosExamen.curso2223.Convocatoria1.class,
                "calcularPuntuacion", 12);
    }
}