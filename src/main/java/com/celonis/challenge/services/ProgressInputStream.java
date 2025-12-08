package com.celonis.challenge.services;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class ProgressInputStream extends FilterInputStream {
    private final long totalSize;
    private final Consumer<Integer> progressConsumer;
    private long bytesRead = 0;

    private int lastReportedProgress = 0;

    public ProgressInputStream(InputStream in, long totalSize, Consumer<Integer> progressConsumer) {
        super(in);
        this.totalSize = totalSize;
        this.progressConsumer = progressConsumer;
    }

    @Override
    public int read() throws IOException {
        int b = super.read();
        if (b != -1) updateProgress(1);
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = super.read(b, off, len);
        if (n > 0) updateProgress(n);
        return n;
    }

    private void updateProgress(int increment) {
        bytesRead += increment;
        int currentProgress = getProgress();
        // Report progress only if it has increased by at least 10%
        if (currentProgress - lastReportedProgress >= 10) {
            lastReportedProgress = currentProgress;
            progressConsumer.accept(currentProgress);
        }
    }

    public int getProgress() {
        return totalSize > 0 ? (int) (100.0 * bytesRead / totalSize) : 0;
    }
}
