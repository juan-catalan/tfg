package org.example.ejerciciosExamen.curso1415;

import org.example.Coverage2Edge;

public class Convocatoria2 {
    @Coverage2Edge(numCaminosImposibles = 1)
    public static int imagen(int n) {
        boolean negativo = n < 0;
        if (negativo) {
            n = -n;
        }

        int imagenEspecular = 0;
        while (n != 0) {
            imagenEspecular = 10 * imagenEspecular + n % 10;
            n = n / 10;
        }

        if (negativo) {
            return -imagenEspecular;
        }
        else {
            return imagenEspecular;
        }
    }
}
