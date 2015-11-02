package com.annimon.tsvc.tasks;

import com.annimon.tsvc.Util;
import javafx.concurrent.Task;

/**
 * Checks if ffmpeg exists in system.
 * 
 * @author aNNiMON
 */
public final class FFmpegCheckingTask extends Task<Boolean> {

    @Override
    protected Boolean call() throws Exception {
        return Util.isFFmpegExists();
    }
}
