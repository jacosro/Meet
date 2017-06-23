package dds.project.meet.logic.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import static android.R.attr.src;

/**
 * Created by jacosro on 23/06/17.
 */

public class FileUtils {

    public static final long ONE_KB = 1024;
    public static final long ONE_MB = ONE_KB * ONE_KB;
    private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;
    private static final String TAG = "FileUtils";

    public static boolean copy(InputStream from, File to) {

        OutputStream out = null;
        try {
            Log.d(TAG, "Opening streams");
            out = new FileOutputStream(to);

            Log.d(TAG, "Opened");
            // Transfer bytes from in to out
            byte[] buf = new byte[1024*1024];
            int len;
            while ((len = from.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                    // Nothing
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // Nothing
                }
            }
        }
    }
}
