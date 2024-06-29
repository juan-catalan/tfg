package org.example;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testCoberturaTodosCaminosCurso1819Convocatoria1() throws NoSuchMethodException {
        // Obtener clase
        org.example.ejerciciosExamen.curso1819.Convocatoria1 convocatoria1 = new org.example.ejerciciosExamen.curso1819.Convocatoria1();
        // Obtener identificador
        String descriptorMetodo = Type.getMethodDescriptor(convocatoria1.getClass().getMethod("buscar", int[].class, int.class));
        String idMetodo = getClassName(convocatoria1.getClass()).concat("." + "buscar").concat("." + descriptorMetodo);
        // Ejecutar metodo
        convocatoria1.buscar(new int[]{}, 8);
        convocatoria1.buscar(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, 6);
        convocatoria1.buscar(new int[]{1}, 2);
        convocatoria1.buscar(new int[]{1, 2}, 1);

        // Comprobar si todos los caminos han sido cubiertos
        assertEquals(AddPrintConditionsTransformer.numeroCaminosTotal(idMetodo),
                AddPrintConditionsTransformer.numeroCaminosCubiertos(idMetodo));
    }

    @Test
    void testCoberturaTodosCaminosCurso1617Convocatoria1() throws NoSuchMethodException {
        // Obtener clase
        org.example.ejerciciosExamen.curso1617.Convocatoria1 convocatoria1 = new org.example.ejerciciosExamen.curso1617.Convocatoria1();
        // Obtener identificador
        String descriptorMetodo = Type.getMethodDescriptor(convocatoria1.getClass().getMethod("buscar", int[].class, int.class));
        String idMetodo = getClassName(convocatoria1.getClass()).concat("." + "buscar").concat("." + descriptorMetodo);
        // Ejecutar metodo
        convocatoria1.buscar(new int[]{11, 12, 13}, 12);
        convocatoria1.buscar(new int[]{22}, 22);
        convocatoria1.buscar(new int[]{21, 22}, 20);

        // Comprobar si todos los caminos han sido cubiertos (excepto el camino imposible)
        assertEquals(AddPrintConditionsTransformer.numeroCaminosTotal(idMetodo) - 1,
                AddPrintConditionsTransformer.numeroCaminosCubiertos(idMetodo));
    }

    // Nodos de más debido a asignacion booleana
    @Test
    void testCoberturaTodosCaminosCurso1516Convocatoria1() throws NoSuchMethodException {
        // Obtener clase
        org.example.ejerciciosExamen.curso1516.Convocatoria1 convocatoria1 = new org.example.ejerciciosExamen.curso1516.Convocatoria1();
        // Obtener identificador
        String descriptorMetodo = Type.getMethodDescriptor(convocatoria1.getClass().getMethod("esPrimo", int.class));
        String idMetodo = getClassName(convocatoria1.getClass()).concat("." + "esPrimo").concat("." + descriptorMetodo);
        // Ejecutar metodo
        convocatoria1.esPrimo(2);
        convocatoria1.esPrimo(1);
        convocatoria1.esPrimo(4);
        convocatoria1.esPrimo(11);
        convocatoria1.esPrimo(13);
        convocatoria1.esPrimo(17);
        convocatoria1.esPrimo(19);
        convocatoria1.esPrimo(23);
        convocatoria1.esPrimo(9);

        // Comprobar si todos los caminos han sido cubiertos (excepto el camino imposible)
        assertEquals(AddPrintConditionsTransformer.numeroCaminosTotal(idMetodo) - 1,
                AddPrintConditionsTransformer.numeroCaminosCubiertos(idMetodo));
    }

    // Nodos de más debido a asignacion booleana
    @Test
    void testCoberturaTodosCaminosCurso1415Convocatoria1() throws NoSuchMethodException {
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