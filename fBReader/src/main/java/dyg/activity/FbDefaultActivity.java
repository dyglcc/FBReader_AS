package dyg.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dyg.android.reader.R;

import org.geometerplus.android.fbreader.library.DefaultBooks;
import org.geometerplus.android.fbreader.library.LibraryActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class FbDefaultActivity extends Activity implements EasyPermissions.PermissionCallbacks{
    public static final String PATH_BOOKS = "localbooks";
    public static final String PATH_ICONS = "localicons";
    private static final int RC_CAMERA_AND_LOCATION = 110;
    public static void startActivity(Activity libraryActivity) {
        libraryActivity.startActivity(new Intent(libraryActivity, FbDefaultActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout_localbooks);
        BackendSupport.getInstance();

        methodRequiresTwoPermission();


    }
    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            extractBooks();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "需要sdcard读写权限",
                    RC_CAMERA_AND_LOCATION, perms);
        }
    }

    private static final String TAG = "permission";
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i(TAG, "获取权限成功" + perms);
        extractBooks();
    }

    private void extractBooks(){
        BackendSupport.getInstance().post(new Runnable() {
            @Override
            public void run() {
                File file = new File(getFilesDir(), PATH_BOOKS);
                if (!file.exists()) {
                    extractFiles();
                }
                initDefaultBooks();
            }
        });
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i(TAG, "获取权限失败" + perms);
    }
    private void extractFiles() {
        File file = getFilesDir();
        // create localbools dir
        File localbooks = new File(file, PATH_BOOKS);
        File localicons = new File(file, PATH_ICONS);
        if (!localbooks.exists()) {
            localbooks.mkdir();
        }
        if (!localicons.exists()) {
            localicons.mkdir();
        }
        AssetManager manager = getAssets();
        try {
            String[] books = manager.list(PATH_BOOKS);
            copyFile2Dirs(books, manager, localbooks, PATH_BOOKS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String[] icons = manager.list(PATH_ICONS);
            copyFile2Dirs(icons, manager, localicons, PATH_ICONS);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void copyFile2Dirs(String[] books, AssetManager manager, File destFile, String path) throws IOException {
        for (int i = 0; i < books.length; i++) {
            InputStream inputStream = manager.open(path + "/" + books[i]);
            writeFile(inputStream, destFile, books[i]);
        }
    }

    public void writeFile(InputStream inputStream, File desFile, String fileName) throws IOException {
        File file = new File(desFile, fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buff = new byte[1024];
        FileOutputStream fileOutputStream = null;
        try {
            int len;
            fileOutputStream = new FileOutputStream(file);
            while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
                fileOutputStream.write(buff, 0, len);
            }
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            fileOutputStream.close();
        }
    }

    private void initDefaultBooks() {
        new DefaultBooks(FbDefaultActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // test code del

        File file = new File(getFilesDir(), PATH_BOOKS);
        for (File file1 : file.listFiles()) {
            file1.delete();
        }
        file.delete();
        File file2 = new File(getFilesDir(), PATH_ICONS);
        for (File file3 : file2.listFiles()) {
            file3.delete();
        }
        file2.delete();
    }
}
