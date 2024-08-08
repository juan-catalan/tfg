package org.juancatalan.edgepaircoverage;

import java.util.List;

public class MethodReportDTO {
    public String nombre;
    public String grafo;
    public String grafoImagen;
    public Integer caminosImposibles;
    public List<EdgePair> caminos;
    public List<EdgePair> caminosCubiertos;
    public Double porcentajeCobertura;

    public MethodReportDTO(String nombre, String grafo, String grafoImagen, Integer caminosImposibles, List<EdgePair> caminos, List<EdgePair> caminosCubiertos, Double porcentajeCobertura) {
        this.nombre = nombre;
        this.grafo = grafo;
        this.grafoImagen = grafoImagen;
        this.caminosImposibles = caminosImposibles;
        this.caminos = caminos;
        this.caminosCubiertos = caminosCubiertos;
        this.porcentajeCobertura = porcentajeCobertura;
    }

    @Override
    public String toString() {
        return "MethodReportDTO{" +
                "nombre='" + nombre + '\'' +
                ", grafo='" + grafo + '\'' +
                ", grafoImagen='" + grafoImagen + '\'' +
                ", caminosImposibles=" + caminosImposibles +
                ", caminos=" + caminos +
                ", caminosCubiertos=" + caminosCubiertos +
                ", porcentajeCobertura=" + porcentajeCobertura +
                '}';
    }
}
