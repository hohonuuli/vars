package vars.shared.util;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;


import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Watches the given class for some duration to see if it changes (created, modified, or deleted)
 * Created by brian on 3/4/14.
 */
public class FileChangeWatchDog<R> {
    private final long durationNanos;
    private final File file;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Thread thread;
    private final Function<File, R> fn;
    private final SettableFuture<R> future;
    private final WatchEvent.Kind<Path>[] kinds;


    public FileChangeWatchDog(File file, long durationSeconds, Function<File, R> fn, WatchEvent.Kind<Path>... kinds) {
        Preconditions.checkArgument(durationSeconds > 0L, "durationSeconds must be greater than 0");
        this.file = file;
        this.durationNanos = durationSeconds * 1000000000;
        this.fn = fn;
        this.future = SettableFuture.create();
        this.kinds = kinds;
        thread = new Thread(new WatchRunnable(), getClass().getSimpleName() + "-" + System.currentTimeMillis());
        thread.setDaemon(true);
    }

    public ListenableFuture<R> getFuture() {
        return future;
    }

    public void start() {
        thread.start();
    }

    private class WatchRunnable implements Runnable {

        long elapsed = 0;
        long start;

        public void run() {
            start = System.nanoTime();
            Path path = file.getParentFile().toPath();
            WatchService watchService = null;
            boolean didComplete = false;
            try{
                watchService = path.getFileSystem().newWatchService();
                WatchKey registrationKey = path.register(watchService, kinds);
                while (elapsed < durationNanos) {
                    WatchKey key = watchService.take();
                    for (WatchEvent event: key.pollEvents()) {
                        WatchEvent.Kind kind = event.kind();
                        if (kind == OVERFLOW) {
                            continue;
                        }

                        Path changedPath = (Path) event.context();

                        if (changedPath.toFile().getName().equals(file.getName())) {
                            future.set(fn.apply(changedPath.toFile()));
                            didComplete = true;
                            break;
                        }
                    }
                    elapsed = System.nanoTime() - start;
                    boolean isKeyStillValid = key.reset();
                    //  If the key is no longer valid, the directory is inaccessible so exit the loop.
                    if (!isKeyStillValid) {
                        break;
                    }
                }
            }
            catch (Exception e) {
               future.setException(e);
            }
            finally {
                if (watchService != null) {
                    try {
                        watchService.close();
                    }
                    catch (IOException e) {
                        log.warn("An Exception was thrown while closing a WatchService", e);
                    }
                }
            }

            if (!didComplete) {
                future.setException(new RuntimeException("Failed to execute function via FileChangeWatchDog"));
            }
        }
    }


}
