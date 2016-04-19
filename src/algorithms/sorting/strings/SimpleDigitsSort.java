package algorithms.sorting.strings;

import java.util.Arrays;

/**
 * Created by macbookpro on 11/23/15.
 */
public class SimpleDigitsSort {
    public static void sort(Student[] array, int maxVal) {
        int[] counter = new int[maxVal + 2];

        int index;
        for (Student student: array) {
            index = student.mark + 1;
            counter[index]++;
        }

        for (int i = 1; i < counter.length; i++) {
            counter[i] += counter[i-1];
        }

        Student[] aux = new Student[array.length];
        for (Student student: array) {
            index = counter[student.mark]++;
            aux[index] = student;
        }

        for (Student st: aux) {
            System.out.println(st.name + " " + st.mark);
        }
    }

    static class Student {
        String name;
        int mark;
        Student (String name, int mark) {
            this.name = name;
            this.mark = mark;
        }
    }

    public static void main(String[] args) {
        Student[] array = new Student[10];
        array[0] = new Student("a", 2);
        array[1] = new Student("b", 0);
        array[2] = new Student("c", 4);
        array[3] = new Student("d", 3);
        array[4] = new Student("e", 2);
        array[5] = new Student("f", 1);
        array[6] = new Student("g", 2);
        array[7] = new Student("h", 4);
        array[8] = new Student("i", 1);
        array[9] = new Student("k", 5);

        sort(array, 5);


    }
}
