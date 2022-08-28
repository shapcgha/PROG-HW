package info.kgeorgiy.ja.shaburov.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IterativeParallelism implements ScalarIP {

    private final ParallelMapper mapper;

    /**
     * Default class constructor
     */
    public IterativeParallelism() {
        mapper = null;
    }

    /**
     * Class constructor with {@link ParallelMapper mapper}
     *
     * @param mapper {@link ParallelMapper} to construct from
     */
    public IterativeParallelism(final ParallelMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Return max of list
     *
     * @param threads    number or concurrent threads.
     * @param values     values to get max of.
     * @param comparator value comparator.
     * @param <T>        value Type
     * @return max of values
     * @throws InterruptedException if one thread was interrupted
     */
    @Override
    public <T> T maximum(final int threads, final List<? extends T> values, final Comparator<? super T> comparator) throws InterruptedException {
        return multiTread(threads, values, stream -> stream.max(comparator).orElseThrow())
                .stream().max(comparator).orElseThrow();
    }

    /**
     * Return min of list
     *
     * @param threads    number or concurrent threads.
     * @param values     values to get min of.
     * @param comparator value comparator.
     * @param <T>        value Type
     * @return min of values
     * @throws InterruptedException if one thread was interrupted
     */
    @Override
    public <T> T minimum(final int threads, final List<? extends T> values, final Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threads, values, comparator.reversed());
    }

    /**
     * Return if all values satisfied predicate
     *
     * @param threads   number or concurrent threads.
     * @param values    values to check.
     * @param predicate test predicate.
     * @param <T>       value type
     * @return if all values satisfied predicate
     * @throws InterruptedException if one thread was interrupted
     */
    @Override
    public <T> boolean all(final int threads, final List<? extends T> values, final Predicate<? super T> predicate) throws InterruptedException {
        return multiTread(threads, values, stream -> stream.allMatch(predicate))
                .stream().allMatch(Boolean::booleanValue);
    }

    /**
     * Return if any of values satisfied predicate
     *
     * @param threads   number or concurrent threads.
     * @param values    values to check.
     * @param predicate test predicate.
     * @param <T>       value type
     * @return if any of values satisfied predicate
     * @throws InterruptedException if one thread was interrupted
     */
    @Override
    public <T> boolean any(final int threads, final List<? extends T> values, final Predicate<? super T> predicate) throws InterruptedException {
        return !all(threads, values, predicate.negate());
    }

    /**
     * Splits execution of functions in numberOfTreads threads
     *
     * @param numberOfTreads number of concurrent threads
     * @param values         list of values to be executed
     * @param operation      function to execute on values
     * @param <T>            value type
     * @param <R>            return type
     * @return Returns list of results on each thread
     * @throws InterruptedException     if one thread was interrupted
     * @throws IllegalArgumentException if number of thread less then 1 or list of values is empty or null
     */
    private <T, R> List<R> multiTread(final int numberOfTreads, final List<T> values, final Function<Stream<? extends T>, R> operation) throws InterruptedException {
        if (numberOfTreads < 1) {
            throw new IllegalArgumentException("Number of Treads must be more than 0");
        }
        if (values == null) {
            // :NOTE: NPE
            throw new NullPointerException();
        }
        final List<Stream<T>> subLists = split(numberOfTreads, values);
        return mapper == null ? map(operation, subLists) : mapper.map(operation, subLists);
    }

    private <T, R> List<R> map(
            final Function<Stream<? extends T>, R> operation,
            final List<Stream<T>> subLists
    ) throws InterruptedException {
        final List<R> result = new ArrayList<>(Collections.nCopies(subLists.size(), null));
        final List<Thread> threads = IntStream.range(0, subLists.size())
                .mapToObj(i -> new Thread(() -> result.set(i, operation.apply(subLists.get(i)))))
                .peek(Thread::start).collect(Collectors.toList());
        InterruptedException ex = null;
        for (final Thread thread : threads) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                // :NOTE: Не дождались
                if (ex != null) {
                    e.addSuppressed(ex);
                }
                ex = e;
            }
        }
        if (ex != null) {
            throw ex;
        }
        return result;
    }

    private static <T> List<Stream<T>> split(int numberOfTreads, final List<T> values) {
        // :NOTE: Лишний поток
        if(values.isEmpty()) {
            return List.of();
        }
        numberOfTreads = Math.max(1, Math.min(numberOfTreads, values.size()));
        final int numberOfElements = values.size() / numberOfTreads;
        final int restElements = values.size() % numberOfTreads;
        final List<Stream<T>> subLists = new ArrayList<>();
        int r = 0;
        for (int i = 0; i < numberOfTreads; i++) {
            final int l = r;
            r = l + numberOfElements + (i < restElements ? 1 : 0);
            final int tempR = r;
            subLists.add(values.subList(l, tempR).stream());
        }
        return subLists;
    }
}
