package searchEngine.core.index;

import searchEngine.core.segments.memorySegment.MemorySegment;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Taras.Mykulyn on 05.05.2016.
 */
public class FutureMemorySegment implements Future<MemorySegment> {
    private static final int ONE_CYCLE_SLEEP = 500;
    private static final int TIMEOUT_FOREVER = -1;
    private Index index;
    private String filePath;
    private MemorySegment instance;
    private volatile long timeout = TIMEOUT_FOREVER;
    private volatile boolean interrupted;
    private volatile boolean running;
    private volatile boolean cancelled;

    public FutureMemorySegment(MemorySegment instance) {
        this.instance = instance;
    }

    public FutureMemorySegment(Index index, String filePath) {
        this.index = index;
        this.filePath = filePath;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this) {
            interrupted = mayInterruptIfRunning || !running;
            cancelled = interrupted;
            return cancelled;
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public synchronized boolean isDone() {
        return instance != null;
    }

    @Override
    public MemorySegment get() throws InterruptedException, ExecutionException {
        running = true;
        while ((instance = index.getMemSegment(filePath)) == null && !interrupted) {
            if (timeout != TIMEOUT_FOREVER && timeout <= 0) {
                break;
            }
            Thread.sleep(ONE_CYCLE_SLEEP);
        }
        synchronized (this) {
            if (interrupted) {
                interrupted = false;
            }
            running = false;
        }
        return instance;
    }

    @Override
    public MemorySegment get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (this) {
            timeout = unit.convert(timeout, TimeUnit.SECONDS);
        }
        return get();
    }
}
