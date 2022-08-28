package info.kgeorgiy.ja.shaburov.mapper;

import info.kgeorgiy.java.advanced.concurrent.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class IterativeParallelism implements ScalarIP {

    private final ParallelMapper mapper;

    /**
     *  Default class constructor
     */
    public IterativeParallelism() {
        mapper = null;
    }

    /**
     * Class constructor with {@link ParallelMapper mapper}
     * @param mapper {@link ParallelMapper} to construct from
     */
    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Return max of list
     * @param threads number or concurrent threads.
     * @param values values to get max of.
     * @param comparator value comparator.
     * @param <T> value Type
     * @return max of values
     * @throws InterruptedException if one thread was interrupted
     */
    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return multyTread(threads, values, stream -> stream.stream().max(comparator).orElseThrow())
                .stream().max(comparator).orElseThrow();
    }

    /**
     * Return min of list
     * @param threads number or concurrent threads.
     * @param values values to get min of.
     * @param comparator value comparator.
     * @param <T> value Type
     * @return min of values
     * @throws InterruptedException if one thread was interrupted
     */
    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threads, values, comparator.reversed());
    }

    /**
     * Return if all values satisfied predicate
     * @param threads number or concurrent threads.
     * @param values values to check.
     * @param predicate test predicate.
     * @param <T> value type
     * @return if all values satisfied predicate
     * @throws InterruptedException if one thread was interrupted
     */
    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return multyTread(threads, values, stream -> stream.stream().allMatch(predicate))
                .stream().allMatch(item -> item);
    }

    /**
     * Return if any of values satisfied predicate
     * @param threads number or concurrent threads.
     * @param values values to check.
     * @param predicate test predicate.
     * @param <T> value type
     * @return if any of values satisfied predicate
     * @throws InterruptedException if one thread was interrupted
     */
    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return !all(threads, values, predicate.negate());
    }

    /**
     * Splits execution of functions in numberOfTreads threads
     * @param numberOfTreads number of concurrent threads
     * @param values list of values to be executed
     * @param operation function to execute on values
     * @param <T> value type
     * @param <R> return type
     * @return Returns list of results on each thread
     * @throws InterruptedException if one thread was interrupted
     * @throws IllegalArgumentException if number of thread less then 1 or list of values is empty or null
     */
    private <T, R> List<R> multyTread(int numberOfTreads, List<T> values, Function<List<? extends T>, R> operation) throws InterruptedException {
        if (numberOfTreads < 1) {
            throw new IllegalArgumentException("Number of Treads must be more than 0");
        }
        if (values == null) {
            throw new IllegalArgumentException("list of values must be not null");
        }
        numberOfTreads = Math.max(1, Math.min(numberOfTreads, values.size()));
        int numberOfElements = values.size() / numberOfTreads;
        int restElements = values.size() % numberOfTreads;
        List<List<T>> sublists = new ArrayList<>();
        int r = 0;
        for (int i = 0; i < numberOfTreads; i++) {
            final int l = r;
            r = l + numberOfElements + (i < restElements ? 1 : 0);
            final int tempR = r;
            sublists.add(values.subList(l, tempR));
        }
        List<R> result = new ArrayList<>(Collections.nCopies(numberOfTreads, null));
        if (mapper == null) {
            List<Thread> threads = new ArrayList<>();
            for (int i = 0; i < numberOfTreads; i++) {
                final int index = i;
                Thread thread = new Thread(() -> result.set(
                        index, operation.apply(sublists.get(index))));
                thread.start();
                threads.add(thread);
            }
            for (Thread thread : threads) {
                thread.join();
            }
            return result;
        } else {
            return mapper.map(operation, sublists);
        }
    }
}
