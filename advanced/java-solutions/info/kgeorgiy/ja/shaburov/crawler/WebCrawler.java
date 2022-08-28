package info.kgeorgiy.ja.shaburov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements Crawler {
    private final Downloader downloader;
    private final ExecutorService downloaderService;
    private final ExecutorService extractorService;

    /**
     * Main method tha starts download links
     * Usage: [url [depth [downloads [threads [perHost]]]]]
     *
     * @param args array of string args
     */
    public static void main(final String[] args) {
        if (args == null || args.length < 5) {
            System.err.println("Incorrect input");
            return;
        }
        final String url;
        final int depth;
        final int downloads;
        final int extractors;
        final int perHost;
        try {
            url = args[0];
            depth = Integer.parseInt(args[1]);
            downloads = Integer.parseInt(args[2]);
            extractors = Integer.parseInt(args[3]);
            perHost = Integer.parseInt(args[4]);
        } catch (final NumberFormatException e) {
            System.err.println("input numbers");
            return;
        }
        try {
            new WebCrawler(new CachingDownloader(), downloads, extractors, perHost).download(url, depth);
        } catch (final IOException e) {
            System.err.println("Error int downloader " + e.getMessage());
        }
    }

    /**
     * Download all links from {@link String url} and down to {@link Integer depth}
     *
     * @param url   start link.
     * @param depth download depth.
     * @return {@link Result}, where we see List of successfully visited links
     * and map of error Links with error text
     */
    @Override
    public Result download(final String url, final int depth) {
        final Map<String, IOException> errorLinks = new ConcurrentHashMap<>();
        final Queue<String> queue = new ConcurrentLinkedQueue<>();
        final Set<String> visitedLinks = new ConcurrentSkipListSet<>();
        final Set<String> links = new ConcurrentSkipListSet<>();
        final Queue<String> nextLinks = new ConcurrentLinkedQueue<>();
        visitedLinks.add(url);
        queue.add(url);
        final Phaser phaser = new Phaser(1);
        for (int i = 0; i < depth; i++) {
            // :NOTE: Эффективность
            nextLinks.stream().filter(visitedLinks::add).forEach(queue::add);
            nextLinks.clear();
            queue.forEach(nowUrl -> {
                phaser.register();
                downloaderService.submit(() -> {
                    try {
                        final Document doc = downloader.download(nowUrl);
                        links.add(nowUrl);
                        phaser.register();
                        extractorService.submit(() -> {
                            try {
                                nextLinks.addAll(doc.extractLinks());
                            } catch (final IOException ignored) {
                            } finally {
                                phaser.arriveAndDeregister();
                            }
                        });
                    } catch (final IOException e) {
                        errorLinks.put(nowUrl, e);
                    } finally {
                        phaser.arriveAndDeregister();
                    }
                });
            });
            queue.clear();
            phaser.arriveAndAwaitAdvance();
        }
        phaser.arrive();
        return new Result(new ArrayList<>(links), errorLinks);
    }

    /**
     * Close {@link ExecutorService extractorService} and {@link ExecutorService downloaderService}
     */
    @Override
    public void close() {
        // :NOTE: Не дождались
        downloaderService.shutdown();
        extractorService.shutdown();
    }

    // :NOTE: В конце
    /**
     * Class constructor which is created of {@link Downloader downloader}, {@link ExecutorService downloaderService},
     * {@link ExecutorService extractorService} and {@link Integer perHost}
     *
     * @param downloader {@link Downloader} download pages and links from them
     * @param downloads  max number of simultaneously load pages
     * @param extractors max number that can be checked for links at the same time
     * @param perHost    max number of pages from one host, that can be loaded simultaneously
     */
    @SuppressWarnings("unused")
    public WebCrawler(final Downloader downloader, final int downloads, final int extractors, final int perHost) {
        this.downloader = downloader;
        this.downloaderService = Executors.newFixedThreadPool(downloads);
        this.extractorService = Executors.newFixedThreadPool(extractors);
    }
}
