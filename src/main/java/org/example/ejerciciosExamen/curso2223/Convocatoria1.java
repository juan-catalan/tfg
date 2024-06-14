package org.example.ejerciciosExamen.curso2223;

import org.example.Coverage2Edge;

public class Convocatoria1 {
    private int[] bolos;

    public int bolos(int lanzamiento) {
        if (lanzamiento < this.bolos.length) {
            return bolos[lanzamiento];
        } else {
            return 0;
        }
    }

    @Coverage2Edge
    public int calcularPuntuacion() {
        int puntuacion = 0;
        int lanzamiento = 0;
        while (   lanzamiento < bolos.length) {
            if (   bolos(lanzamiento) == 10) {
                puntuacion += 10 + bolos(lanzamiento + 1) + bolos(lanzamiento + 2);
                lanzamiento++;
            } else if (   bolos(lanzamiento) + bolos(lanzamiento + 1) == 10) {
                puntuacion += 10 + bolos(lanzamiento + 2);
                lanzamiento += 2;
            } else {
                puntuacion += bolos(lanzamiento) + bolos(lanzamiento + 1);
                lanzamiento += 2;
            }
        }
        return puntuacion;
    }

    public void setBolos(int[] bolos) {
        this.bolos = bolos;
    }
}
