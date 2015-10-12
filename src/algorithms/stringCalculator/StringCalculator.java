package algorithms.stringCalculator;

import algorithms.dataStructures.stack.CustomArrayStack;

/**
 * Created by Taras.Mykulyn on 05.10.2015.
 */
public class StringCalculator {
    private CustomArrayStack<Commands> commands;
    private CustomArrayStack<String> params;

    public StringCalculator() {
        commands = new CustomArrayStack<>();
        params = new CustomArrayStack<>();
    }

    public void calculate(String strToCalc) {
        readStr(strToCalc);
    }

    private void readStr(String strToCalc) {
        char[] array = new char[strToCalc.length()];
        strToCalc.getChars(0, strToCalc.length(), array, 0);
        int pos = 0;
        String part;
        while(pos < array.length) {
            part = "";
            while(array[pos] > 47 &&
                    array[pos] < 58 ||
                    array[pos] == 46) {
                part += array[pos++];
            }

            if (part.length() != 0) {
                params.push(part);
            } else {
                part += array[pos++];
                switch (part) {
                    case "+":
                        commands.push(Commands.PLUS);
                        break;
                    case "-":
                        commands.push(Commands.MINUS);
                        break;
                    case "*":
                        commands.push(Commands.MULTIPLY);
                        break;
                    case "/":
                        commands.push(Commands.DIVIDE);
                        break;
                    case ")":
                        Commands com = commands.pop();
                        double x = Double.valueOf(params.pop());
                        double y = Double.valueOf(params.pop());
                        double res = com.apply(y, x);
                        params.push(String.valueOf(res));
                        break;
                    default:
                        break;
                }
            }
        }

        System.out.println(params.pop());
    }

    public static void main(String[] args) {
        String str = "((3.5 * 2) / 3.2)";
        StringCalculator stringCalculator = new StringCalculator();
        stringCalculator.calculate(str);
    }
}
