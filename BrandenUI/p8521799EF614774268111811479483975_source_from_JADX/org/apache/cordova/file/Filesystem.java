package org.apache.cordova.file;

import android.net.Uri;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Filesystem {
    public final String name;
    private final JSONObject rootEntry;
    protected final Uri rootUri;

    public interface ReadFileCallback {
        void handleData(InputStream inputStream, String str) throws IOException;
    }

    /* renamed from: org.apache.cordova.file.Filesystem.1 */
    class C00911 implements ReadFileCallback {
        final /* synthetic */ LocalFilesystemURL val$destination;

        C00911(LocalFilesystemURL localFilesystemURL) {
            this.val$destination = localFilesystemURL;
        }

        public void handleData(InputStream inputStream, String contentType) throws IOException {
            if (inputStream != null) {
                OutputStream os = Filesystem.this.getOutputStreamForURL(this.val$destination);
                byte[] buffer = new byte[8192];
                while (true) {
                    int bytesRead = inputStream.read(buffer, 0, 8192);
                    if (bytesRead <= 0) {
                        os.close();
                        return;
                    }
                    os.write(buffer, 0, bytesRead);
                }
            } else {
                throw new IOException("Cannot read file at source URL");
            }
        }
    }

    protected class LimitedInputStream extends FilterInputStream {
        long numBytesToRead;

        public LimitedInputStream(InputStream in, long numBytesToRead) {
            super(in);
            this.numBytesToRead = numBytesToRead;
        }

        public int read() throws IOException {
            if (this.numBytesToRead <= 0) {
                return -1;
            }
            this.numBytesToRead--;
            return this.in.read();
        }

        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            if (this.numBytesToRead <= 0) {
                return -1;
            }
            int bytesToRead = byteCount;
            if (((long) byteCount) > this.numBytesToRead) {
                bytesToRead = (int) this.numBytesToRead;
            }
            int numBytesRead = this.in.read(buffer, byteOffset, bytesToRead);
            this.numBytesToRead -= (long) numBytesRead;
            return numBytesRead;
        }
    }

    abstract LocalFilesystemURL URLforFilesystemPath(String str);

    abstract boolean canRemoveFileAtLocalURL(LocalFilesystemURL localFilesystemURL);

    abstract String filesystemPathForURL(LocalFilesystemURL localFilesystemURL);

    abstract JSONObject getEntryForLocalURL(LocalFilesystemURL localFilesystemURL) throws IOException;

    abstract JSONObject getFileForLocalURL(LocalFilesystemURL localFilesystemURL, String str, JSONObject jSONObject, boolean z) throws FileExistsException, IOException, TypeMismatchException, EncodingException, JSONException;

    abstract JSONObject getFileMetadataForLocalURL(LocalFilesystemURL localFilesystemURL) throws FileNotFoundException;

    abstract OutputStream getOutputStreamForURL(LocalFilesystemURL localFilesystemURL) throws IOException;

    abstract JSONArray readEntriesAtLocalURL(LocalFilesystemURL localFilesystemURL) throws FileNotFoundException;

    abstract void readFileAtURL(LocalFilesystemURL localFilesystemURL, long j, long j2, ReadFileCallback readFileCallback) throws IOException;

    abstract boolean recursiveRemoveFileAtLocalURL(LocalFilesystemURL localFilesystemURL) throws FileExistsException, NoModificationAllowedException;

    abstract boolean removeFileAtLocalURL(LocalFilesystemURL localFilesystemURL) throws InvalidModificationException, NoModificationAllowedException;

    abstract long truncateFileAtURL(LocalFilesystemURL localFilesystemURL, long j) throws IOException, NoModificationAllowedException;

    abstract long writeToFileAtURL(LocalFilesystemURL localFilesystemURL, String str, int i, boolean z) throws NoModificationAllowedException, IOException;

    public Filesystem(Uri rootUri, String name) {
        this.rootUri = rootUri;
        this.name = name;
        this.rootEntry = makeEntryForPath("/", name, Boolean.valueOf(true), rootUri.toString());
    }

    public static JSONObject makeEntryForPath(String path, String fsName, Boolean isDir, String nativeURL) {
        int i = 0;
        try {
            int end;
            boolean z;
            JSONObject entry = new JSONObject();
            if (path.endsWith("/")) {
                end = 1;
            } else {
                end = 0;
            }
            String[] parts = path.substring(0, path.length() - end).split("/+");
            String fileName = parts[parts.length - 1];
            String str = "isFile";
            if (isDir.booleanValue()) {
                z = false;
            } else {
                z = true;
            }
            entry.put(str, z);
            entry.put("isDirectory", isDir);
            entry.put("name", fileName);
            entry.put("fullPath", path);
            entry.put("filesystemName", fsName);
            String str2 = "filesystem";
            if (!"temporary".equals(fsName)) {
                i = 1;
            }
            entry.put(str2, i);
            if (isDir.booleanValue() && !nativeURL.endsWith("/")) {
                nativeURL = nativeURL + "/";
            }
            entry.put("nativeURL", nativeURL);
            return entry;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static JSONObject makeEntryForURL(LocalFilesystemURL inputURL, Boolean isDir, String nativeURL) {
        return makeEntryForPath(inputURL.fullPath, inputURL.filesystemName, isDir, nativeURL);
    }

    public Uri getRootUri() {
        return this.rootUri;
    }

    public JSONObject getRootEntry() {
        return this.rootEntry;
    }

    public JSONObject getParentForLocalURL(LocalFilesystemURL inputURL) throws IOException {
        LocalFilesystemURL newURL = new LocalFilesystemURL(inputURL.URL);
        if (!("".equals(inputURL.fullPath) || "/".equals(inputURL.fullPath))) {
            newURL.fullPath = newURL.fullPath.substring(0, inputURL.fullPath.replaceAll("/+$", "").lastIndexOf(47) + 1);
        }
        return getEntryForLocalURL(newURL);
    }

    protected LocalFilesystemURL makeDestinationURL(String newName, LocalFilesystemURL srcURL, LocalFilesystemURL destURL) {
        if ("null".equals(newName) || "".equals(newName)) {
            newName = srcURL.URL.getLastPathSegment();
        }
        String newDest = destURL.URL.toString();
        if (newDest.endsWith("/")) {
            newDest = newDest + newName;
        } else {
            newDest = newDest + "/" + newName;
        }
        return new LocalFilesystemURL(newDest);
    }

    JSONObject copyFileToURL(LocalFilesystemURL destURL, String newName, Filesystem srcFs, LocalFilesystemURL srcURL, boolean move) throws IOException, InvalidModificationException, JSONException, NoModificationAllowedException, FileExistsException {
        if (!move || srcFs.canRemoveFileAtLocalURL(srcURL)) {
            LocalFilesystemURL destination = makeDestinationURL(newName, srcURL, destURL);
            srcFs.readFileAtURL(srcURL, 0, -1, new C00911(destination));
            if (move) {
                srcFs.removeFileAtLocalURL(srcURL);
            }
            return getEntryForLocalURL(destination);
        }
        throw new NoModificationAllowedException("Cannot move file at source URL");
    }

    public JSONObject makeEntryForFile(File file) throws JSONException {
        return null;
    }
}
