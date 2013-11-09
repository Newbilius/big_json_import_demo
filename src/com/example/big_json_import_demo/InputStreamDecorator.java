package com.example.big_json_import_demo;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamDecorator<T extends InputStream> extends InputStream {
    // Декорируемый поток
    public final T inner;

    // Сколько байт было прочитано из потока
    protected long bytesReadedCount=0;

    public long completeBytes() {
        return bytesReadedCount;
    }

    public InputStreamDecorator(T inner) {
        this.inner = inner;
    }

    protected void publishByteReaded(long amount){
        bytesReadedCount += amount;
    }

    public int read() throws IOException {
        int r = inner.read();
        publishByteReaded(1);
        return r;
    }

    public void reset() throws IOException {
        publishByteReaded(-bytesReadedCount);
        inner.reset();
    }

    public void mark(int readlimit) {
        inner.mark(readlimit);
    }

    public void close() throws IOException {
        inner.close();
    }

    public int available() throws IOException {
        return inner.available();
    }

    public long skip(long n) throws IOException {
        long r = inner.skip(n);
        publishByteReaded(r);
        return r;

    }

    public int read(byte[] b) throws IOException {
        int r = inner.read(b);
        publishByteReaded(r);
        return r;
    }

    public boolean markSupported() {
        return inner.markSupported();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int r = inner.read(b, off, len);
        publishByteReaded(r);
        return r;
    }

}
