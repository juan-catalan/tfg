package org.juancatalan.edgepaircoverage;

import java.util.List;

public class MethodReportDTO {
    public String nombre;
    public String grafo;
    public String grafoImagen;
    public Integer caminosImposibles;
    public List<Camino2Edge> caminos;
    public List<Camino2Edge> caminosCubiertos;
    public Double porcentajeCobertura;

    public MethodReportDTO(String nombre, String grafo, String grafoImagen, Integer caminosImposibles, List<Camino2Edge> caminos, List<Camino2Edge> caminosCubiertos, Double porcentajeCobertura) {
        this.nombre = nombre;
        this.grafo = grafo;
        this.grafoImagen = grafoImagen;
        this.caminosImposibles = caminosImposibles;
        this.caminos = caminos;
        this.caminosCubiertos = caminosCubiertos;
        this.porcentajeCobertura = porcentajeCobertura;
    }
}
