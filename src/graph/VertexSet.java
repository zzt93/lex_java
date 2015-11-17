package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zzt on 11/15/15.
 * <p>
 * Usage:
 */
public class VertexSet {

    private ArrayList<Integer> indexes = new ArrayList<>();

    public VertexSet() {
    }

    public VertexSet(List<Vertex> vertices) {
        indexes.addAll(vertices.stream().map(Vertex::ordinal).collect(Collectors.toList()));
    }

    public VertexSet(ArrayList<Integer> src) {
        indexes.addAll(src);
    }

    public ArrayList<Integer> getIndexes() {
        return indexes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VertexSet vertexSet = (VertexSet) o;

        ArrayList<Integer> indexes = vertexSet.indexes;
        if (this.indexes != null && indexes != null) {
            if (this.indexes.size() != indexes.size()) {
                return false;
            }
            Collections.sort(this.indexes);
            Collections.sort(indexes);
            for (int i = 0; i < this.indexes.size(); i++) {
                if (!indexes.get(i).equals(this.indexes.get(i))) {
                    return false;
                }
            }
            return true;
        }
        throw new RuntimeException("indexes should not be null");
    }

    @Override
    public int hashCode() {
        if (indexes != null) {
            return indexes.stream().mapToInt(Integer::intValue).sum();
        }
        return 0;
    }

    public void add(Integer ordinal) {
        indexes.add(ordinal);
    }

    public void addAll(VertexSet tmp) {
        indexes.addAll(tmp.getIndexes());
    }

    public int size() {
        return indexes.size();
    }

    public static boolean contain(ArrayList<VertexSet> oldState, VertexSet list) {
        for (VertexSet vertexSet : oldState) {
            if (vertexSet.equals(list)) {
                return true;
            }
        }
        return false;
    }

    public static int get(ArrayList<VertexSet> oldState, VertexSet list) {
        for (int i = 0; i < oldState.size(); i++) {
            if (list.equals(oldState.get(i))) {
                return i;
            }
        }
        return -1;
    }
}
