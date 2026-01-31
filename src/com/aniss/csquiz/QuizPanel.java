package com.aniss.csquiz;

import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class QuizPanel extends JPanel {
    private Quiz quiz;
    private QuizApplication app;
    private JLabel questionLabel;
    private JLabel progressLabel;
    private JLabel scoreLabel;
    private JPanel answersPanel;
    private JButton[] answerButtons;
    private JButton nextBtn;
    private JPanel progressBarPanel;
    private JLabel streakLabel;
    private JLabel timerLabel;

    private int streak = 0;
    private Timer countdownTimer;
    private int timeRemaining = 30;

    private final Color BG_DARK = new Color(10, 10, 20);
    private final Color BG_CARD = new Color(20, 20, 35);
    private final Color ACCENT_CYAN = new Color(0, 240, 255);
    private final Color ACCENT_MAGENTA = new Color(255, 0, 170);
    private final Color ACCENT_YELLOW = new Color(255, 220, 0);
    private final Color TEXT_PRIMARY = new Color(240, 240, 255);
    private final Color TEXT_SECONDARY = new Color(150, 150, 180);
    private final Color SUCCESS = new Color(0, 255, 150);
    private final Color ERROR = new Color(255, 70, 100);
    private final Color GLOW_CYAN = new Color(0, 240, 255, 40);
    private final Color GLOW_MAGENTA = new Color(255, 0, 170, 40);

    private Clip backgroundMusicClip;
    private boolean musicEnabled = true;

    public QuizPanel(Quiz quiz, QuizApplication app, Clip sharedMusicClip, boolean musicState) {
        this.quiz = quiz;
        this.app = app;
        this.backgroundMusicClip = sharedMusicClip;
        this.musicEnabled = musicState;

        setLayout(new BorderLayout());
        setOpaque(false);

        createUI();
        loadQuestion();
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
        JPanel header = createHeader();
        JPanel questionCard = createQuestionCard();
        answersPanel = createAnswersPanel();
        JPanel bottomPanel = createBottomPanel();

        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        contentPanel.add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(questionCard, BorderLayout.NORTH);
        centerPanel.add(answersPanel, BorderLayout.CENTER);

        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        progressLabel = new JLabel();
        progressLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        progressLabel.setForeground(TEXT_SECONDARY);

        JPanel centerStats = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        centerStats.setOpaque(false);

        scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        scoreLabel.setForeground(ACCENT_YELLOW);

        streakLabel = new JLabel("STREAK: 0");
        streakLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        streakLabel.setForeground(ACCENT_MAGENTA);

        centerStats.add(scoreLabel);
        centerStats.add(streakLabel);

        timerLabel = new JLabel("TIME: 30s");
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        timerLabel.setForeground(ACCENT_CYAN);

        header.add(progressLabel, BorderLayout.WEST);
        header.add(centerStats, BorderLayout.CENTER);
        header.add(timerLabel, BorderLayout.EAST);

        progressBarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                g2d.setColor(new Color(40, 40, 60));
                g2d.fillRoundRect(0, 0, width, height, 10, 10);

                int progress = (int) (width * (quiz.getIndex() / (double) quiz.getTotal()));
                GradientPaint gradient = new GradientPaint(
                        0, 0, ACCENT_CYAN,
                        progress, 0, ACCENT_MAGENTA
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, progress, height, 10, 10);

                g2d.setColor(new Color(0, 240, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, progress, height, 10, 10);
            }
        };
        progressBarPanel.setPreferredSize(new Dimension(0, 8));
        progressBarPanel.setOpaque(false);

        JPanel headerContainer = new JPanel(new BorderLayout(0, 15));
        headerContainer.setOpaque(false);
        headerContainer.add(header, BorderLayout.NORTH);
        headerContainer.add(progressBarPanel, BorderLayout.SOUTH);

        return headerContainer;
    }

    private JPanel createQuestionCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(GLOW_CYAN);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(BG_CARD);
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 18, 18);

                g2d.setColor(ACCENT_CYAN);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 18, 18);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        questionLabel.setForeground(TEXT_PRIMARY);

        card.add(questionLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createAnswersPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 15));
        panel.setOpaque(false);

        answerButtons = new JButton[4];

        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new AnswerButton();

            answerButtons[i].setFont(new Font("Segoe UI", Font.PLAIN, 16));
            answerButtons[i].setForeground(TEXT_PRIMARY);
            answerButtons[i].setHorizontalAlignment(SwingConstants.LEFT);
            answerButtons[i].setBorderPainted(false);
            answerButtons[i].setFocusPainted(false);
            answerButtons[i].setContentAreaFilled(false);
            answerButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            answerButtons[i].setPreferredSize(new Dimension(0, 60));
            answerButtons[i].setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

            final int idx = i;
            answerButtons[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((AnswerButton) e.getSource()).setHovered(true);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    ((AnswerButton) e.getSource()).setHovered(false);
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    selectAnswer(idx);
                }
            });

            panel.add(answerButtons[i]);
        }

        return panel;
    }

    private class AnswerButton extends JButton {
        private boolean isHovered = false;
        private boolean isSelected = false;

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            if (isHovered || isSelected) {
                g2d.setColor(GLOW_CYAN);
                g2d.fillRoundRect(0, 0, width, height, 15, 15);
            }

            if (isSelected) {
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 240, 255, 60),
                        0, height, new Color(255, 0, 170, 40)
                );
                g2d.setPaint(gradient);
            } else {
                g2d.setColor(new Color(30, 30, 50));
            }
            g2d.fillRoundRect(3, 3, width - 6, height - 6, 12, 12);

            g2d.setColor(isSelected ? ACCENT_CYAN : new Color(60, 60, 90));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(3, 3, width - 6, height - 6, 12, 12);

            super.paintComponent(g);
        }

        public void setHovered(boolean hovered) {
            this.isHovered = hovered;
            repaint();
        }

        public void setSelected(boolean selected) {
            this.isSelected = selected;
            repaint();
        }
    }

    private int selectedAnswerIndex = -1;

    private void selectAnswer(int index) {
        for (int i = 0; i < answerButtons.length; i++) {
            ((AnswerButton) answerButtons[i]).setSelected(false);
        }
        ((AnswerButton) answerButtons[index]).setSelected(true);
        selectedAnswerIndex = index;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        nextBtn = new JButton("NEXT") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                g2d.setColor(GLOW_MAGENTA);
                g2d.fillRoundRect(0, 0, width, height, 15, 15);

                GradientPaint gradient = new GradientPaint(
                        0, 0, ACCENT_CYAN,
                        width, 0, ACCENT_MAGENTA
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(3, 3, width - 6, height - 6, 12, 12);

                super.paintComponent(g);
            }
        };
        nextBtn.setFont(new Font("Consolas", Font.BOLD, 16));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setPreferredSize(new Dimension(0, 50));
        nextBtn.setBorderPainted(false);
        nextBtn.setFocusPainted(false);
        nextBtn.setContentAreaFilled(false);
        nextBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextBtn.addActionListener(e -> handleNext());

        panel.add(nextBtn, BorderLayout.CENTER);

        return panel;
    }

    private void loadQuestion() {
        stopTimer();

        Question q = quiz.getCurrentQuestion();
        questionLabel.setText("<html>" + q.getText() + "</html>");
        progressLabel.setText("QUESTION " + (quiz.getIndex() + 1) + "/" + quiz.getTotal());
        scoreLabel.setText("SCORE: " + quiz.getScore() + "/" + quiz.getTotal());
        streakLabel.setText("STREAK: " + streak);

        answerButtons[0].setText("A)  " + q.getAnswer1());
        answerButtons[1].setText("B)  " + q.getAnswer2());
        answerButtons[2].setText("C)  " + q.getAnswer3());
        answerButtons[3].setText("D)  " + q.getAnswer4());

        for (JButton btn : answerButtons) {
            ((AnswerButton) btn).setSelected(false);
            btn.setEnabled(true);
        }
        selectedAnswerIndex = -1;

        progressBarPanel.repaint();
        nextBtn.setText("NEXT");

        startTimer();
    }

    private void startTimer() {
        timeRemaining = 30;
        timerLabel.setText("TIME: " + timeRemaining + "s");

        countdownTimer = new Timer(1000, e -> {
            timeRemaining--;
            timerLabel.setText("TIME: " + timeRemaining + "s");

            if (timeRemaining <= 5) {
                timerLabel.setForeground(ERROR);
            } else {
                timerLabel.setForeground(ACCENT_CYAN);
            }

            if (timeRemaining <= 0) {
                stopTimer();
                autoSubmitWrong();
            }
        });
        countdownTimer.start();
    }

    private void stopTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
    }

    private void autoSubmitWrong() {
        streak = 0;
        streakLabel.setText("STREAK: " + streak);
        playWrongSound();
        showFeedback("Time's up!", ERROR);

        quiz.submitAnswer('X');

        for (JButton btn : answerButtons) {
            btn.setEnabled(false);
        }

        if (!quiz.hasNext()) {
            nextBtn.setText("FINISH");
        }

        Timer delay = new Timer(1500, e -> {
            if (quiz.hasNext()) {
                loadQuestion();
            } else {
                showFinalScore();
            }
        });
        delay.setRepeats(false);
        delay.start();
    }

    private void handleNext() {
        if (selectedAnswerIndex == -1) {
            shakeComponent(answersPanel);
            showNotification("Please select an answer!");
            return;
        }

        stopTimer();

        char[] answers = {'A', 'B', 'C', 'D'};
        char selectedAnswer = answers[selectedAnswerIndex];
        char correctAnswer = quiz.getCurrentQuestion().getCorrect();

        boolean isCorrect = (selectedAnswer == correctAnswer);
        quiz.submitAnswer(selectedAnswer);

        for (JButton btn : answerButtons) {
            btn.setEnabled(false);
        }

        if (isCorrect) {
            streak++;
            streakLabel.setText("STREAK: " + streak);
            playCorrectSound();
            flashButton(answerButtons[selectedAnswerIndex], SUCCESS);
            showFeedback("Correct! +" + streak + " streak", SUCCESS);
        } else {
            streak = 0;
            streakLabel.setText("STREAK: " + streak);
            playWrongSound();
            flashButton(answerButtons[selectedAnswerIndex], ERROR);

            int correctIndex = correctAnswer - 'A';
            flashButton(answerButtons[correctIndex], SUCCESS);

            showFeedback("Wrong! Correct: " + correctAnswer, ERROR);
        }

        scoreLabel.setText("SCORE: " + quiz.getScore() + "/" + quiz.getTotal());

        if (!quiz.hasNext()) {
            nextBtn.setText("FINISH");
        }

        Timer delay = new Timer(1500, e -> {
            if (quiz.hasNext()) {
                loadQuestion();
            } else {
                showFinalScore();
            }
        });
        delay.setRepeats(false);
        delay.start();
    }

    private void showFeedback(String message, Color color) {
        JLabel feedback = new JLabel(message);
        feedback.setFont(new Font("Consolas", Font.BOLD, 14));
        feedback.setForeground(color);
        feedback.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel notifPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        notifPanel.setOpaque(false);
        notifPanel.setLayout(new BorderLayout());
        notifPanel.add(feedback);
        notifPanel.setBounds(200, 10, 300, 40);

        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane != null) {
            rootPane.getLayeredPane().add(notifPanel, JLayeredPane.POPUP_LAYER);

            Timer fadeOut = new Timer(1500, e -> {
                rootPane.getLayeredPane().remove(notifPanel);
                rootPane.getLayeredPane().repaint();
            });
            fadeOut.setRepeats(false);
            fadeOut.start();
        }
    }

    private void showNotification(String message) {
        JLabel notif = new JLabel(message);
        notif.setFont(new Font("Consolas", Font.BOLD, 12));
        notif.setForeground(ACCENT_YELLOW);
        notif.setHorizontalAlignment(SwingConstants.CENTER);
        notif.setBounds(200, 15, 300, 30);

        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane != null) {
            rootPane.getLayeredPane().add(notif, JLayeredPane.POPUP_LAYER);

            Timer remove = new Timer(1000, e -> {
                rootPane.getLayeredPane().remove(notif);
                rootPane.getLayeredPane().repaint();
            });
            remove.setRepeats(false);
            remove.start();
        }
    }

    private void shakeComponent(JComponent comp) {
        Point original = comp.getLocation();
        Timer timer = new Timer(50, null);
        final int[] count = {0};
        timer.addActionListener(e -> {
            if (count[0] < 6) {
                int offset = (count[0] % 2 == 0) ? 8 : -8;
                comp.setLocation(original.x + offset, original.y);
                count[0]++;
            } else {
                comp.setLocation(original);
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    private void flashButton(JButton btn, Color flashColor) {
        Timer timer = new Timer(150, null);
        final int[] count = {0};
        Color original = btn.getForeground();

        timer.addActionListener(e -> {
            if (count[0] < 4) {
                btn.setForeground(count[0] % 2 == 0 ? flashColor : original);
                count[0]++;
            } else {
                btn.setForeground(original);
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    private void showFinalScore() {
        stopTimer();
        app.showResults();
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    private void playBeep(final int frequency, final int duration) {
        new Thread(() -> {
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
        }).start();
    }

    private void playCorrectSound() {
        File soundFile = new File("src/sounds/correct.wav");
        if (soundFile.exists()) {
            playSound("src/sounds/correct.wav");
        } else {
            new Thread(() -> {
                playBeep(523, 100);
                try { Thread.sleep(50); } catch (InterruptedException e) {}
                playBeep(659, 100);
                try { Thread.sleep(50); } catch (InterruptedException e) {}
                playBeep(784, 150);
            }).start();
        }
    }

    private void playWrongSound() {
        File soundFile = new File("src/sounds/wrong.wav");
        if (soundFile.exists()) {
            playSound("src/sounds/wrong.wav");
        } else {
            new Thread(() -> {
                playBeep(400, 150);
                try { Thread.sleep(50); } catch (InterruptedException e) {}
                playBeep(300, 200);
            }).start();
        }
    }
}