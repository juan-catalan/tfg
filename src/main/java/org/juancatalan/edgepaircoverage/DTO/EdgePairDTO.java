package org.juancatalan.edgepaircoverage.DTO;


import org.juancatalan.edgepaircoverage.graphs.EdgeType;

import java.util.Objects;

public class EdgePairDTO {
    public String nodoInicio, nodoMedio, nodoFinal;
    public EdgeType aristaInicioMedio, aristaMedioFinal;

    public EdgePairDTO(String nodoIni, EdgeType aristaIniMedio, String nodoMedio, EdgeType aristaMedioFin, String nodoFinal){
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
        EdgePairDTO edgePair = (EdgePairDTO) o;
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
