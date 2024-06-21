package org.example;

public class MethodReportDTO {
    public String nombre;
    public String grafo;
    public String caminos;
    public String caminosCubiertos;

    public MethodReportDTO(String nombre, String grafo, String caminos, String caminosCubiertos) {
        this.nombre = nombre;
        this.grafo = grafo;
        this.caminos = caminos;
        this.caminosCubiertos = caminosCubiertos;
    }
}
