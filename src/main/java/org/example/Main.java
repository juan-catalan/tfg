package org.example;

public class Main {
    @Coverage2Edge
    public static void pruebaFor(){
        for (int i=0; i<5; i++){
            System.out.println(i);
        }
    }

    @Coverage2Edge
    public static void pruebaWhile(){
        int i = 0;
        while(i < 5){
            System.out.println(i);
            i++;
        }
    }

    @Coverage2Edge
    public static void pruebaWhileForCombinado(){
        int i = 0;
        while(i < 5){
            System.out.println("i: ".concat(String.valueOf(i)));
            for (int j=0; j<5; j++){
                System.out.println("\nj: ".concat(String.valueOf(j)));
            }
            i++;
        }
    }

    @Coverage2Edge
    public static void pruebaThrows(){
        int i = 0;
        if (System.currentTimeMillis() % 2 == 0){
            throw new RuntimeException("Prueba");
        }
        System.out.println("No excepcion");
    }

    @Coverage2Edge
    public static void antiguoMain(){
        // Index: 7
        if (System.currentTimeMillis() % 2 == 0){
            System.out.println("Milis ext pares");
            // Index: 26
            if (System.currentTimeMillis() % 2 == 0){
                System.out.println("Milis int pares");
            }
            else {
                System.out.println("Milis int impares");
            }
        }
        else {
            System.out.println("Milis ext impares");
            // Index: 65
            if (System.currentTimeMillis() % 2 == 0){
                System.out.println("Milis int pares");
            }
            else {
                System.out.println("Milis int impares");
            }
        }
    }


    public static void main(String[] args) {
        antiguoMain();

        pruebaFor();

        pruebaWhile();

        antiguoMain();
    }

}