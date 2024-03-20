package org.example;

import org.jgrapht.graph.DefaultEdge;

import java.util.Objects;

public class BooleanEdge extends DefaultEdge {
    private EdgeType type;

    public BooleanEdge(EdgeType type)
    {
        this.type = type;
    }

    /**
     * Gets the label associated with this edge.
     *
     * @return edge label
     */
    public EdgeType getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return type.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanEdge that = (BooleanEdge) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
