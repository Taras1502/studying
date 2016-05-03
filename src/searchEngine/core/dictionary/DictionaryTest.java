package searchEngine.core.dictionary;

import java.util.Random;

/**
 * Created by macbookpro on 4/30/16.
 */
public class DictionaryTest {
    private static final Random rand = new Random();


    public static void main(String[] args) throws InterruptedException {
        Dictionary dictionary = Dictionary.load("/Users/macbookpro/Desktop/test/dictionary");
//        new TokenGenerator(dictionary).start();
//        Thread.sleep(200);
        new TokenRetriever(dictionary).start();
    }

    static class TokenGenerator extends Thread {
        private Dictionary dictionary;

        public TokenGenerator(Dictionary dictionary) {
            this.dictionary = dictionary;
        }

        @Override
        public void run() {
            for (int i = 0; i < 300000; i++) {
                int val = rand.nextInt(100) + 0;
//                System.out.println("adding" + val);
//                dictionary.addToken(String.valueOf(val), 0, 0);
//                if (i % 1000 == 0) {
//                    dictionary.commit();
//                }
            }
        }
    }

    static class TokenRetriever extends Thread {
        private Dictionary dictionary;

        public TokenRetriever(Dictionary dictionary) {
            this.dictionary = dictionary;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                int val = rand.nextInt(100) + 0;
                System.out.println("getting" + val);
                System.out.println(dictionary.getTokenData(String.valueOf(val)));
            }
        }
    }
}
