package org.juancatalan.edgepaircoverage.ejerciciosExamen.curso1617;

import org.juancatalan.edgepaircoverage.Coverage2Edge;

public class Convocatoria1 {
    /**
     * Busca un dato determinado en un vector de enteros.
     *
     * @param vector
     *            - el vector en el que se busca el dato «datoBuscado». Debe
     *            ser distinto de null y tener al menos una componente.
     * @param datoBuscado
     *            - el dato que se quiere buscar en el vector «vector».
     *
     * @return Si en el vector «vector» hay un dato igual a «datoBuscado»,
     *         devuelve el índice de la componente en la que se encuentra.
     *         En caso contrario, devuelve -1;
     */
    @Coverage2Edge(numCaminosImposibles = 1)
    public static int buscar(int[] vector, int datoBuscado) {
        int i = 0;

        while (i < vector.length - 1 && vector[i] != datoBuscado) {
            i++;
        }

        if (vector[i] == datoBuscado) {
            return i;
        } else {
            return -1;
        }
    }
}
