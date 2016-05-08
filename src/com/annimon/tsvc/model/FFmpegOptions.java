package com.annimon.tsvc.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * FFmpeg command line options
 * 
 * @author aNNiMON
 */
public final class FFmpegOptions {

    private String input;
    private double speedFactor;
    private boolean isAudio;
    private String output;

    public FFmpegOptions() {
        speedFactor = 1d;
        isAudio = true;
    }

    public FFmpegOptions(String input, double speedFactor, boolean isAudio, String output) {
        this.input = input;
        this.speedFactor = speedFactor;
        this.isAudio = isAudio;
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public double getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
    }

    public boolean isIsAudio() {
        return isAudio;
    }

    public void setIsAudio(boolean isAudio) {
        this.isAudio = isAudio;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
    
    public String buildOptions() {
        final StringBuilder sb = new StringBuilder();
        sb.append(" -protocol_whitelist ").append(whitelistProtocols());
        sb.append(" -i ").append('"').append(input).append('"');
        if (speedFactor < 1d) {
            sb.append(" -filter:v ").append(speedFactorFormat());
        }
        if (!isAudio) {
            sb.append(" -an");
        }
        sb.append(' ').append('"').append(output).append('"');
        return sb.toString();
    }
    
    public List<String> buildOptionsAsList() {
        final List<String> args = new ArrayList<>(10);
        args.add("-protocol_whitelist");
        args.add(whitelistProtocols());
        args.add("-i");
        args.add(input);
        if (speedFactor < 1d) {
            args.add("-filter:v");
            args.add(speedFactorFormat());
        }
        if (!isAudio) {
            args.add("-an");
        }
        args.add(output);
        return args;
    }
    
    private String speedFactorFormat() {
        return String.format("\"setpts=%s*PTS\"", 
                new DecimalFormat("#.#####", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
                        .format(speedFactor));
    }

    private String whitelistProtocols() {
        return "file,crypto,tcp,http,https";
    }

    @Override
    public String toString() {
        return buildOptions();
    }
}
