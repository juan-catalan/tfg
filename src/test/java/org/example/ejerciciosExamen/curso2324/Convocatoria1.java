package org.example.ejerciciosExamen.curso2324;

public class Convocatoria1 {
    /**
     * Dada una palabra oculta, una secuencia de letras correspondiente a     *
     * intentos de adivinar letras de la palabra y un número máximo de fallos
     * permitidos, determina si dicha secuencia permite adivinar todas las
     * letras de la palabra oculta en una partida del juego del ahorcado.
     * @param palabraOculta palabra que hay que adivinar en la partida
     * @param letras vector correspondiente a los intentos del jugador por
     *               adivinar las letras de la palabra
     * @param maxFallos número de fallos que hace que se pierda la partida
     * @return true si la secuencia de letras permite descubrir la palabra
     *         sin alcanzar el número máximo de fallos, false en caso
     *         contrario (se alcanza el número máximo de fallos o quedan
     *         letras de la palabra oculta sin acertar)
     */
    public boolean jugar(String palabraOculta, char[] letras, int maxFallos) {
        int fallos = 0;
        int aciertos = 0;
        int i = 0;
        while (    fallos < maxFallos
                &&    aciertos < palabraOculta.length()
                &&    i < letras.length) {
            final char letra = letras[i];
            if (    palabraOculta.contains(String.valueOf(letra))) {
                // Se cuenta el número de repeticiones de «letra» en la palabra
                // y se incrementa «aciertos»:
                aciertos += (int) palabraOculta.chars()
                        .filter(ch -> ch == letra).count();
                // Se elimina de la palabra la letra acertada:
                palabraOculta = palabraOculta.replace(letra, '-');
            } else {
                fallos++;
            }
            i++;
        }
        return aciertos == palabraOculta.length();
    }
}
