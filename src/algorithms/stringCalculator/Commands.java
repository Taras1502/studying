package algorithms.stringCalculator;

/**
 * Created by Taras.Mykulyn on 06.10.2015.
 */
public enum Commands {
    PLUS("+") {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },

    MINUS("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },

    DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            return x / y;
        }
    },

    MULTIPLY("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    };

    private String strCommand;

    Commands(String strCommand) {
        this.strCommand = strCommand;
    }

    public abstract double apply(double x, double y);
}
