package searchEngine.core.segments.memorySegment;

import searchEngine.core.dictionary.Dictionary;

import java.util.Random;

/**
 * Created by macbookpro on 5/2/16.
 */
public class MemorySegmentTest {
    private static final Random rand = new Random();
    private static final int MAX_SIZE = 10240000;

    public static void main(String[] args) throws InterruptedException {
        MemorySegment memorySegment = MemorySegment.load(1, "/Users/macbookpro/Desktop/test/segment.mem");
        new TokenGenerator(memorySegment).start();
        Thread.sleep(200);
        new TokenRetriever(memorySegment).start();
        Thread.sleep(4000);

//        Dictionary dictionary = Dictionary.create("/Users/macbookpro/Desktop/test/dictionary");
//        memorySegment.writeToDisc(dictionary);
//        dictionary.commit();
    }

    static class TokenGenerator extends Thread {
        private MemorySegment memorySegment;

        public TokenGenerator(MemorySegment memorySegment) {
            this.memorySegment = memorySegment;
        }

        @Override
        public void run() {
            for (int i = 0; i < 300000; i++) {
                int val = rand.nextInt(100) + 0;
                memorySegment.addPostList(String.valueOf(val), 0, i);
                if (i % 10000 == 0) {
                    memorySegment.commit();
                }
            }
        }
    }

    static class TokenRetriever extends Thread {
        private MemorySegment memorySegment;

        public TokenRetriever(MemorySegment memorySegment) {
            this.memorySegment = memorySegment;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                int val = rand.nextInt(100) + 0;
                System.out.println("getting" + val + " ");
                memorySegment.getPostList(String.valueOf(val));
            }
        }
    }
}
