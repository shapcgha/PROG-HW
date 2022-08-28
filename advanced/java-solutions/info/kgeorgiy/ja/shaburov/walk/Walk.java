package info.kgeorgiy.ja.shaburov.walk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Walk {

    public static final byte[] ERROR_HASH = new byte[20];

    private static String hex(final byte[] sha1) {
        final StringBuilder sb = new StringBuilder(sha1.length * 2);
        for (final byte b : sha1) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void walk(final Path in, final Path out) throws NoSuchAlgorithmException {
        final MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        checkOutputPath(out);
        // :NOTE: Старое
        try (final BufferedReader reader = Files.newBufferedReader(in)) {
            try (final Writer writer = Files.newBufferedWriter(out)) {
                try {
                    String newFile = reader.readLine();
                    while (newFile != null) {
                        try (final InputStream file = Files.newInputStream(Path.of(newFile))) {
                            sha1.reset();
                            final byte[] buffer = new byte[1 << 13];
                            int length;
                            while ((length = file.read(buffer)) > 0) {
                                sha1.update(buffer, 0, length);
                            }
                            // :NOTE: Дублирование
                            writer.write(String.format("%s %s%n", hex(sha1.digest()), newFile));
                        } catch (final IOException  | InvalidPathException e) {
                            writer.write(String.format("%s %s%n", hex(ERROR_HASH), newFile));
                        }
                        newFile = reader.readLine();
                    }
                } catch (final IOException e) {
                    System.err.println("Can't read in input file " + e.getMessage());
                }
            } catch (final FileNotFoundException e) {
                System.err.println("Can't find output file: " + e.getMessage());
            } catch (final IOException e) {
                System.err.println("Can't open output file: " + e.getMessage());
            }
        } catch (final FileNotFoundException e) {
            System.err.println("Can't find input file: " + e.getMessage());
        } catch (final IOException e) {
            System.err.println("Can't open input file: " + e.getMessage());
        }
    }

    private static void checkOutputPath(final Path out) {
        if (out.getParent() != null) {
            try {
                // :NOTE: Дублирование
                Files.createDirectories(out.getParent());
            } catch (final IOException e) {
                System.err.println("Can't create directory path " + e.getMessage());
            }
        }
    }

    public static void main(final String[] args) throws NoSuchAlgorithmException {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Invalid arguments");
        } else {
            try {
                walk(Path.of(args[0]), Path.of(args[1]));
            } catch (InvalidPathException e) {
                System.err.println("Invalid path to in or out file:" + e.getMessage());
            }
        }
    }
}
