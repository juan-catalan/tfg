package org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1819;

import org.juancatalan.edgepaircoverage.Coverage2Edge;

public class Convocatoria1 {
    /**
     * Pre:  Los valores de las componentes del parámetro «vector» están ordenados
     *       de forma no decreciente.
     * Post: Si entre las componentes de «vector» hay una cuyo valor es igual a
     *       «datoBuscado», ha devuelto su índice.
     *       En caso contrario, ha devuelto -1.
     */
    @Coverage2Edge
    public static int buscar(int[] vector, int datoBuscado) {
        if (vector.length == 0) {
            return -1;
        }
        else {
            int inf = 0;
            int sup = vector.length - 1;
            while (inf < sup) {
                int mitad = (inf + sup) / 2;
                if (datoBuscado > vector[mitad]) {
                    inf = mitad + 1;
                } else {
                    sup = mitad;
                }
            }

            if (vector[inf] == datoBuscado) {
                return inf;
            } else {
                return -1;
            }
        }
    }
}
