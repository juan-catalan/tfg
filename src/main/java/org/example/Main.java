package org.example;

public class Main {
    public static void main(String[] args) {
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
}