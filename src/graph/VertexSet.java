package graph;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by zzt on 11/15/15.
 * <p>
 * Usage:
 */
public class VertexSet {

    private ArrayList<Integer> indexes = new ArrayList<>();

    public VertexSet() {
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
                if (indexes.get(i).equals(this.indexes.get(i))) {
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
}
