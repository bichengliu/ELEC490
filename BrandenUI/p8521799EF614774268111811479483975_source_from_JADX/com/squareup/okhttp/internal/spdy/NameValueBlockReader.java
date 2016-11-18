package com.squareup.okhttp.internal.spdy;

import com.squareup.okhttp.internal.Util;
import com.squareup.okhttp.internal.http.HttpTransport;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

class NameValueBlockReader implements Closeable {
    private int compressedLimit;
    private final FillableInflaterInputStream fillableInflaterInputStream;
    private final DataInputStream nameValueBlockIn;

    /* renamed from: com.squareup.okhttp.internal.spdy.NameValueBlockReader.2 */
    class C00192 extends Inflater {
        C00192() {
        }

        public int inflate(byte[] buffer, int offset, int count) throws DataFormatException {
            int result = super.inflate(buffer, offset, count);
            if (result != 0 || !needsDictionary()) {
                return result;
            }
            setDictionary(Spdy3.DICTIONARY);
            return super.inflate(buffer, offset, count);
        }
    }

    static class FillableInflaterInputStream extends InflaterInputStream {
        public FillableInflaterInputStream(InputStream in, Inflater inf) {
            super(in, inf);
        }

        public void fill() throws IOException {
            super.fill();
        }
    }

    /* renamed from: com.squareup.okhttp.internal.spdy.NameValueBlockReader.1 */
    class C00251 extends InputStream {
        final /* synthetic */ InputStream val$in;

        C00251(InputStream inputStream) {
            this.val$in = inputStream;
        }

        public int read() throws IOException {
            return Util.readSingleByte(this);
        }

        public int read(byte[] buffer, int offset, int byteCount) throws IOException {
            int consumed = this.val$in.read(buffer, offset, Math.min(byteCount, NameValueBlockReader.this.compressedLimit));
            NameValueBlockReader.this.compressedLimit = NameValueBlockReader.this.compressedLimit - consumed;
            return consumed;
        }

        public void close() throws IOException {
            this.val$in.close();
        }
    }

    NameValueBlockReader(InputStream in) {
        this.fillableInflaterInputStream = new FillableInflaterInputStream(new C00251(in), new C00192());
        this.nameValueBlockIn = new DataInputStream(this.fillableInflaterInputStream);
    }

    public List<String> readNameValueBlock(int length) throws IOException {
        this.compressedLimit += length;
        try {
            int numberOfPairs = this.nameValueBlockIn.readInt();
            if (numberOfPairs < 0) {
                throw new IOException("numberOfPairs < 0: " + numberOfPairs);
            } else if (numberOfPairs > HttpTransport.DEFAULT_CHUNK_LENGTH) {
                throw new IOException("numberOfPairs > 1024: " + numberOfPairs);
            } else {
                List<String> entries = new ArrayList(numberOfPairs * 2);
                for (int i = 0; i < numberOfPairs; i++) {
                    String name = readString();
                    String values = readString();
                    if (name.length() == 0) {
                        throw new IOException("name.length == 0");
                    }
                    entries.add(name);
                    entries.add(values);
                }
                doneReading();
                return entries;
            }
        } catch (DataFormatException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void doneReading() throws IOException {
        if (this.compressedLimit != 0) {
            this.fillableInflaterInputStream.fill();
            if (this.compressedLimit != 0) {
                throw new IOException("compressedLimit > 0: " + this.compressedLimit);
            }
        }
    }

    private String readString() throws DataFormatException, IOException {
        int length = this.nameValueBlockIn.readInt();
        byte[] bytes = new byte[length];
        Util.readFully(this.nameValueBlockIn, bytes);
        return new String(bytes, 0, length, "UTF-8");
    }

    public void close() throws IOException {
        this.nameValueBlockIn.close();
    }
}
