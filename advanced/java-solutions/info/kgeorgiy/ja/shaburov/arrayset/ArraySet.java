package info.kgeorgiy.ja.shaburov.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {
    private final List<T> list;
    private final Comparator<? super T> comp;

    public ArraySet() {
        list = List.of();
        comp = null;
    }

    public ArraySet(Comparator<? super T> comp) {
        this.list = Collections.emptyList();
        this.comp = comp;
    }

    public ArraySet(Collection<T> list) {
        // :NOTE:         this.list = List.copyOf(new TreeSet<>(list));
        this.list = List.copyOf(new TreeSet<>(list));
        comp = null;
    }

    public ArraySet(Collection<T> list, Comparator<? super T> comp) {
        this.comp = comp;
        SortedSet<T> newSet = new TreeSet<>(comp);
        newSet.addAll(list);
        // :NOTE:         this.list = List.copyOf(newSet);
        this.list = List.copyOf(newSet);
    }

    @Override
    public Comparator<? super T> comparator() {
        return comp;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (comp.compare(fromElement, toElement) > 0) {
            // :NOTE: message
            throw new IllegalArgumentException("Invalid arguments for subset. Must be FromElement > ToElement");
        }
        if (isEmpty()) {
            return new ArraySet<>(comp);
        }
        return checkedSubSet(fromElement, toElement, false);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        if (isEmpty()) {
            // :NOTE: потерян компаратор
            return new ArraySet<>(comp);
        }
        return checkedSubSet(first(), toElement, false);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        if (isEmpty()) {
            return new ArraySet<>(comp);
        }
        return checkedSubSet(fromElement, last(), true);
    }

    private SortedSet<T> checkedSubSet(T fromElement, T toElement, boolean toInclude) {
        // :NOTE: redundant check

        int fromIndex = Collections.binarySearch(list, fromElement, comp);
        int toIndex = Collections.binarySearch(list, toElement, comp);
        if (fromIndex < 0) fromIndex = -(fromIndex + 1);
        if (toIndex < 0) toIndex = -(toIndex + 1);
        if (toInclude) toIndex += 1;
        return new ArraySet<>(list.subList(fromIndex, toIndex), comp);
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return list.get(0);
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return list.get(size() - 1);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(list, (T) o, comp) >= 0;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}
