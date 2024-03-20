package org.example;


import java.util.Objects;

public class Camino2Edge {
    Integer nodoInicio, nodoMedio, nodoFinal;
    EdgeType aristaInicioMedio, aristaMedioFinal;

    public Camino2Edge(Integer nodoIni, EdgeType aristaIniMedio, Integer nodoMedio, EdgeType aristaMedioFin, Integer nodoFinal){
        this.nodoInicio = nodoIni;
        this.nodoMedio  = nodoMedio;
        this.nodoFinal = nodoFinal;

        this.aristaInicioMedio = aristaIniMedio;
        this.aristaMedioFinal = aristaMedioFin;
    }

    public boolean isComplete(){
        return nodoInicio != null && nodoMedio != null && nodoFinal != null &&
                aristaInicioMedio != null && aristaMedioFinal != null;
    }

    public Camino2Edge nextHalf (){
        return new Camino2Edge(nodoMedio, aristaMedioFinal, nodoFinal, null, null);
    }

    public void addNode(Integer node){
        if (nodoInicio == null) nodoInicio = node;
        else if (nodoMedio == null) nodoMedio = node;
        else if (nodoFinal == null) nodoFinal = node;
    }

    public void addEdge(EdgeType edge){
        if (aristaInicioMedio == null) aristaInicioMedio = edge;
        else if (aristaMedioFinal == null) aristaMedioFinal = edge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Camino2Edge camino2Edge = (Camino2Edge) o;
        return Objects.equals(nodoInicio, camino2Edge.nodoInicio) && Objects.equals(nodoMedio, camino2Edge.nodoMedio) && Objects.equals(nodoFinal, camino2Edge.nodoFinal) && Objects.equals(aristaInicioMedio, camino2Edge.aristaInicioMedio) && Objects.equals(aristaMedioFinal, camino2Edge.aristaMedioFinal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodoInicio, nodoMedio, nodoFinal, aristaInicioMedio, aristaMedioFinal);
    }

    @Override
    public String toString() {
        return  nodoInicio +
                " ----" + aristaInicioMedio + "--> " +
                nodoMedio +
                " ----" + aristaMedioFinal + "--> " +
                nodoFinal;
    }
}
