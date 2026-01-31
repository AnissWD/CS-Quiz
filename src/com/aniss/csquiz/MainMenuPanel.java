package com.aniss.csquiz;

import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.File;

public class MainMenuPanel extends JPanel {

    private Quiz quiz;
    private QuizApplication app;

    private final Color BG_DARK = new Color(10, 10, 20);
    private final Color BG_CARD = new Color(20, 20, 35);
    private final Color ACCENT_CYAN = new Color(0, 240, 255);
    private final Color ACCENT_MAGENTA = new Color(255, 0, 170);
    private final Color ACCENT_YELLOW = new Color(255, 220, 0);
    private final Color TEXT_PRIMARY = new Color(240, 240, 255);
    private final Color TEXT_SECONDARY = new Color(150, 150, 180);
    private final Color GLOW_CYAN = new Color(0, 240, 255, 40);

    public MainMenuPanel(Quiz quiz, QuizApplication app) {
        this.quiz = quiz;
        this.app = app;

        setLayout(new BorderLayout());
        setOpaque(false);

        createUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradient = new GradientPaint(
                0, 0, BG_DARK,
                getWidth(), getHeight(), new Color(30, 20, 50)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(new Color(0, 240, 255, 20));
        g2d.setStroke(new BasicStroke(1));
        int gridSize = 40;
        for (int i = 0; i < getWidth(); i += gridSize) {
            g2d.drawLine(i, 0, i, getHeight());
        }
        for (int i = 0; i < getHeight(); i += gridSize) {
            g2d.drawLine(0, i, getWidth(), i);
        }
    }

    private void createUI() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JPanel menuCard = createMenuCard();

        contentPanel.add(menuCard);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createMenuCard() {
        JPanel menuCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(GLOW_CYAN);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2d.setColor(BG_CARD);
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 27, 27);

                GradientPaint border = new GradientPaint(
                        0, 0, ACCENT_CYAN,
                        getWidth(), getHeight(), ACCENT_MAGENTA
                );
                g2d.setPaint(border);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 27, 27);
            }
        };
        menuCard.setOpaque(false);
        menuCard.setLayout(new BoxLayout(menuCard, BoxLayout.Y_AXIS));
        menuCard.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80));

        JLabel titleLabel = new JLabel("CS QUIZ");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 48));
        titleLabel.setForeground(ACCENT_CYAN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Test Your Knowledge");
        subtitleLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel questionsLabel = new JLabel(quiz.getTotal() + " Questions");
        questionsLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        questionsLabel.setForeground(ACCENT_YELLOW);
        questionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startBtn = createMenuButton("START QUIZ");
        startBtn.addActionListener(e -> startQuiz());

        JButton exitBtn = createMenuButton("EXIT");
        exitBtn.addActionListener(e -> System.exit(0));

        menuCard.add(titleLabel);
        menuCard.add(Box.createVerticalStrut(10));
        menuCard.add(subtitleLabel);
        menuCard.add(Box.createVerticalStrut(30));
        menuCard.add(questionsLabel);
        menuCard.add(Box.createVerticalStrut(40));
        menuCard.add(startBtn);
        menuCard.add(Box.createVerticalStrut(15));
        menuCard.add(exitBtn);

        return menuCard;
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, ACCENT_CYAN,
                        getWidth(), 0, ACCENT_MAGENTA
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Consolas", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(250, 50));
        btn.setMaximumSize(new Dimension(250, 50));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setFont(new Font("Consolas", Font.BOLD, 17));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setFont(new Font("Consolas", Font.BOLD, 16));
            }
        });

        return btn;
    }

    private void startQuiz() {
        playButtonSound();
        app.showQuiz();
    }

    private void playButtonSound() {
        File soundFile = new File("src/sounds/start.wav");
        if (soundFile.exists()) {
            playSound("src/sounds/start.wav");
        } else {
            new Thread(() -> {
                playBeep(440, 80);
                try { Thread.sleep(40); } catch (InterruptedException e) {}
                playBeep(554, 80);
                try { Thread.sleep(40); } catch (InterruptedException e) {}
                playBeep(659, 120);
            }).start();
        }
    }

    private void playSound(String filename) {
        try {
            File soundFile = new File(filename);
            if (!soundFile.exists()) return;

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        audioInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            clip.start();
        } catch (Exception e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    private void playBeep(final int frequency, final int duration) {
        try {
            int sampleRate = 44100;
            int samples = (int) ((duration / 1000.0) * sampleRate);
            byte[] buffer = new byte[samples * 2];

            int fadeLength = samples / 10;

            for (int i = 0; i < samples; i++) {
                double angle = 2.0 * Math.PI * i * frequency / sampleRate;
                double sample = Math.sin(angle);

                double envelope = 1.0;
                if (i < fadeLength) {
                    envelope = (double) i / fadeLength;
                } else if (i > samples - fadeLength) {
                    envelope = (double) (samples - i) / fadeLength;
                }

                short val = (short) (sample * envelope * 5000);
                buffer[i * 2] = (byte) (val & 0xFF);
                buffer[i * 2 + 1] = (byte) ((val >> 8) & 0xFF);
            }

            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            AudioInputStream ais = new AudioInputStream(bais, format, samples);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();

            Thread.sleep(duration + 100);
            clip.close();
        } catch (Exception e) {
            System.out.println("Error playing beep: " + e.getMessage());
        }
    }
}