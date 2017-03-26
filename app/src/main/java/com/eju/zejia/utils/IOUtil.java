package com.eju.zejia.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.zip.ZipInputStream;

import timber.log.Timber;

/**
 * Created by Sidney on 2016/7/18.
 */
public class IOUtil {

    private IOUtil() {
    }

    public static boolean deleteFile(final File file) {
        return file.exists() && file.delete();
    }

    public static void recursiveDelete(final File file) {
        if (file.isDirectory() && file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    recursiveDelete(f);
                }
            }
        }
        deleteFile(file);
    }

    public static void deleteChildren(final File root) {
        if (root.isDirectory() && root.exists()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File f : files) {
                    recursiveDelete(f);
                }
            }
        }
    }

    public static void writeText(String text, final OutputStream out) throws IOException {
        writeText(text, "UTF-8", out);
    }

    public static void writeText(String text, String encode, final OutputStream out) throws IOException {
        try {
            out.write(text.getBytes(encode));
        } finally {
            close(out);
        }
    }

    public static void tryWriteText(String text, String encode, final OutputStream out) {
        try {
            writeText(text, encode, out);
        } catch (IOException e) {
            Timber.e(e, "Error occurs when process io.");
        }
    }

    public static void tryWriteText(String text, final OutputStream out) {
        tryWriteText(text, "UTF-8", out);
    }

    public static String readText(String encode, final InputStream in) throws IOException {
        byte[] bytes = readBytes(in);
        return new String(bytes, encode);
    }

    public static String readText(final InputStream in) throws IOException {
        return readText("UTF-8", in);
    }

    public static String tryReadText(String encode, final InputStream in) {
        try {
            return readText(encode, in);
        } catch (IOException e) {
            Timber.e(e, "Error occurs when process io.");
        }
        return null;
    }

    public static String tryReadText(final InputStream in) {
        return tryReadText("UTF-8", in);
    }

    public static void writeBytes(byte[] bytes, final OutputStream out) throws IOException {
        try {
            out.write(bytes);
        } finally {
            close(out);
        }
    }

    public static byte[] readBytes(final InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4098];
        int len = -1;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        return out.toByteArray();
    }

    public static byte[] tryReadBytes(final InputStream in) {
        try {
            return readBytes(in);
        } catch (IOException e) {
            Timber.e(e, "Error occurs when process io.");
        }
        return null;
    }

    public static void tryWriteBytes(byte[] bytes, final OutputStream out) {
        try {
            writeBytes(bytes, out);
        } catch (IOException e) {
            Timber.e(e, "Error occurs when process io.");
        }
    }

    public static void pipe(final InputStream in, final OutputStream out,
                            final int bufferSize)
            throws IOException {
        byte[] buffer = new byte[bufferSize];
        int len;
        try {
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } finally {
            close(out, in);
        }
    }

    public static void close(Closeable... closeable) {
        for (Closeable cls : closeable) {
            if (cls != null && (cls instanceof ZipInputStream)) {
                try {
                    cls.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static void tryPipe(final InputStream in, final OutputStream out,
                               final int bufferSize) {
        try {
            pipe(in, out, bufferSize);
        } catch (IOException e) {
            Timber.e(e, "Error occurs when process io.");
        }
    }

    public static void pipeLarge(final FileInputStream in, final FileOutputStream out)
            throws IOException {
        FileChannel readChannel = null;
        FileChannel writeChannel = null;
        try {
            readChannel = in.getChannel();
            writeChannel = out.getChannel();
            readChannel.transferTo(0, readChannel.size(), writeChannel);
        } finally {
            close(writeChannel, readChannel, out, in);
        }
    }
}
