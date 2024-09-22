// Created: 21 Juli 2024
package de.freese.player.test.ui.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public final class AudioSynth {
    private static final boolean BIG_ENDIAN = true;
    private static final int SAMPLE_SIZE_IN_BITS = 16;
    private static final boolean SIGNED = true;

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(AudioSynth::new);
    }

    private final JLabel elapsedTimeMeter = new JLabel("Duration:");

    public AudioSynth() {
        super();

        final JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEtchedBorder());
        controlPanel.add(elapsedTimeMeter);

        final JButton decayPulse = new JButton("Decay Pulse");
        final JButton echoPulse = new JButton("Echo Pulse");
        final JButton fmSweep = new JButton("FM Sweep");
        final JButton stereoPanning = new JButton("Stereo Panning");
        final JButton stereoPingpong = new JButton("Stereo Pingpong");
        final JButton tones = new JButton("Tones");

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1));
        buttonPanel.add(tones);
        buttonPanel.add(stereoPanning);
        buttonPanel.add(stereoPingpong);
        buttonPanel.add(fmSweep);
        buttonPanel.add(decayPulse);
        buttonPanel.add(echoPulse);

        echoPulse.addActionListener(event -> playEchoPulse());
        decayPulse.addActionListener(event -> playDecayPulse());
        fmSweep.addActionListener(event -> playFmSweep());
        stereoPanning.addActionListener(event -> playStereoPanning());
        stereoPingpong.addActionListener(event -> playStereoPingpong());
        tones.addActionListener(event -> playTones());

        // final JPanel centerPanel = new JPanel();
        // centerPanel.add(buttonPanel);

        final JFrame frame = new JFrame();
        frame.getContentPane().add(controlPanel, BorderLayout.NORTH);
        frame.getContentPane().add(buttonPanel, BorderLayout.CENTER);
        // frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

        frame.setTitle("Audio-Synth");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(250, 275);
        // frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void play(final byte[] audioData, final AudioFormat audioFormat) {
        elapsedTimeMeter.setText("Duration:");

        // Write the data to an output file.
        // // AudioSystem.write(audioInputStream, AudioFileFormat.Type.AU, new File(fileName.getText() + ".au"));
        // AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(fileName.getText() + ".wav"));

        // Thread.ofPlatform().daemon().name("player-", 1).start(new Player(sourceDataLine));
        Thread.ofVirtual().name("player-", 1).start(() -> {
            try (InputStream inputStream = new ByteArrayInputStream(audioData);
                 AudioInputStream audioInputStream = new AudioInputStream(inputStream, audioFormat, audioData.length / audioFormat.getFrameSize())) {

                final DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
                final SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                // final byte[] playBuffer = new byte[16384];
                final byte[] playBuffer = new byte[4096];

                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

                final long startTime = System.currentTimeMillis();
                int cnt;

                while ((cnt = audioInputStream.read(playBuffer, 0, playBuffer.length)) != -1) {
                    if (cnt > 0) {
                        sourceDataLine.write(playBuffer, 0, cnt);
                    }
                }

                sourceDataLine.drain();

                final long elapsedTime = System.currentTimeMillis() - startTime;

                sourceDataLine.stop();
                sourceDataLine.close();

                SwingUtilities.invokeLater(() -> elapsedTimeMeter.setText("Duration: %d ms".formatted(elapsedTime)));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * This method generates a mono triple-frequency pulse that decays in a linear fashion with time.
     */
    private void playDecayPulse() {
        final int channels = 1;
        final int bytesPerSamp = 2; // Based on channels
        final float sampleRate = 16_000.0F;

        // A buffer to hold two seconds mono and one second stereo data at 16000 samp/sec for 16-bit samples
        final byte[] audioData = new byte[16_000 * 4];
        final int byteLength = audioData.length;

        // Allowable 8000,11025,16000,22050,44100
        final int sampLength = byteLength / bytesPerSamp;

        // This class uses a ByteBuffer asShortBuffer to handle the data, it can only be used to generate signed 16-bit data.
        final ByteBuffer byteBuffer = ByteBuffer.wrap(audioData);
        final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();

        for (int cnt = 0; cnt < sampLength; cnt++) {
            // The value of scale controls the rate of decay - large scale, fast decay.
            double scale = 2D * cnt;

            if (scale > sampLength) {
                scale = sampLength;
            }

            final double gain = 16_000D * (sampLength - scale) / sampLength;
            final double time = cnt / sampleRate;
            final double freq = 499.0D; // Frequency
            final double sinValue = (Math.sin(2D * Math.PI * freq * time)
                    + Math.sin(2D * Math.PI * (freq / 1.8D) * time)
                    + Math.sin(2D * Math.PI * (freq / 1.5D) * time)) / 3.0D;
            shortBuffer.put((short) (gain * sinValue));
        }

        final AudioFormat audioFormat = new AudioFormat(sampleRate,
                SAMPLE_SIZE_IN_BITS,
                channels,
                SIGNED,
                BIG_ENDIAN);

        play(audioData, audioFormat);
    }

    /**
     * This method generates a mono triple-frequency pulse that decays in a linear fashion with time. However, three echoes
     * can be heard over time with the amplitude of the echoes also decreasing with time.
     */
    private void playEchoPulse() {
        final int channels = 1;
        final int bytesPerSamp = 2; // Based on channels
        final float sampleRate = 16_000.0F;

        // A buffer to hold two seconds mono and one second stereo data at 16000 samp/sec for 16-bit samples
        final byte[] audioData = new byte[16_000 * 4];
        final int byteLength = audioData.length;

        // Allowable 8000,11025,16000,22050,44100
        final int sampLength = byteLength / bytesPerSamp;

        // This class uses a ByteBuffer asShortBuffer to handle the data, it can only be used to generate signed 16-bit data.
        final ByteBuffer byteBuffer = ByteBuffer.wrap(audioData);
        final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();

        int cnt2 = -8_000;
        int cnt3 = -16_000;
        int cnt4 = -24_000;

        for (int cnt1 = 0; cnt1 < sampLength; cnt1++, cnt2++, cnt3++, cnt4++) {
            double val = playEchoPulseHelper(cnt1, sampLength, sampleRate);

            if (cnt2 > 0) {
                val += 0.7D * playEchoPulseHelper(cnt2, sampLength, sampleRate);
            }

            if (cnt3 > 0) {
                val += 0.49D * playEchoPulseHelper(cnt3, sampLength, sampleRate);
            }

            if (cnt4 > 0) {
                val += 0.34D * playEchoPulseHelper(cnt4, sampLength, sampleRate);
            }

            shortBuffer.put((short) val);
        }

        final AudioFormat audioFormat = new AudioFormat(sampleRate,
                SAMPLE_SIZE_IN_BITS,
                channels,
                SIGNED,
                BIG_ENDIAN);

        play(audioData, audioFormat);
    }

    private double playEchoPulseHelper(final int cnt, final int sampLength, final float sampleRate) {
        // The value of scale controls the rate of decay - large scale, fast decay.
        double scale = 2D * cnt;

        if (scale > sampLength) {
            scale = sampLength;
        }

        final double gain = 16_000D * (sampLength - scale) / sampLength;
        final double time = cnt / sampleRate;
        final double freq = 499.0D; // Frequency
        final double sinValue = (Math.sin(2D * Math.PI * freq * time)
                + Math.sin(2D * Math.PI * (freq / 1.8D) * time)
                + Math.sin(2D * Math.PI * (freq / 1.5D) * time)) / 3.0D;

        return (short) (gain * sinValue);
    }

    /**
     * This method generates a mono linear frequency sweep from 100 Hz to 1000Hz.
     */
    private void playFmSweep() {
        final int channels = 1;
        final int bytesPerSamp = 2; // Based on channels
        final float sampleRate = 16_000.0F;

        // A buffer to hold two seconds mono and one second stereo data at 16000 samp/sec for 16-bit samples
        final byte[] audioData = new byte[16_000 * 4];
        final int byteLength = audioData.length;

        // Allowable 8000,11025,16000,22050,44100
        final int sampLength = byteLength / bytesPerSamp;

        // This class uses a ByteBuffer asShortBuffer to handle the data, it can only be used to generate signed 16-bit data.
        final ByteBuffer byteBuffer = ByteBuffer.wrap(audioData);
        final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();

        final double lowFreq = 100.0D;
        final double highFreq = 1000.0D;

        for (int cnt = 0; cnt < sampLength; cnt++) {
            final double time = cnt / sampleRate;

            final double freq = lowFreq + cnt * (highFreq - lowFreq) / sampLength;
            final double sinValue = Math.sin(2D * Math.PI * freq * time);
            shortBuffer.put((short) (16_000D * sinValue));
        }

        final AudioFormat audioFormat = new AudioFormat(sampleRate,
                SAMPLE_SIZE_IN_BITS,
                channels,
                SIGNED,
                BIG_ENDIAN);

        play(audioData, audioFormat);
    }

    /**
     * This method generates a stereo speaker sweep, starting with a relatively high frequency
     * tone on the left speaker and moving across to a lower frequency tone on the right speaker.
     */
    private void playStereoPanning() {
        final int channels = 2;
        final int bytesPerSamp = 4; // Based on channels
        final float sampleRate = 16_000.0F;

        // A buffer to hold two seconds mono and one second stereo data at 16000 samp/sec for 16-bit samples
        final byte[] audioData = new byte[16_000 * 4];
        final int byteLength = audioData.length;

        // Allowable 8000,11025,16000,22050,44100
        final int sampLength = byteLength / bytesPerSamp;

        // This class uses a ByteBuffer asShortBuffer to handle the data, it can only be used to generate signed 16-bit data.
        final ByteBuffer byteBuffer = ByteBuffer.wrap(audioData);
        final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();

        for (int cnt = 0; cnt < sampLength; cnt++) {
            // Calculate time-varying gain for each speaker.
            final double rightGain = 16_000.0D * cnt / sampLength;
            final double leftGain = 16_000.0D - rightGain;

            final double time = cnt / sampleRate;
            final double freq = 600D; // Frequency

            // Generate data for left speaker.
            double sinValue = Math.sin(2D * Math.PI * freq * time);
            shortBuffer.put((short) (leftGain * sinValue));

            // Generate data for right speaker.
            sinValue = Math.sin(2D * Math.PI * (freq * 0.8D) * time);
            shortBuffer.put((short) (rightGain * sinValue));
        }

        final AudioFormat audioFormat = new AudioFormat(sampleRate,
                SAMPLE_SIZE_IN_BITS,
                channels,
                SIGNED,
                BIG_ENDIAN);

        play(audioData, audioFormat);
    }

    private void playStereoPingpong() {
        final int channels = 2;
        final int bytesPerSamp = 4; // Based on channels
        final float sampleRate = 16_000.0F;

        // A buffer to hold two seconds mono and one second stereo data at 16000 samp/sec for 16-bit samples
        final byte[] audioData = new byte[16_000 * 4];
        final int byteLength = audioData.length;

        // Allowable 8000,11025,16000,22050,44100
        final int sampLength = byteLength / bytesPerSamp;

        // This class uses a ByteBuffer asShortBuffer to handle the data, it can only be used to generate signed 16-bit data.
        final ByteBuffer byteBuffer = ByteBuffer.wrap(audioData);
        final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();

        double leftGain = 0.0D;
        double rightGain = 16_000.0D;

        for (int cnt = 0; cnt < sampLength; cnt++) {
            // Calculate time-varying gain for each speaker.
            if (cnt % (sampLength / 8) == 0) {
                // swap gain values
                final double temp = leftGain;
                leftGain = rightGain;
                rightGain = temp;
            }

            final double time = cnt / sampleRate;
            final double freq = 600D; // Frequency

            // Generate data for left speaker.
            double sinValue = Math.sin(2D * Math.PI * freq * time);
            shortBuffer.put((short) (leftGain * sinValue));

            // Generate data for right speaker.
            sinValue = Math.sin(2D * Math.PI * (freq * 0.8D) * time);
            shortBuffer.put((short) (rightGain * sinValue));
        }

        final AudioFormat audioFormat = new AudioFormat(sampleRate,
                SAMPLE_SIZE_IN_BITS,
                channels,
                SIGNED,
                BIG_ENDIAN);

        play(audioData, audioFormat);
    }

    /**
     * This method generates a mono tone consisting of the sum of three sinusoids.
     */
    private void playTones() {
        final int channels = 1;

        // Each channel requires two 8-bit bytes per 16-bit sample.
        final int bytesPerSamp = 2;
        final float sampleRate = 16_000.0F;

        // A buffer to hold two seconds mono and one second stereo data at 16000 samp/sec for 16-bit samples
        final byte[] audioData = new byte[16_000 * 4];
        final int byteLength = audioData.length;

        // Allowable 8000,11025,16000,22050,44100
        final int sampLength = byteLength / bytesPerSamp;

        // This class uses a ByteBuffer asShortBuffer to handle the data, it can only be used to generate signed 16-bit data.
        final ByteBuffer byteBuffer = ByteBuffer.wrap(audioData);
        final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();

        for (int cnt = 0; cnt < sampLength; cnt++) {
            final double time = cnt / sampleRate;
            final double freq = 950.0D; // Frequency
            final double sinValue = (Math.sin(2D * Math.PI * freq * time)
                    + Math.sin(2D * Math.PI * (freq / 1.8D) * time)
                    + Math.sin(2D * Math.PI * (freq / 1.5D) * time)
            ) / 3.0D;
            shortBuffer.put((short) (16_000D * sinValue));
        }

        final AudioFormat audioFormat = new AudioFormat(sampleRate,
                SAMPLE_SIZE_IN_BITS,
                channels,
                SIGNED,
                BIG_ENDIAN);

        play(audioData, audioFormat);
    }
}
