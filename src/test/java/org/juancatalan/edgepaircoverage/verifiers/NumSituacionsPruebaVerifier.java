package org.juancatalan.edgepaircoverage.verifiers;

import org.juancatalan.edgepaircoverage.controlFlow.ControlFlowAnalyser;
import org.juancatalan.edgepaircoverage.controlFlow.EdgePair;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.objectweb.asm.Opcodes.ASM4;

public class NumSituacionsPruebaVerifier {
    public static void verify(Class clase, String nombreMetodo, int numSituacionesPrueba){
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
        ControlFlowAnalyser controlFlowAnalyser = new ControlFlowAnalyser(false);
        controlFlowAnalyser.analyze(idMetodo, listaInstrucciones);
        Set<EdgePair> situacionesPrueba = controlFlowAnalyser.obtenerSituacionesPrueba(idMetodo);

        assertEquals(numSituacionesPrueba, situacionesPrueba.size());
    }
}
