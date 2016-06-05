package com.annimon.tsvc.tasks;

import com.annimon.tsvc.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javafx.concurrent.Task;

public final class QuickDownloadTask extends Task<List<String>> implements TaskListener<List<String>> {

    private final PlaylistTask playlistTask;
    private final String workDirectory;
    private final int numberOfParts;

    public QuickDownloadTask(PlaylistTask playlistTask, String workDirectory, int numberOfParts) {
        this.playlistTask = playlistTask;
        this.workDirectory = workDirectory;
        this.numberOfParts = numberOfParts;
        playlistTask.setListener(QuickDownloadTask.this);
    }

    @Override
    protected List<String> call() throws Exception {
        final List<String> urls = playlistTask.call().stream()
                .filter(s -> s.startsWith("http"))
                .collect(Collectors.toList());

        final int urlsSize = urls.size();
        final int limitItems = (int) Math.ceil(urlsSize / (double)numberOfParts);
        final List<List<String>> urlsGroup = urls.stream()
                .collect(
                        ArrayList::new,
                        (list, url) -> {
                            final boolean needNewList = (list.isEmpty() || (list.get(list.size() - 1).size() >= limitItems));
                            final List<String> innerList = needNewList
                                    ? new ArrayList<>(limitItems)
                                    : list.get(list.size() - 1);
                            innerList.add(url);
                            if (needNewList) {
                                list.add(innerList);
                            }
                        },
                        (list1, list2) -> list1.addAll(list2));

        // Prepare to download
        final int groupsSize = urlsGroup.size();
        deletePartsIfExists(groupsSize);

        // Download in separate threads
        updateTaskMessage("Starting download " + urlsSize + " urls");
        final Thread[] threads = new Thread[groupsSize];
        final AtomicLong downloadedPartsCount = new AtomicLong(0L);
        for (int i = 0; i < groupsSize; i++) {
            final File partFile = partFile(i + 1);
            final List<String> partUrls = urlsGroup.get(i);
            threads[i] = new Thread(() -> {
                final int partUrlsSize = partUrls.size();
                for (int j = 0; j < partUrlsSize; j++) {
                    if (isTaskCancelled()) {
                        updateMessage("Stopping download " + partFile.getName());
                        return;
                    }
                    Util.download(partUrls.get(j), partFile, true);
                    updateTaskProgress(downloadedPartsCount.incrementAndGet(), urlsSize);
                }
            });
            threads[i].start();
        }

        // Wait until complete
        for (Thread thread : threads) {
            thread.join();
        }

        if (isTaskCancelled()) {
            updateMessage("Stopping download");
            return urls;
        }

        // Join all files
        updateTaskMessage("Joining " + groupsSize + " files");
        final File destFile = new File(workDirectory, playlistTask.getVodId() + ".ts");
        for (int i = 1; i <= groupsSize; i++) {
            final File partFile = partFile(i);
            Util.joinFiles(destFile, partFile);
            partFile.delete();
        }
        return urls;
    }

    private void deletePartsIfExists(int count) {
        for (int i = 1; i <= count; i++) {
            final File destFile = partFile(i);
            if (destFile.exists()) destFile.delete();
        }
    }

    private File partFile(int index) {
        return new File(workDirectory, playlistTask.getVodId() + "_" + index + ".ts");
    }

    @Override
    public void updateTaskValue(List<String> value) {
        updateValue(value);
    }

    @Override
    public void updateTaskTitle(String title) {
        updateTitle(title);
    }

    @Override
    public void updateTaskMessage(String message) {
        updateMessage(message);
    }

    @Override
    public void updateTaskProgress(double workDone, double max) {
        updateProgress(workDone, max);
    }

    @Override
    public void updateTaskProgress(long workDone, long max) {
        updateProgress(workDone, max);
    }

    @Override
    public boolean isTaskCancelled() {
        return isCancelled();
    }
}
