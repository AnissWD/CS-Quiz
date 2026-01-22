package com.aniss.csquiz;

import javax.swing.*;
import java.awt.*;

public class QuizUI extends JFrame {

    private Quiz quiz;
    private JLabel questionLabel;
    private JLabel progressLabel;
    private JRadioButton a, b, c, d;
    private ButtonGroup group;
    private JButton nextBtn;


    private final Color BG = new Color(30, 30, 30);
    private final Color CARD = new Color(45, 45, 45);
    private final Color ACCENT = new Color(70, 130, 255);
    private final Color TEXT = Color.WHITE;

    public QuizUI(Quiz quiz) {
        this.quiz = quiz;

        setTitle("CS QuizPackage.Quiz");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);


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

        JPanel answers = new JPanel();
        answers.setBackground(CARD);
        answers.setLayout(new GridLayout(4, 1, 5, 5));
        answers.add(a);
        answers.add(b);
        answers.add(c);
        answers.add(d);


        nextBtn = new JButton("Next ➜");
        nextBtn.setBackground(ACCENT);
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFont(new Font("Arial", Font.BOLD, 14));
        nextBtn.setFocusPainted(false);
        nextBtn.addActionListener(e -> next());


        card.add(top, BorderLayout.NORTH);
        card.add(answers, BorderLayout.CENTER);
        card.add(nextBtn, BorderLayout.SOUTH);

        add(card);
        loadQuestion();
        setVisible(true);
    }

    private JRadioButton createOption() {
        JRadioButton btn = new JRadioButton();
        btn.setBackground(CARD);
        btn.setForeground(TEXT);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        return btn;
    }

    private void loadQuestion() {
        Question q = quiz.getCurrentQuestion();

        questionLabel.setText("<html>" + q.getText() + "</html>");
        progressLabel.setText(
                "QuizPackage.Question " + (quiz.getTotal() - (quiz.hasNext() ? quiz.getTotal() - 1 : 0))
        );

        a.setText("A) " + q.getAnswer1());
        b.setText("B) " + q.getAnswer2());
        c.setText("C) " + q.getAnswer3());
        d.setText("D) " + q.getAnswer4());

        group.clearSelection();
    }

    private void next() {
        char ans;

        if (a.isSelected()) ans = 'A';
        else if (b.isSelected()) ans = 'B';
        else if (c.isSelected()) ans = 'C';
        else if (d.isSelected()) ans = 'D';
        else {
            JOptionPane.showMessageDialog(this, "Select an answer!");
            return;
        }

        quiz.submitAnswer(ans);

        if (quiz.hasNext()) {
            loadQuestion();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "QuizPackage.Quiz Finished!\nScore: " +
                            quiz.getScore() + "/" + quiz.getTotal()
            );
            dispose();
        }
    }
}
