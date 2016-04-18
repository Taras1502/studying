package java8;

import algorithms.stringCalculator.StringCalculator;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by macbookpro on 10/30/15.
 */
public class LambdaBasics<T> {
    /*
    Using the listFiles(FileFilter) and isDirectory methods of the java.io.File class,
    write a method that returns all subdirectories of a given directory.
    Use a lambda expression instead of a FileFilter object. Repeat with a method expression.
     */
    public static File[] subDirs(String dir) {
        if (dir != null) {
            File dirFile = new File(dir);
            return dirFile.listFiles(File::isDirectory); // return file.listFiles((f) -> f.isDirectory());
        } else {
            return null;
        }
    }

    /*
    Using the list(FilenameFilter) method of the java.io.File class, write a method that returns all files
    in a given directory with a given extension. Use a lambda expression, not a FilenameFilter.
    Which variables from the enclosing scope does it capture?
     */
    public static File[] files(String dir, String ext) {
        if (dir != null && ext != null) {
            File dirFile = new File(dir);
            return dirFile.listFiles((directory, name) -> name.endsWith(ext));
        } else {
            return null;
        }

    }

    /*
    Given an array of File objects, sort it so that the directories come before the files, and within each group,
    elements are sorted by path name. Use a lambda expression, not a Comparator.
     */
    public static void sort(File[] files) {
        if (files == null || files.length < 2) return;
        Arrays.sort(files, (first, second) -> {
            if (first.isDirectory() && second.isDirectory() ||
                    first.isFile() && second.isFile()) {
                return first.getPath().compareTo(second.getPath());
            } else {
                if (first.isDirectory()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    /*
    Form a subclass Collection2 from Collection and add a default method void forEachIf(Consumer<T> action,
    Predicate<T> filter) that applies action to each element for which filter returns true. How could you use it?
     */
    public void forEachIf(Collection<T> collection, Consumer<T> action, Predicate<T> filter) {
        collection.forEach((elem) -> {
            if (filter.test(elem)) {
                action.accept(elem);
            }
        });
    }


    public static void main(String[] args) {
        System.out.println(Arrays.toString(subDirs("/Applications")));
        System.out.println(Arrays.toString(files("/Applications", "app")));
        File rootDir = new File("/Users/macbookpro/Downloads");
        File[] list = rootDir.listFiles();
        sort(list);
        System.out.println(Arrays.toString(list));

        Collection<String> collection = new ArrayList<>();
        collection.add("Taras");
        collection.add("Oleg");
        new LambdaBasics<String>().forEachIf(collection,
                System.out::println,
                (x) -> x.startsWith("Tar"));

        Stream<Long> stream = Stream.generate(System::currentTimeMillis).
                limit(10).
                distinct().
                sorted(Comparator.comparing(String::valueOf, (first, second) ->
                        first.compareTo(second)).
                        reversed()).
                peek(System.out::println);
        stream.filter((Long elem) -> elem > 0);
        Optional<Long> optional = stream.max(Comparator.comparing(String::valueOf, (x, y) -> x.compareTo(y)));


    }
}
