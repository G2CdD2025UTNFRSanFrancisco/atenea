package ar.edu.utn.sanfrancisco.atenea.domain.account;

public record HierarchyLevel(int value) implements Comparable<HierarchyLevel> {

    public HierarchyLevel {
        if (value < 0) throw new IllegalArgumentException("Hierarchy level cannot be negative");
        if (value > 100) throw new IllegalArgumentException("Hierarchy level cannot be greater than 100");
    }

    public static HierarchyLevel max() {
        return new HierarchyLevel(100);
    }
    public static HierarchyLevel min() {
        return new HierarchyLevel(0);
    }

    public boolean isHigherThan(final HierarchyLevel other) {
        return this.value > other.value;
    }

    public boolean canCommand(final HierarchyLevel other) {
        return this.isHigherThan(other);
    }

    @Override
    public int compareTo(final HierarchyLevel other) {
        return Integer.compare(this.value, other.value);
    }
}