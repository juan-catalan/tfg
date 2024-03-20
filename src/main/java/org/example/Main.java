package org.example;

public class Main {
    @Coverage2Edge
    public static void pruebaFor(int j){
        for (int i=0; i<j; i++){
            System.out.println(i);
        }
    }

    @Coverage2Edge
    public static void pruebaWhile(int i){
        while(i < 5){
            System.out.println(i);
            i++;
        }
    }

    @Coverage2Edge
    public static void pruebaWhileForCombinado(int i, int j){
        while(i < 5){
            System.out.println("i: ".concat(String.valueOf(i)));
            for (int k=0; k<j; k++){
                System.out.println("\nj: ".concat(String.valueOf(k)));
            }
            i++;
        }
    }

    @Coverage2Edge
    public static void pruebaThrows(int i){
        if (i % 2 == 0){
            throw new RuntimeException("Excepcion");
        }
        System.out.println("No excepcion");
    }

    @Coverage2Edge
    public static void pruebaIfConAnd(int i){
        if (i%2 == 0 && i%3 ==0){
            System.out.println("Multiplo de 2 y 3");
        }
        System.out.println("No multiplo");
    }

    @Coverage2Edge
    public static void antiguoMain(int i, int j){
        // Index: 7
        if (i % 2 == 0){
            System.out.println("i par");
            // Index: 26
            if (j % 2 == 0){
                System.out.println("j par");
            }
            else {
                System.out.println("j impar");
            }
        }
        else {
            System.out.println("i impar");
            // Index: 65
            if (j % 2 == 0){
                System.out.println("j par");
            }
            else {
                System.out.println("j impar");
            }
        }
    }


    public static void main(String[] args) {
        antiguoMain(2,1);

        pruebaFor(5);

        pruebaWhile(4);

        try {
            pruebaThrows(2);
        }catch (Exception e){
            System.out.println(e.getMessage());
        };


        antiguoMain(1,2);
    }

}