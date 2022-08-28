package info.kgeorgiy.ja.shaburov.concurrent;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ParallelMapperImpl implements ParallelMapper {

    private final List<Thread> threads;
    private final Queue<Runnable> tasks = new ArrayDeque<>();

    /**
     * Class constructor of number of threads
     *
     * @param numberOfThreads number of threads
     */
    public ParallelMapperImpl(final int numberOfThreads) {
        threads = IntStream.range(0, numberOfThreads)
                .mapToObj(i -> new Thread(() -> {
                    try {
                        while (!Thread.interrupted()) {
                            pollTask().run();
                        }
                    } catch (final InterruptedException ignored) {
                    } finally {
                        Thread.currentThread().interrupt();
                    }
                }))
                .peek(Thread::start)
                .toList();
    }

    private Runnable pollTask() throws InterruptedException {
        synchronized (tasks) {
            while (tasks.isEmpty()) {
                tasks.wait();
            }
            return tasks.poll();
        }
    }

    /**
     * Maps func over list of values
     *
     * @param f    function to map
     * @param args list of values
     * @param <T>  value type
     * @param <R>  return type
     * @return mapped function
     * @throws InterruptedException if on of threads is interrupted
     */
    @Override
    public <T, R> List<R> map(final Function<? super T, ? extends R> f, final List<? extends T> args) throws InterruptedException {
        final Future<R> answer = new Future<>(args.size());
        // :NOTE: f.apply
        IntStream.range(0, args.size()).forEach(i -> addTask(() -> {
            try {
                answer.set(i, f.apply(args.get(i)));
            } catch (final Exception e) {
                // :NOTE: System.err.println(e.getMessage());
            }
        }));
        return answer.get();
    }

    private void addTask(final Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
    }

    /**
     * stops all threads
     */
    @Override
    public void close() {
        threads.forEach(Thread::interrupt);
        InterruptedException ex = null;
        for (final Thread thread : threads) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                if (ex != null) {
                    e.addSuppressed(ex);
                }
                ex = e;
            }
        }
    }

    private static class Future<R> {
        private final List<R> list;
        private int count;

        public Future(final int size) {
            list = new ArrayList<>(Collections.nCopies(size, null));
            count = size;
        }

        public synchronized void set(final int index, final R arg) {
            list.set(index, arg);
            if (--count == 0) {
                notify();
            }
        }

        public synchronized List<R> get() throws InterruptedException {
            // :NOTE: Бесконечное ожидание
            while (count != 0) {
                wait();
            }
            return list;
        }
    }
}
