package com.aniss.csquiz;

import javax.swing.*;
import java.awt.*;

public class QuizUI extends JFrame {

    private Quiz quiz;
    private JLabel questionLabel;
    private JLabel progressLabel;
    private JPanel answersPanel;
    private JRadioButton a, b, c, d;
    private ButtonGroup group;
    private JButton nextBtn;


    private final Color BG = new Color(30, 30, 30);
    private final Color CARD = new Color(45, 45, 45);
    private final Color ACCENT = new Color(70, 130, 255);
    private final Color TEXT = Color.WHITE;

    public QuizUI(Quiz quiz) {
        this.quiz = quiz;

        setTitle("CS Quiz");
        setSize(600, 400);
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

        JPanel top = new JPanel(new BorderLayout());
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


        card.add(top, BorderLayout.NORTH);
        card.add(answersPanel, BorderLayout.CENTER);
        card.add(nextBtn, BorderLayout.SOUTH);

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

    private void loadQuestion() {
        Question q = quiz.getCurrentQuestion();

        questionLabel.setText("<html>" + q.getText() + "</html>");
        progressLabel.setText("Question "+(quiz.getIndex()+1)+" :");

        a.setText("A) " + q.getAnswer1());
        b.setText("B) " + q.getAnswer2());
        c.setText("C) " + q.getAnswer3());
        d.setText("D) " + q.getAnswer4());

        group.clearSelection();
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
        Color flash = new Color(50, 200, 100);

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
        Color flash = new Color(255, 80, 80);

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
            JOptionPane.showMessageDialog(this, "Select an answer!");
            return;
        }

        char correctAnswer = quiz.getCurrentQuestion().getCorrect();
        boolean wasCorrect = (ans == correctAnswer);
        quiz.submitAnswer(ans);

        if (wasCorrect) {
            flashCorrect(getSelectedButton());
            Timer timer = new Timer(500, e -> {
                JOptionPane.showMessageDialog(this, "Correct!", "Result", JOptionPane.INFORMATION_MESSAGE);
                if (quiz.hasNext()) {
                    loadQuestion();
                } else {
                    JOptionPane.showMessageDialog(this, "CS Quiz Finished!\nScore: " + quiz.getScore() + "/" + quiz.getTotal());
                    dispose();
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            flashWrong(getSelectedButton());
            Timer timer = new Timer(500, e -> {
                JOptionPane.showMessageDialog(this, "Wrong! The correct answer was " + correctAnswer, "Result", JOptionPane.ERROR_MESSAGE);
                if (quiz.hasNext()) {
                    loadQuestion();
                } else {
                    JOptionPane.showMessageDialog(this, "CS Quiz Finished!\nScore: " + quiz.getScore() + "/" + quiz.getTotal());
                    dispose();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
}
