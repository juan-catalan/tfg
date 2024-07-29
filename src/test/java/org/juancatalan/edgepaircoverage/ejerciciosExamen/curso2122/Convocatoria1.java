package org.juancatalan.edgepaircoverage.ejerciciosExamen.curso2122;

import org.juancatalan.edgepaircoverage.Coverage2Edge;

public class Convocatoria1 {
    public static final String FOO_STRING = "Foo";
    public static final String BAR_STRING = "Bar";

    @Coverage2Edge(numCaminosImposibles = 1)
    /**
     * Devuelve una cadena de caracteres en la que cada aparición del dígito 3 del
     * parámetro n se ha sustituido por la cadena FOO_STRING, cada aparición del
     * dígito 5 de n se ha sustituido por la cadena BAR_STRING y las apariciones
     * del resto de los dígitos se han ignorado.
     *
     * Por ejemplo:
     *      replaceDigits(1) devuelve la cadena vacía
     *      replaceDigits(3) devuelve "Foo"
     *      replaceDigits(5) devuelve "Bar"
     *      replaceDigits(5303) devuelve "BarFooFoo"
     *
     * @param n el número del que se quiere averiguar su representación FooBar
     *
     * @return una cadena de caracteres compuesta por la concatenación de las
     *     cadenas FOO_STRING y BAR_STRING en función de las apariciones y orden
     *     de los dígitos 3 y 5 presentes en n.
     */
    public String replaceDigits(int n) {
        String result = "";
        String digitString = Integer.toString(n);
        int i = 0;
        while (   i < digitString.length()) {
            if (   digitString.charAt(i) == '3') {
                result += FOO_STRING;
            } else if (   digitString.charAt(i) == '5') {
                result += BAR_STRING;
            }
            i++;
        }
        return result;
    }
}
