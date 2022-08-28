package info.kgeorgiy.ja.shaburov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Implementor implements Impler {

    private void checkClass(Class<?> token) throws ImplerException {
        if (!token.isInterface() || Modifier.isPrivate(token.getModifiers()))
            throw new ImplerException("Not an interface or private");
    }

    private Path createDirectory(Path path, Class<?> token) throws ImplerException {
        try {
            path = path.resolve(token.getPackage().getName()
                    .replace('.', '\\'))
                    .resolve(token.getSimpleName() + "Impl.java");
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException e) {
            throw new ImplerException("Cannot create directory" + path + e);
        }
        return path;
    }

    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        checkClass(token);
        root = createDirectory(root, token);
        try (Writer writer = Files.newBufferedWriter(root)) {
            writer.write(classHeader(token));
            for (Method method : token.getMethods()) {
                if (Modifier.isAbstract(method.getModifiers())) {
                    writer.write(classMethod(method));
                }
            }
            writer.write("}");
        } catch (IOException e) {
            throw new ImplerException("Cannot write in java file");
        }
    }

    private String classMethod(Method method) {
        StringBuilder textMethod = new StringBuilder();
        return textMethod
                .append(System.lineSeparator())
                .append(getModifiers(method))
                .append(" ")
                .append(method.getReturnType().getCanonicalName())
                .append(" ")
                .append(method.getName())
                .append("(")
                .append(getParameters(method))
                .append(") ")
                .append(getThrows(method))
                .append("{")
                .append(getReturn(method))
                .append(System.lineSeparator())
                .append("\t}")
                .append(System.lineSeparator()).toString();
    }

    private String getReturn(Method method) {
        StringBuilder textReturn = new StringBuilder();
        if (method.getReturnType().equals(void.class)) {
            return textReturn.toString();
        }
        textReturn.append(System.lineSeparator());
        textReturn.append("\t\treturn ");
        if (!method.getReturnType().isPrimitive()) {
            textReturn.append("null");
        }
        else if (method.getReturnType().equals(boolean.class)) {
            textReturn.append("false");
        }
        else {
            textReturn.append("0");
        }
        return textReturn.append(";").toString();
    }

    private String getThrows(Method method) {
        StringBuilder textException = new StringBuilder();
        Class<?>[] exceptions = method.getExceptionTypes();
        if (exceptions.length != 0) {
            textException.append("throws ");
            textException.append(arrayStream(method.getExceptionTypes(), Class::getCanonicalName));
        }
        return textException.toString();
    }

    private String getParameters(Method method) {
        return arrayStream(method.getParameters(), parameter -> parameter.getType().getCanonicalName() + " " + parameter.getName());
    }

    private <T> String arrayStream(T[] arr, Function<T, String> func) {
        return Arrays.stream(arr).map(func).collect(Collectors.joining(", "));
    }

    private String getModifiers(Method method) {
        int modifier = method.getModifiers();
        StringBuilder modifiers = new StringBuilder();
        modifiers.append('\t');
        modifiers.append(Modifier.toString(modifier & ~Modifier.ABSTRACT & ~Modifier.TRANSIENT));
        return modifiers.toString();
    }

    private String classHeader(Class<?> token) {
        StringBuilder lines = new StringBuilder();
        lines.append("package ")
                .append(token.getPackageName())
                .append(";")
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("public class ")
                .append(token.getSimpleName())
                .append("Impl implements ")
                .append(token.getCanonicalName())
                .append(" {")
                .append(System.lineSeparator());
        return lines.toString();
    }
}
