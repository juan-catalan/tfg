package org.example;

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
        org.example.ejerciciosExamen.curso2122.Convocatoria1 convocatoria1 = new org.example.ejerciciosExamen.curso2122.Convocatoria1();
        // Obtener identificador
        String descriptorMetodo = Type.getMethodDescriptor(convocatoria1.getClass().getMethod("replaceDigits", int.class));
        String idMetodo = getClassName(convocatoria1.getClass()).concat("." + "replaceDigits").concat("." + descriptorMetodo);
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

    @Test
    void testCoberturaTodosCaminosCurso2223Convocatoria1() throws NoSuchMethodException {
        // Obtener clase
        org.example.ejerciciosExamen.curso2223.Convocatoria1 convocatoria1 = new org.example.ejerciciosExamen.curso2223.Convocatoria1();
        // Obtener identificador
        String descriptorMetodo = Type.getMethodDescriptor(convocatoria1.getClass().getMethod("calcularPuntuacion"));
        String idMetodo = getClassName(convocatoria1.getClass()).concat("." + "calcularPuntuacion").concat("." + descriptorMetodo);
        // Ejecutar metodo
        convocatoria1.setBolos(new int[]{10, 10});
        convocatoria1.calcularPuntuacion();
        convocatoria1.setBolos(new int[]{4, 6, 10, 4, 4, 10, 4, 6});
        convocatoria1.calcularPuntuacion();
        convocatoria1.setBolos(new int[]{});
        convocatoria1.calcularPuntuacion();
        convocatoria1.setBolos(new int[]{4, 3});
        convocatoria1.calcularPuntuacion();
        convocatoria1.setBolos(new int[]{4, 6, 4, 4, 4, 6});
        convocatoria1.calcularPuntuacion();
        /*
        convocatoria1.setBolos(new int[]{1, 1});
        convocatoria1.calcularPuntuacion();

         */

        // Comprobar si todos los caminos han sido cubiertos
        assertEquals(AddPrintConditionsTransformer.numeroCaminosTotal(idMetodo),
                AddPrintConditionsTransformer.numeroCaminosCubiertos(idMetodo));
    }

    @Test
    void testCoberturaTodosCaminosCurso1920Convocatoria1() throws NoSuchMethodException {
        // Obtener clase
        org.example.ejerciciosExamen.curso1920.Convocatoria1 convocatoria1 = new org.example.ejerciciosExamen.curso1920.Convocatoria1();
        // Obtener identificador
        String descriptorMetodo = Type.getMethodDescriptor(convocatoria1.getClass().getMethod("minimo", int[].class));
        String idMetodo = getClassName(convocatoria1.getClass()).concat("." + "minimo").concat("." + descriptorMetodo);
        // Ejecutar metodo
        assertThrows(NullPointerException.class, ()->{
            convocatoria1.minimo(null);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            convocatoria1.minimo(new int[]{});
        });
        convocatoria1.minimo(new int[]{3, 2, 4, 1});
        convocatoria1.minimo(new int[]{1});
        convocatoria1.minimo(new int[]{1, 2});

        // Comprobar si todos los caminos han sido cubiertos
        assertEquals(AddPrintConditionsTransformer.numeroCaminosTotal(idMetodo),
                AddPrintConditionsTransformer.numeroCaminosCubiertos(idMetodo));
    }
}