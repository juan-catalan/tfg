package org.example;

import java.util.Objects;

public class Camino2Edge {
    Integer nodoInicio, nodoMedio, nodoFinal;
    BooleanEdge aristaInicioMedio, aristaMedioFinal;

    public Camino2Edge(Integer nodoIni, BooleanEdge aristaIniMedio, Integer nodoMedio, BooleanEdge aristaMedioFin, Integer nodoFinal){
        this.nodoInicio = nodoIni;
        this.nodoMedio  = nodoMedio;
        this.nodoFinal = nodoFinal;

        this.aristaInicioMedio = aristaIniMedio;
        this.aristaMedioFinal = aristaMedioFin;
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