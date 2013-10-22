package com.pych.foothillmap.data;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Elena Pychenkova on 24.09.13.
 */
public class StoreManager {

    static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
    static final String ViewerPath = SDCARD_PATH + "/Android/data/com.pych.foothillmap/files";
    static final String ViewerDataPath = ViewerPath + "/schedule.archive";


    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static void storeData(String fileName, Object object) {
        if (isExternalStorageWritable()) {
            FileOutputStream file_output_stream;
            ObjectOutputStream object_output_stream;

            try {
                File file = new File(ViewerPath + fileName);

                file_output_stream = new FileOutputStream(file);
                object_output_stream = new ObjectOutputStream(file_output_stream);

                object_output_stream.writeObject(object);

                object_output_stream.close();
                file_output_stream.close();
            } catch (Exception ex) {
            }
        }
    }

    public static Object restoreData(String fileName) {
        Object object = null;

        if (isExternalStorageReadable()) {
            try {
                File file = new File(ViewerPath + fileName);

                FileInputStream file_input_stream = new FileInputStream(file);
                ObjectInputStream object_input_stream = new ObjectInputStream(file_input_stream);
                object = object_input_stream.readObject();
                object_input_stream.close();
            } catch (Exception ex) {
            }
        }
        return object;

    }
}
