package algorithms.parenthesesCheck;

import algorithms.dataStructures.stack.CustomArrayStack;
import algorithms.dataStructures.stack.Stack;

/**
 * Created by Taras.Mykulyn on 07.10.2015.
 */
public class ParenthesesChecker {
    public static final String OPENING_PARENTHESES = "({[";
    public static final String CLOSING_PARENTHESES = ")}]";

    public static boolean allParenthesesClosed(String str) {
        Stack<Character> stack = new CustomArrayStack<>();
        char[] array = str.toCharArray();
        for (char parenthesis: array) {
            if (OPENING_PARENTHESES.contains(String.valueOf(parenthesis))) {
                stack.push(parenthesis);
            } else if (CLOSING_PARENTHESES.contains(String.valueOf(parenthesis))) {
                char lastOpened = stack.pop();
                if (CLOSING_PARENTHESES.indexOf(parenthesis) !=
                        OPENING_PARENTHESES.indexOf(lastOpened)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String p = "[()]{}{[()()]()}";

        System.out.println(allParenthesesClosed(p));
    }
}
