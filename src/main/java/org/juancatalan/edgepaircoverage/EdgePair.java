package org.juancatalan.edgepaircoverage;


import java.util.Objects;

public class EdgePair {
    public Integer nodoInicio, nodoMedio, nodoFinal;
    public EdgeType aristaInicioMedio, aristaMedioFinal;

    public EdgePair(Integer nodoIni, EdgeType aristaIniMedio, Integer nodoMedio, EdgeType aristaMedioFin, Integer nodoFinal){
        this.nodoInicio = nodoIni;
        this.nodoMedio  = nodoMedio;
        this.nodoFinal = nodoFinal;

        this.aristaInicioMedio = aristaIniMedio;
        this.aristaMedioFinal = aristaMedioFin;
    }

    boolean isComplete(){
        return nodoInicio != null && nodoMedio != null && nodoFinal != null &&
                aristaInicioMedio != null && aristaMedioFinal != null;
    }

    public EdgePair nextHalf (){
        return new EdgePair(nodoMedio, aristaMedioFinal, nodoFinal, null, null);
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
        EdgePair edgePair = (EdgePair) o;
        return Objects.equals(nodoInicio, edgePair.nodoInicio) && Objects.equals(nodoMedio, edgePair.nodoMedio) && Objects.equals(nodoFinal, edgePair.nodoFinal) && Objects.equals(aristaInicioMedio, edgePair.aristaInicioMedio) && Objects.equals(aristaMedioFinal, edgePair.aristaMedioFinal);
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
