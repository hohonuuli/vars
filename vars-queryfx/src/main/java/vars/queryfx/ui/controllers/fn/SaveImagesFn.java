package vars.queryfx.ui.controllers.fn;

import javafx.scene.control.Label;
import org.mbari.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.queryfx.ui.db.results.QueryResults;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2015-08-11T14:30:00
 */
public class SaveImagesFn {
    private final File targetDir;
    private final QueryResults queryResults;
    private volatile boolean ok = true;
    private Label fileLabel;
    private Consumer<Double> progressFn; // Used to updated a progress indicator
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Executor executor;

    public SaveImagesFn(Executor executor, File targetDir, QueryResults queryResults, Consumer<Double> progressFn) {
        this.executor = executor;
        this.queryResults = queryResults;
        this.targetDir = targetDir;
        this.progressFn = progressFn;
    }

    public void apply() {
        executor.execute(() -> {
            Tuple2<List<String>, List<String[]>> rowData = queryResults.toRowOrientedData();
            List<String[]> rows = rowData.getB();
            double n = rows.size();
            for (int i = 0; i < rows.size(); i++) {
                progressFn.accept(i / n);
                if (!ok) {
                    break;
                }
                List<String> imageRefs = Arrays.stream(rows.get(i))
                        .filter(s -> {
                            String u = s.toUpperCase();
                            return (u.startsWith("HTTP") ||
                                    u.startsWith("FILE")) &&
                                    (u.endsWith(".PNG") ||
                                            u.endsWith(".JPG") ||
                                            u.endsWith(".JPEG") ||
                                            u.endsWith(".TIF") ||
                                            u.endsWith(".TIFF") ||
                                            u.endsWith(".GIF"));
                        })
                        .collect(Collectors.toList());
                if (!imageRefs.isEmpty()) {
                    try {
                        URL src = new URL(imageRefs.get(0));
                        File dst = urlToLocalPath(src);
                        if (log.isDebugEnabled()) {
                            log.debug("Saving " + src.toExternalForm() + " to " + dst.getAbsolutePath());
                        }
                        copy(src, dst);
                    }
                    catch (Exception e) {
                        log.debug("Failed to save image from " + imageRefs.get(0));
                    }
                }
            }
            progressFn.accept(0D);
        });
    }

    private void copy(URL src, File dst) throws IOException{
        if ((src != null) && (dst != null)) {
            boolean success = true;
            File parent = dst.getParentFile();
            if (!parent.exists()) {
                success = parent.mkdirs();
            }

            if (success) {
                InputStream in = src.openStream();
                OutputStream out = new FileOutputStream(dst);

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();
            }
        }
    }

    /**
     * Takes a URL of the framegrab and turns it into a file name. This is nescessary
     * since the 'filename' part of the URL may not be unique by iteself. By
     * writing out a full path we preserve some unique reference. As a shortcut
     * for MBARI internal use we remove parts of the pathname up to the word
     * 'framegrab' For example,
     * <code>
     *  // External code
     * SaveFramegrabAction action = new SaveFramegrabAction();
     * action.setSaveLocations('/Temp')
     *
     *  // Internal to SaveFramegrabAction
     *  // Here we use a url with the string 'framegrabs' in it
     * File f = urlToLocalPath(new URL("http://search.mbari.org/ARCHIVE/frameGrabs/Ventana/images/2627/00_50_41_04.jpg"));
     * f.toString(); // = "/Temp/Ventana/images/2677/00_05_41_04.jpg
     *
     *  // Here we use a url without 'framegrabs'
     * File g = urlToLocalPath(new URL("http://search.mbari.org/ARCHIVE/foo/Ventana/images/2627/00_50_41_04.jpg"));
     * g.toString(); // = "/Temp/search.mbari.org/ARCHIVE/foo/Ventana/images/2627/00_50_41_04.jpg"
     * </code>
     *
     * @param url
     * @return
     */
    private File urlToLocalPath(URL url) {
        String[] parts = url.toExternalForm().replace("%20", " ").split("/");
        int idx = 0;
        for (int i = 0; i < parts.length; i++) {
            String s = parts[i];
            if (s.equalsIgnoreCase("framegrabs")) {
                idx = i + 1;
                break;
            }
        }

        File f = targetDir;
        for (int i = idx; i < parts.length; i++) {
            String s = parts[i];
            if (!s.equalsIgnoreCase("http:") &&!s.equals("")) {
                f = new File(f, parts[i]);
            }
        }

        return f;
    }

}
