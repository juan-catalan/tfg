package org.example;

import org.example.ejerciciosExamen.curso2122.Convocatoria1;
import org.jgrapht.graph.DirectedPseudograph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.ASM4;

class CoverageMeasurementTest {
    static private String getClassName(Class clase){
        return clase.getName().replace('.', '/');
    }

    @Test
    void testCoberturaTodosCaminosCurso2122Convocatoria1() throws NoSuchMethodException {
        // Obtener clase
        Convocatoria1 convocatoria1 = new Convocatoria1();
        // Obtener identificador
        String descriptorMetodo = Type.getMethodDescriptor(convocatoria1.getClass().getMethod("replaceDigits", int.class));
        String idMetodo = getClassName(Convocatoria1.class).concat("." + "replaceDigits").concat("." + descriptorMetodo);
        // Ejecutar metodo
        convocatoria1.replaceDigits(353);
        convocatoria1.replaceDigits(15);
        convocatoria1.replaceDigits(1);

        /*
        // Comprobar si todos los caminos han sido cubiertos
        assertEquals(AddPrintConditionsTransformer.numeroCaminosTotal(idMetodo),
                AddPrintConditionsTransformer.numeroCaminosCubiertos(idMetodo));
         */
        // Comprobar si todos los caminos han sido cubiertos (excepto el camino imposible)
        assertEquals(AddPrintConditionsTransformer.numeroCaminosTotal(idMetodo) - 1,
                AddPrintConditionsTransformer.numeroCaminosCubiertos(idMetodo));
    }
}