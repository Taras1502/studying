package searchEngine.core;

/**
 * Created by Taras.Mykulyn on 29.04.2016.
 */
public class Logger {
    public static synchronized void info(Class c, String log) {
        System.out.println("[INFO] " + c.getName() + ": " + log);
    }

    public static synchronized void warn(Class c, String log) {
        System.out.println("[WARN] " + c.getName() + ": " + log);
    }

    public static synchronized void error(Class c, String log) {
        System.out.println("[ERROR] " + c.getName() + ": " + log);
    }
}
