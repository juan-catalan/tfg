package org.example;

import java.util.List;

public class MethodReportDTO {
    public String nombre;
    public String grafo;
    public String grafoImagen;
    public List<String> caminos;
    public List<String> caminosCubiertos;

    public MethodReportDTO(String nombre, String grafo, String grafoImagen, List<String> caminos, List<String> caminosCubiertos) {
        this.nombre = nombre;
        this.grafo = grafo;
        this.grafoImagen = grafoImagen;
        this.caminos = caminos;
        this.caminosCubiertos = caminosCubiertos;
    }
}
