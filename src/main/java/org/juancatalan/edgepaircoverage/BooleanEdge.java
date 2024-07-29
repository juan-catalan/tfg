package org.juancatalan.edgepaircoverage;

import org.jgrapht.graph.DefaultEdge;

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
        if (!(o instanceof BooleanEdge that)) return false;
        return super.equals(o) && type.equals(that.getType());
    }
}
