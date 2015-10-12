package algorithms.dataStructures.test;


import algorithms.dataStructures.linkedList.CustomLinkedList;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Taras.Mykulyn on 07.10.2015.
 */
public class CustomLinkedListTest {
    @Test
    public void testAddFirst() {
        CustomLinkedList<String> linkedList = new CustomLinkedList<>();
        Assert.assertTrue(linkedList.size() == 0);

        String one = "1";
        linkedList.addFirst(one);
        Assert.assertTrue(linkedList.getFirst().equals(one));
        Assert.assertTrue(linkedList.get(0).equals(one));
        Assert.assertTrue(linkedList.size() == 1);

        String two = "2";
        linkedList.addFirst(two);
        Assert.assertTrue(linkedList.getFirst().equals(two));
        Assert.assertTrue(linkedList.get(0).equals(two));
        Assert.assertTrue(linkedList.size() == 2);
    }

    @Test
    public void testAddLast() {
        CustomLinkedList<String> linkedList = new CustomLinkedList<>();
        Assert.assertTrue(linkedList.size() == 0);

        String one = "1";
        linkedList.addLast(one);
        Assert.assertTrue(linkedList.getLast().equals(one));
        Assert.assertTrue(linkedList.get(0).equals(one));
        Assert.assertTrue(linkedList.size() == 1);

        String two = "2";
        linkedList.addLast(two);
        Assert.assertTrue(linkedList.getLast().equals(two));
        Assert.assertTrue(linkedList.get(1).equals(two));
        Assert.assertTrue(linkedList.size() == 2);
    }

    @Test
    public void testAddFirstLast() {
        CustomLinkedList<String> linkedList = new CustomLinkedList<>();
        Assert.assertTrue(linkedList.size() == 0);

        String one = "1";
        linkedList.addFirst(one);
        Assert.assertTrue(linkedList.getFirst().equals(one));
        Assert.assertTrue(linkedList.getLast().equals(one));
        Assert.assertTrue(linkedList.get(0).equals(one));
        Assert.assertTrue(linkedList.size() == 1);

        String two = "2";
        linkedList.addLast(two);
        Assert.assertTrue(linkedList.getFirst().equals(one));
        Assert.assertTrue(linkedList.getLast().equals(two));
        Assert.assertTrue(linkedList.get(1).equals(two));
        Assert.assertTrue(linkedList.size() == 2);

        String three = "3";
        linkedList.addFirst(three);
        Assert.assertTrue(linkedList.getFirst().equals(three));
        Assert.assertTrue(linkedList.getLast().equals(two));
        Assert.assertTrue(linkedList.get(0).equals(three));
        Assert.assertTrue(linkedList.size() == 3);
    }
}
