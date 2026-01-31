package com.aniss.csquiz;

import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;

public class ResultsScreen extends JPanel {

    private Quiz quiz;
    private QuizApplication app;
    private Clip backgroundMusicClip;
    private boolean musicEnabled;

    private final Color BG_DARK = new Color(10, 10, 20);
    private final Color BG_CARD = new Color(20, 20, 35);
    private final Color ACCENT_CYAN = new Color(0, 240, 255);
    private final Color ACCENT_MAGENTA = new Color(255, 0, 170);
    private final Color ACCENT_YELLOW = new Color(255, 220, 0);
    private final Color TEXT_PRIMARY = new Color(240, 240, 255);
    private final Color TEXT_SECONDARY = new Color(150, 150, 180);
    private final Color SUCCESS = new Color(0, 255, 150);
    private final Color ERROR = new Color(255, 70, 100);
    private final Color GLOW_MAGENTA = new Color(255, 0, 170, 40);

    public ResultsScreen(Quiz quiz, QuizApplication app, Clip backgroundMusicClip, boolean musicEnabled, JFrame parentFrame) {
        this.quiz = quiz;
        this.app = app;
        this.backgroundMusicClip = backgroundMusicClip;
        this.musicEnabled = musicEnabled;

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
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel headerPanel = createHeader();
        JScrollPane resultsPanel = createResultsList();
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("QUIZ RESULTS");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 36));
        titleLabel.setForeground(ACCENT_CYAN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        double percentage = (quiz.getScore() / (double) quiz.getTotal()) * 100;
        JLabel scoreLabel = new JLabel(quiz.getScore() + " / " + quiz.getTotal() + " (" + String.format("%.1f%%", percentage) + ")");
        scoreLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        scoreLabel.setForeground(ACCENT_YELLOW);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scoreLabel);

        return panel;
    }

    private JScrollPane createResultsList() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        for (int i = 0; i < quiz.getTotal(); i++) {
            Question q = quiz.getQuestion(i);
            char userAnswer = quiz.getUserAnswer(i);
            char correctAnswer = q.getCorrect();
            boolean isCorrect = userAnswer == correctAnswer;

            JPanel questionPanel = createQuestionPanel(i + 1, q, userAnswer, correctAnswer, isCorrect);
            listPanel.add(questionPanel);
            listPanel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = ACCENT_CYAN;
                this.trackColor = new Color(30, 30, 50);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });

        return scrollPane;
    }

    private JPanel createQuestionPanel(int num, Question q, char userAnswer, char correctAnswer, boolean isCorrect) {
        final Color successColor = SUCCESS;
        final Color errorColor = ERROR;

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color glowColor = isCorrect ? new Color(0, 255, 150, 40) : new Color(255, 70, 100, 40);
                g2d.setColor(glowColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(BG_CARD);
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 13, 13);

                Color borderColor = isCorrect ? successColor : errorColor;
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 13, 13);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel statusLabel = new JLabel(isCorrect ? "[+]" : "[X]");
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 20));
        statusLabel.setForeground(isCorrect ? successColor : errorColor);

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel questionLabel = new JLabel("Q" + num + ": " + q.getText());
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        questionLabel.setForeground(TEXT_PRIMARY);

        JLabel yourAnswerLabel = new JLabel("Your answer: " + userAnswer + ") " + getAnswerText(q, userAnswer));
        yourAnswerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        yourAnswerLabel.setForeground(isCorrect ? successColor : errorColor);

        contentPanel.add(questionLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(yourAnswerLabel);

        if (!isCorrect) {
            JLabel correctAnswerLabel = new JLabel("Correct answer: " + correctAnswer + ") " + getAnswerText(q, correctAnswer));
            correctAnswerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            correctAnswerLabel.setForeground(successColor);
            contentPanel.add(Box.createVerticalStrut(3));
            contentPanel.add(correctAnswerLabel);
        }

        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private String getAnswerText(Question q, char answer) {
        switch (answer) {
            case 'A': return q.getAnswer1();
            case 'B': return q.getAnswer2();
            case 'C': return q.getAnswer3();
            case 'D': return q.getAnswer4();
            default: return "No answer";
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setOpaque(false);

        JButton retryBtn = createButton("RETRY QUIZ");
        retryBtn.addActionListener(e -> retryQuiz());

        JButton menuBtn = createButton("MAIN MENU");
        menuBtn.addActionListener(e -> returnToMenu());

        panel.add(retryBtn);
        panel.add(menuBtn);

        return panel;
    }

    private JButton createButton(String text) {
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
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private void retryQuiz() {
        quiz.reset();
        app.showQuiz();
    }

    private void returnToMenu() {
        quiz.reset();
        app.showMainMenu();
    }
}