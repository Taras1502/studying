package algorithms.josephus;

/**
 * Created by Taras.Mykulyn on 07.10.2015.
 */
public class Josephus {
    public static String josephus(int size, int index) {
        StringBuilder order = new StringBuilder("");
        int current = -1;
        while(order.length() < size) {
            current = (current + index) % size;
            System.out.println(current);
            if (!order.toString().contains(String.valueOf(current))) {
                order.append(current);
            }
        }
        return order.toString();
    }

    public static void main(String[] args) {
        System.out.println(josephus(7, 2));
    }
}