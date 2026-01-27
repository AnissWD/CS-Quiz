package com.aniss.csquiz;

import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class QuizUI extends JFrame {
    private Quiz quiz;
    private JLabel questionLabel;
    private JLabel progressLabel;
    private JLabel feedbackLabel;
    private JPanel answersPanel;
    private JRadioButton a, b, c, d;
    private ButtonGroup group;
    private JButton nextBtn;

    private final Color BG = new Color(30, 30, 30);
    private final Color CARD = new Color(45, 45, 45);
    private final Color ACCENT = new Color(70, 130, 255);
    private final Color TEXT = Color.WHITE;
    private final Color SUCCESS = new Color(50, 200, 100);
    private final Color ERROR = new Color(255, 80, 80);

    public QuizUI(Quiz quiz) {
        this.quiz = quiz;
        setTitle("CS Quiz");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);

        try {
            Image icon = Toolkit.getDefaultToolkit().getImage("src/CSQuizIcon.png");
            setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Icon not found");
        }

        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setLayout(new BorderLayout(10, 10));

        progressLabel = new JLabel();
        progressLabel.setForeground(ACCENT);
        progressLabel.setFont(new Font("Arial", Font.BOLD, 12));

        questionLabel = new JLabel();
        questionLabel.setForeground(TEXT);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setBackground(CARD);
        top.add(progressLabel, BorderLayout.NORTH);
        top.add(questionLabel, BorderLayout.CENTER);

        a = createOption();
        b = createOption();
        c = createOption();
        d = createOption();

        group = new ButtonGroup();
        group.add(a);
        group.add(b);
        group.add(c);
        group.add(d);

        answersPanel = new JPanel();
        answersPanel.setBackground(CARD);
        answersPanel.setLayout(new GridLayout(4, 1, 5, 5));
        answersPanel.add(a);
        answersPanel.add(b);
        answersPanel.add(c);
        answersPanel.add(d);

        nextBtn = new JButton("Next");
        nextBtn.setBackground(ACCENT);
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFont(new Font("Arial", Font.BOLD, 14));
        nextBtn.setFocusPainted(false);
        nextBtn.addActionListener(e -> next());

        feedbackLabel = new JLabel("");
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 14));
        feedbackLabel.setForeground(TEXT);
        feedbackLabel.setPreferredSize(new Dimension(0, 25));

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 5));
        bottomPanel.setBackground(CARD);
        bottomPanel.add(feedbackLabel, BorderLayout.NORTH);
        bottomPanel.add(nextBtn, BorderLayout.SOUTH);

        card.add(top, BorderLayout.NORTH);
        card.add(answersPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        add(card);
        loadQuestion();
        setVisible(true);
    }

    private JRadioButton createOption() {
        JRadioButton btn = new JRadioButton();
        btn.setBackground(CARD);
        btn.setForeground(TEXT);
        btn.setFont(new Font("Arial", Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setIcon(new ImageIcon());
        btn.setSelectedIcon(new ImageIcon());
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60), 2, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(new Color(55, 55, 55));
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ACCENT, 2, true),
                            BorderFactory.createEmptyBorder(15, 20, 15, 20)
                    ));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn.isEnabled() && !btn.isSelected()) {
                    btn.setBackground(CARD);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(60, 60, 60), 2, true),
                            BorderFactory.createEmptyBorder(15, 20, 15, 20)
                    ));
                }
            }
        });

        btn.addItemListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(new Color(55, 65, 85));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT, 3, true),
                        BorderFactory.createEmptyBorder(14, 19, 14, 19)
                ));
            } else {
                btn.setBackground(CARD);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(60, 60, 60), 2, true),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
            }
        });

        return btn;
    }

    private void playSound(String filename) {
        try {
            File soundFile = new File(filename);
            if (!soundFile.exists()) {
                System.out.println("Sound file not found: " + filename);
                return;
            }

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
                AudioFormat af = new AudioFormat(44100, 8, 1, true, false);
                SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
                sdl.open(af);
                sdl.start();

                byte[] buf = new byte[1];
                for (int i = 0; i < duration * 44.1; i++) {
                    double angle = i / (44100.0 / frequency) * 2.0 * Math.PI;
                    buf[0] = (byte) (Math.sin(angle) * 100);
                    sdl.write(buf, 0, 1);
                }

                sdl.drain();
                sdl.stop();
                sdl.close();
            } catch (LineUnavailableException e) {
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
                playBeep(400, 150); // Lower frequency
                try { Thread.sleep(50); } catch (InterruptedException e) {}
                playBeep(300, 200); // Even lower
            }).start();
        }
    }

    private void loadQuestion() {
        Question q = quiz.getCurrentQuestion();
        questionLabel.setText("<html>" + q.getText() + "</html>");
        progressLabel.setText("Question " + (quiz.getIndex() + 1) + ":");
        a.setText("A) " + q.getAnswer1());
        b.setText("B) " + q.getAnswer2());
        c.setText("C) " + q.getAnswer3());
        d.setText("D) " + q.getAnswer4());
        group.clearSelection();
        clearFeedback();
        nextBtn.setText("Next");
    }

    private void showFeedback(String message, Color color) {
        feedbackLabel.setText(message);
        feedbackLabel.setForeground(color);
    }

    private void clearFeedback() {
        feedbackLabel.setText("");
    }

    private void shakeComponent(JComponent comp) {
        Point original = comp.getLocation();
        Timer timer = new Timer(50, null);
        final int[] count = {0};
        timer.addActionListener(e -> {
            if (count[0] < 8) {
                int offset = (count[0] % 2 == 0) ? 10 : -10;
                comp.setLocation(original.x + offset, original.y);
                count[0]++;
            } else {
                comp.setLocation(original);
                timer.stop();
            }
        });
        timer.start();
    }

    private void flashCorrect(JRadioButton btn) {
        Color original = btn.getBackground();
        Color flash = SUCCESS;
        Timer timer = new Timer(100, null);
        final boolean[] isFlash = {false};
        final int[] count = {0};
        timer.addActionListener(e -> {
            if (count[0] < 4) {
                btn.setBackground(isFlash[0] ? original : flash);
                isFlash[0] = !isFlash[0];
                count[0]++;
            } else {
                btn.setBackground(original);
                timer.stop();
            }
        });
        timer.start();
    }

    private void flashWrong(JRadioButton btn) {
        Color original = btn.getBackground();
        Color flash = ERROR;
        Timer timer = new Timer(100, null);
        final boolean[] isFlash = {false};
        final int[] count = {0};
        timer.addActionListener(e -> {
            if (count[0] < 4) {
                btn.setBackground(isFlash[0] ? original : flash);
                isFlash[0] = !isFlash[0];
                count[0]++;
            } else {
                btn.setBackground(original);
                timer.stop();
            }
        });
        timer.start();
    }

    private JRadioButton getSelectedButton() {
        if (a.isSelected()) return a;
        if (b.isSelected()) return b;
        if (c.isSelected()) return c;
        return d;
    }

    private void next() {
        char ans;
        if (a.isSelected()) ans = 'A';
        else if (b.isSelected()) ans = 'B';
        else if (c.isSelected()) ans = 'C';
        else if (d.isSelected()) ans = 'D';
        else {
            shakeComponent(answersPanel);
            showFeedback("Please select an answer!", new Color(255, 165, 0));
            return;
        }

        char correctAnswer = quiz.getCurrentQuestion().getCorrect();
        boolean wasCorrect = (ans == correctAnswer);
        quiz.submitAnswer(ans);

        if (wasCorrect) {
            playCorrectSound();
            flashCorrect(getSelectedButton());
            showFeedback("Correct!", SUCCESS);

            if (!quiz.hasNext()) {
                nextBtn.setText("Finish");
            }

            Timer timer = new Timer(500, e -> {
                if (quiz.hasNext()) {
                    loadQuestion();
                } else {
                    showFinalScore();
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            playWrongSound();
            flashWrong(getSelectedButton());
            showFeedback("Wrong! The correct answer was " + correctAnswer, ERROR);

            if (!quiz.hasNext()) {
                nextBtn.setText("Finish");
            }

            Timer timer = new Timer(500, e -> {
                if (quiz.hasNext()) {
                    loadQuestion();
                } else {
                    showFinalScore();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void showFinalScore() {
        questionLabel.setText("<html><center>Quiz Completed!</center></html>");
        progressLabel.setText("");
        showFeedback("Final Score: " + quiz.getScore() + "/" + quiz.getTotal(), ACCENT);

        a.setVisible(false);
        b.setVisible(false);
        c.setVisible(false);
        d.setVisible(false);

        nextBtn.setText("Close");
        nextBtn.removeActionListener(nextBtn.getActionListeners()[0]);
        nextBtn.addActionListener(e -> dispose());
    }
}