package org.example.ejerciciosExamen.curso1516;

import org.example.Coverage2Edge;

public class Convocatoria1 {
    /**
     * @param  n -  un entero cualquiera
     * @return «true» si y solo si «n» es un número primo (es decir,
     *         si es positivo y tiene exactamente dos divisores: 1 y n
     */
    @Coverage2Edge(numCaminosImposibles = 1)
    public static boolean esPrimo(int n) {
        boolean esPrimo;
        if (n == 2) { // L0
            // L2
            esPrimo = true; // «n» es igual a 2, luego es primo
        }
        else if (n < 2 || n % 2 == 0) { // L1
            esPrimo = false; // «n» es menor que 2 o divisible por 2
        }
        else { // L6
            // Se buscan posibles divisores impares de «n»
            boolean divisorEncontrado = false;
            int divisor = 3; // Primer divisor impar a probar
            while (!divisorEncontrado && divisor * divisor <= n) {
                divisorEncontrado = n % divisor == 0;
                divisor = divisor + 2;
            }
            esPrimo = !divisorEncontrado;
        }
        // L5, L3, L7, L4
        return esPrimo;
    }
}
