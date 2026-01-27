package com.aniss.csquiz;

import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class QuizUI extends JFrame {
    private Quiz quiz;
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

    public QuizUI(Quiz quiz) {
        this.quiz = quiz;
        setTitle("CS QUIZ");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage("src/icons/CSQuizIcon.png");
            setIconImage(icon);
        } catch (Exception e) {
            System.out.println("Icon not found");
        }

        JPanel titleBar = createTitleBar();

        JPanel mainPanel = new JPanel() {
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
        };
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setOpaque(false);

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

        mainPanel.add(titleBar, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        loadQuestion();
        setVisible(true);

        SwingUtilities.invokeLater(() -> startBackgroundMusic());
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(15, 15, 25));
        titleBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ACCENT_CYAN),
                BorderFactory.createEmptyBorder(10, 20, 10, 10)
        ));

        JLabel titleLabel = new JLabel("CS QUIZ");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        titleLabel.setForeground(ACCENT_CYAN);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton musicBtn = createTitleBarButton("MUSIC");
        musicBtn.addActionListener(e -> {
            toggleBackgroundMusic();
            musicBtn.setText(musicEnabled ? "MUSIC" : "MUTED");
        });

        JButton minimizeBtn = createTitleBarButton("MIN");
        minimizeBtn.addActionListener(e -> setState(JFrame.ICONIFIED));

        JButton closeBtn = createTitleBarButton("EXIT");
        closeBtn.setForeground(ERROR);
        closeBtn.addActionListener(e -> {
            stopBackgroundMusic();
            dispose();
        });

        buttonPanel.add(musicBtn);
        buttonPanel.add(minimizeBtn);
        buttonPanel.add(closeBtn);

        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(buttonPanel, BorderLayout.EAST);

        MouseAdapter ma = new MouseAdapter() {
            Point initial;
            @Override
            public void mousePressed(MouseEvent e) {
                initial = e.getPoint();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = getLocation().x + e.getX() - initial.x;
                int y = getLocation().y + e.getY() - initial.y;
                setLocation(x, y);
            }
        };
        titleBar.addMouseListener(ma);
        titleBar.addMouseMotionListener(ma);

        return titleBar;
    }

    private JButton createTitleBarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Consolas", Font.BOLD, 11));
        btn.setForeground(TEXT_SECONDARY);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 5, 0, 5));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(ACCENT_CYAN);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.getText().equals("EXIT")) {
                    btn.setForeground(ERROR);
                } else {
                    btn.setForeground(TEXT_SECONDARY);
                }
            }
        });

        return btn;
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
        String[] labels = {"A", "B", "C", "D"};

        for (int i = 0; i < 4; i++) {
            final int index = i;
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

        getLayeredPane().add(notifPanel, JLayeredPane.POPUP_LAYER);

        Timer fadeOut = new Timer(1500, e -> {
            getLayeredPane().remove(notifPanel);
            getLayeredPane().repaint();
        });
        fadeOut.setRepeats(false);
        fadeOut.start();
    }

    private void showNotification(String message) {
        JLabel notif = new JLabel(message);
        notif.setFont(new Font("Consolas", Font.BOLD, 12));
        notif.setForeground(ACCENT_YELLOW);
        notif.setHorizontalAlignment(SwingConstants.CENTER);
        notif.setBounds(200, 15, 300, 30);

        getLayeredPane().add(notif, JLayeredPane.POPUP_LAYER);

        Timer remove = new Timer(1000, e -> {
            getLayeredPane().remove(notif);
            getLayeredPane().repaint();
        });
        remove.setRepeats(false);
        remove.start();
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
        stopBackgroundMusic();

        getContentPane().removeAll();

        JPanel finalPanel = new JPanel() {
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
            }
        };
        finalPanel.setLayout(new GridBagLayout());

        JPanel scoreCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(GLOW_MAGENTA);
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
        scoreCard.setOpaque(false);
        scoreCard.setLayout(new BoxLayout(scoreCard, BoxLayout.Y_AXIS));
        scoreCard.setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        JLabel completedLabel = new JLabel("QUIZ COMPLETED!");
        completedLabel.setFont(new Font("Consolas", Font.BOLD, 28));
        completedLabel.setForeground(ACCENT_CYAN);
        completedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreLabel = new JLabel(quiz.getScore() + " / " + quiz.getTotal());
        scoreLabel.setFont(new Font("Consolas", Font.BOLD, 72));
        scoreLabel.setForeground(TEXT_PRIMARY);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        double percentage = (quiz.getScore() / (double) quiz.getTotal()) * 100;
        JLabel percentLabel = new JLabel(String.format("%.1f%%", percentage));
        percentLabel.setFont(new Font("Consolas", Font.PLAIN, 24));
        percentLabel.setForeground(ACCENT_YELLOW);
        percentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String grade;
        Color gradeColor;
        if (percentage >= 90) {
            grade = "EXCELLENT!";
            gradeColor = SUCCESS;
        } else if (percentage >= 70) {
            grade = "GOOD JOB!";
            gradeColor = ACCENT_CYAN;
        } else if (percentage >= 50) {
            grade = "KEEP TRYING!";
            gradeColor = ACCENT_YELLOW;
        } else {
            grade = "NEEDS WORK";
            gradeColor = ERROR;
        }

        JLabel gradeLabel = new JLabel(grade);
        gradeLabel.setFont(new Font("Consolas", Font.BOLD, 20));
        gradeLabel.setForeground(gradeColor);
        gradeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton closeBtn = new JButton("CLOSE") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, ACCENT_MAGENTA,
                        getWidth(), 0, ACCENT_CYAN
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                super.paintComponent(g);
            }
        };
        closeBtn.setFont(new Font("Consolas", Font.BOLD, 16));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setPreferredSize(new Dimension(200, 45));
        closeBtn.setMaximumSize(new Dimension(200, 45));
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());

        scoreCard.add(completedLabel);
        scoreCard.add(Box.createVerticalStrut(30));
        scoreCard.add(scoreLabel);
        scoreCard.add(Box.createVerticalStrut(10));
        scoreCard.add(percentLabel);
        scoreCard.add(Box.createVerticalStrut(20));
        scoreCard.add(gradeLabel);
        scoreCard.add(Box.createVerticalStrut(40));
        scoreCard.add(closeBtn);

        finalPanel.add(scoreCard);
        add(finalPanel);
        revalidate();
        repaint();
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

    private void startBackgroundMusic() {
        try {
            File musicFile = new File("src/sounds/background.wav");
            System.out.println("Looking for background music at: " + musicFile.getAbsolutePath());

            if (!musicFile.exists()) {
                System.out.println("Background music file not found!");
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioInputStream);

            if (backgroundMusicClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f);
            }

            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusicClip.start();
            System.out.println("Background music started");
        } catch (Exception e) {
            System.out.println("Error starting background music: " + e.getMessage());
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
        }
    }

    private void toggleBackgroundMusic() {
        if (backgroundMusicClip == null) return;

        if (musicEnabled) {
            backgroundMusicClip.stop();
            musicEnabled = false;
        } else {
            backgroundMusicClip.start();
            musicEnabled = true;
        }
    }
}