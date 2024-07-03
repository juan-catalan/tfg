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

class ObtenerSituacionesPruebaTest {

    void verificarNumSituacionesPrueba(Class clase, String nombreMetodo, int numSituacionesPrueba){
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
        Set<Camino2Edge> situacionesPrueba = transformer.obtenerSituacionesPrueba(idMetodo, controlFlowGraph);


        assertEquals(numSituacionesPrueba, situacionesPrueba.size());
    }

    @Test
    void testCurso1415Convocatoria2() {
        verificarNumSituacionesPrueba(org.example.ejerciciosExamen.curso1415.Convocatoria2.class,
                "imagen", 10);
    }

    @Test
    void testCurso1516Convocatoria1() {
        verificarNumSituacionesPrueba(org.example.ejerciciosExamen.curso1516.Convocatoria1.class,
                "esPrimo", 12);
    }

    @Test
    void testCurso1617Convocatoria1() {
        verificarNumSituacionesPrueba(org.example.ejerciciosExamen.curso1617.Convocatoria1.class,
                "buscar", 10);
    }

    @Test
    void testCurso1819Convocatoria1() {
        verificarNumSituacionesPrueba(org.example.ejerciciosExamen.curso1819.Convocatoria1.class,
                "buscar", 12);
    }

    @Test
    void testCurso1920Convocatoria1() {
        verificarNumSituacionesPrueba(org.example.ejerciciosExamen.curso1920.Convocatoria1.class,
                "minimo", 11);
    }

    @Test
    void testCurso2122Convocatoria1() {
        verificarNumSituacionesPrueba(org.example.ejerciciosExamen.curso2122.Convocatoria1.class,
                "replaceDigits", 12);
    }

    @Test
    void testCurso2223Convocatoria1() {
        verificarNumSituacionesPrueba(org.example.ejerciciosExamen.curso2223.Convocatoria1.class,
                "calcularPuntuacion", 12);
    }
}