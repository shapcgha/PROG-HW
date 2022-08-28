package info.kgeorgiy.ja.shaburov.mapper;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {

    private final List<Thread> threads;
    private final Queue<Runnable> tasks;

    /**
     * Class constructor of number of threads
     * @param numberOfThreads number of threads
     */
    public ParallelMapperImpl(int numberOfThreads) {
        threads = new ArrayList<>();
        tasks = new ArrayDeque<>();

        for (int i = 0; i < numberOfThreads; i++) {
            threads.add(new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        run();
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    Thread.currentThread().interrupt();
                }
            }));
            threads.get(i).start();
        }
    }

    private void run() throws InterruptedException {
        Runnable runnable;
        synchronized (tasks) {
            while (tasks.isEmpty()) {
                tasks.wait();
            }
            runnable = tasks.poll();
            tasks.notify();
        }
        runnable.run();
    }

    /**
     * Maps func over list of values
     * @param f function to map
     * @param args list of values
     * @param <T> value type
     * @param <R> return type
     * @return mapped function
     * @throws InterruptedException if on of threads is interrupted
     */
    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        Future<R> answer = new Future<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            final int index = i;
            synchronized (tasks) {
                tasks.add(() -> answer.set(index, f.apply(args.get(index))));
                tasks.notifyAll();
            }
        }
        return answer.get();
    }

    /**
     * stops all threads
     */
    @Override
    public void close() {
        threads.forEach(Thread::interrupt);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        });
    }

    private static class Future<R> {
        private final List<R> list;
        private int count = 0;

        public Future(int size) {
            list = new ArrayList<>(Collections.nCopies(size, null));
        }

        public void set(int index, R arg) {
            list.set(index, arg);
            synchronized (this) {
                count++;
                if (count == list.size()) {
                    notify();
                }
            }
        }

        public synchronized List<R> get() throws InterruptedException {
            while (count != list.size()) {
                wait();
            }
            return list;
        }
    }
}
