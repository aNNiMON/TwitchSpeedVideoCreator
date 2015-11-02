package com.annimon.tsvc.tasks;

import com.annimon.tsvc.model.FFmpegOptions;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Video processing task.
 * 
 * @author aNNiMON
 */
public final class FFmpegTask extends PartialTask<Boolean> {
    
    private final FFmpegOptions fFmpegOptions;

    public FFmpegTask(FFmpegOptions fFmpegOptions) {
        this.fFmpegOptions = fFmpegOptions;
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
            updateMessage(line);
        }
        ffmpeg.waitFor();
        updateMessage("FFmpeg stopped");
        return true;
    }

    private void deleteOutputIfExists() {
        final File destFile = new File(fFmpegOptions.getOutput());
        if (destFile.exists()) destFile.delete();
    }
}
