package dds.project.meet.logic.util;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import dds.project.meet.persistence.Persistence;
import dds.project.meet.persistence.util.QueryCallback;
import dds.project.meet.presentation.MainActivity;

/**
 * Created by jacosro on 23/06/17.
 */

public class ProfileImage {

    private static ProfileImage INSTANCE = null;

    private Context context;

    private Uri uri;
    private String filename;

    private ProfileImage() {}

    private ProfileImage(MainActivity context) {
        this.context = context;

        String username = Persistence.getInstance().userDAO.getCurrentUser().getUsername();
        filename = String.format("%s.jpg", username);
    }


    public static ProfileImage getInstance(MainActivity context) {
        if (INSTANCE == null) {
            INSTANCE = new ProfileImage(context);
        }
        return INSTANCE;
    }

    public void set(Uri uri) {
        set(uri, true);
    }

    public void set(Uri uri, boolean upload) {
        try {
            InputStream image = context.getContentResolver().openInputStream(uri);
            File cache = createTempFile();

            boolean success = FileUtils.copy(image, cache);

            if (success) {
                this.uri = Uri.fromFile(cache);

                if (upload) {
                    Persistence.getInstance().userDAO.updateUserImage(this.uri, new QueryCallback.EmptyCallBack<Boolean>());
                }
            } else {
                Toast.makeText(context, "Could not copy from files", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            Log.d("PROFILE_IMAGE", "Exception: " + e);
        }
    }

    private File createTempFile() {
        File filesPath = context.getCacheDir();

        return new File(filesPath, filename);
    }

    public void get(final QueryCallback<Uri> callback) {
        File avatar = new File(context.getCacheDir(), filename);
        if (avatar.exists()) {
            callback.result(Uri.fromFile(avatar));
        } else {
            Persistence.getInstance().userDAO.getUserImage(new QueryCallback<Uri>() {
                @Override
                public void result(Uri data) {
                    callback.result(data);
                    set(data, false);
                }
            });
        }

    }
}
