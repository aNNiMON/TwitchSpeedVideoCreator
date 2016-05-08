package com.annimon.tsvc.tasks;

import com.annimon.tsvc.model.FFmpegOptions;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Video processing task.
 * 
 * @author aNNiMON
 */
public final class FFmpegTask extends PartialTask<Boolean> {
    
    private static final Pattern FFMPEG_LINE = Pattern.compile(".*time=(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{2}).*");
    
    private final FFmpegOptions fFmpegOptions;
    private int resultLengthInMillis;

    public FFmpegTask(FFmpegOptions fFmpegOptions) {
        this.fFmpegOptions = fFmpegOptions;
    }
    
    public FFmpegTask setResultLength(int resultLength) {
        this.resultLengthInMillis = resultLength * 1000;
        return this;
    }
    
    @Override
    protected Boolean call() throws Exception {
        deleteOutputIfExists();
        
        final List<String> args = new ArrayList<>(10);
        args.add("ffmpeg");
        args.addAll(fFmpegOptions.buildOptionsAsList());
        
        final ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true);
        updateMessage("Starting ffmpeg");
        final Process ffmpeg = pb.start();
        final Scanner out = new Scanner(ffmpeg.getInputStream());
        while (out.hasNextLine()) {
            if (isCancelled()) {
                // Send stop key to ffmpeg
                updateMessage("Stopping ffmpeg");
                OutputStream ostream = ffmpeg.getOutputStream();
                ostream.write("q\n".getBytes());
                ostream.flush();
                break;
            }
            final String line = out.nextLine();
            calculateProgress(line);
            updateMessage(line);
        }
        
        ffmpeg.waitFor();
        updateMessage("FFmpeg stopped");
        return true;
    }

    private void calculateProgress(final String line) throws NumberFormatException {
        final Matcher matcher = FFMPEG_LINE.matcher(line);
        if (matcher.matches()) {
            final int hour = Integer.parseInt(matcher.group(1));
            final int minute = Integer.parseInt(matcher.group(2));
            final int second = Integer.parseInt(matcher.group(3));
            final int millisecond = Integer.parseInt(matcher.group(4)) * 10;
            final int seconds = (hour * 3600) + (minute * 60) + second;
            final int millis = seconds * 1000 + millisecond;
            updateProgress(millis, resultLengthInMillis);
        }
    }

    private void deleteOutputIfExists() {
        final File destFile = new File(fFmpegOptions.getOutput());
        if (destFile.exists()) destFile.delete();
    }
}
