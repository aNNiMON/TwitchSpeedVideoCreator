package com.annimon.tsvc.model;

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
        sb.append(" -i ").append('"').append(input).append('"');
        if (speedFactor < 1d) {
            sb.append(" -filter:v ").append('"').append("setpts=").append(speedFactor).append("*PTS").append('"');
        }
        if (!isAudio) {
            sb.append(" -an");
        }
        sb.append(' ').append('"').append(output).append('"');
        return sb.toString();
    }

    @Override
    public String toString() {
        return buildOptions();
    }
}
