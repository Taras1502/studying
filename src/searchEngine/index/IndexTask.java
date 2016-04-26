package searchEngine.index;

import searchEngine.interfaces.MemorySegment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public class IndexTask implements Runnable {
    private static final String TOKEN_SPLITTERS = "[ .,:;\"\'{}()-+<>]+?";
    private MemorySegment memorySegment;
    private String filePath;
    private int docId;

    public IndexTask(MemorySegment memorySegment, String filePath, int docId) {
        this.memorySegment = memorySegment;
        this.filePath = filePath;
        this.docId = docId;
    }

    @Override
    public void run() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            String l;
            String[] arr;
            int pos = 0;

            while((l = br.readLine()) != null) {
                arr = l.split(TOKEN_SPLITTERS);
                for (String token: arr) {
                    token = token.toLowerCase();
                    if (!TokenFilter.needToIndex(token)) continue;
                    // TODO: implement token filter and steamer
                    memorySegment.addPostList(token, docId, pos++);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
