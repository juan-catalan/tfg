package org.example;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FindBooleanAssignmentTest {
    static private String getClassName(Class clase){
        return clase.getName().replace('.', '/');
    }

    // Nodos de más debido a asignacion booleana
    @Test
    void testFindBooleanAssignmentTest() throws NoSuchMethodException {
        // Obtener clase
        org.example.ejerciciosExamen.curso1415.Convocatoria2 convocatoria2 = new org.example.ejerciciosExamen.curso1415.Convocatoria2();
        // Obtener identificador
        String descriptorMetodo = Type.getMethodDescriptor(convocatoria2.getClass().getMethod("imagen", int.class));
        String idMetodo = getClassName(convocatoria2.getClass()).concat("." + "imagen").concat("." + descriptorMetodo);
        // Ejecutar metodo
        convocatoria2.imagen(-78);
        convocatoria2.imagen(7);
        convocatoria2.imagen(0);

        // Comprobar si todos los caminos han sido cubiertos (excepto el camino imposible)
        assertEquals(AddPrintConditionsTransformer.numeroCaminosTotal(idMetodo) - 1,
                AddPrintConditionsTransformer.numeroCaminosCubiertos(idMetodo));
    }
}