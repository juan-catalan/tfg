package org.example.ejerciciosExamen.curso1920;

public class Convocatoria1 {
    /**
     * Devuelve el valor del mínimo elemento de «v».
     *
     * @param v el vector en el que se busca el mínimo.
     * @return el valor del mínimo elemento de entre los almacenados en el
     *         vector «v», este es no nulo y tiene al menos una componente.
     * @throws NullPointerException cuando v es null
     * @throws IllegalArgumentException cuando v tiene 0 componentes
     */
    public int minimo(int[] v) {
        if (v == null) {
            throw new NullPointerException("v nulo");
        } else if (v.length == 0) {
            throw new IllegalArgumentException("v de 0 componentes");
        } else {
            int minimo = v[0];
            int i = 1;
            while (i < v.length) {
                if (v[i] < minimo) {
                    minimo = v[i];
                }
                i++;
            }
            return minimo;
        }
    }
}
