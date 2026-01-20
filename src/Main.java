import javax.swing.*;

public class Main{
    public static void main(String[] args){
    Quiz quiz= new Quiz();

        quiz.addQuestion(new Question(
                "Which layer handles routing in the OSI model?",
                "Transport",
                "Network",
                "Data Link",
                "Session",
                'B'
        ));

        quiz.addQuestion(new Question(
                "What does CPU stand for?",
                "Central Process Unit",
                "Central Processor Utility",
                "Central Processing Unit",
                "None of the above",
                'C'
        ));
        quiz.addQuestion(new Question(
                "SQL is mainly used for?",
                "Designing UI",
                "Managing databases",
                "Networking",
                "Operating systems",
                'B'
        ));
        quiz.addQuestion(new Question(
                "Which data structure uses FIFO?",
                "Stack",
                "Array",
                "Tree",
                "Queue",
                'D'
        ));
        quiz.addQuestion(new Question(
                "What is the binary of decimal 5?",
                "101",
                "111",
                "100",
                "110",
                'A'
        ));
        SwingUtilities.invokeLater(() -> new QuizUI(quiz));

}}