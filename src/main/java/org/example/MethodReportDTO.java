package org.example;

public class MethodReportDTO {
    public String nombre;
    public String grafo;
    public String grafoImagen;
    public String caminos;
    public String caminosCubiertos;

    public MethodReportDTO(String nombre, String grafo, String grafoImagen, String caminos, String caminosCubiertos) {
        this.nombre = nombre;
        this.grafo = grafo;
        this.grafoImagen = grafoImagen;
        this.caminos = caminos;
        this.caminosCubiertos = caminosCubiertos;
    }
}
