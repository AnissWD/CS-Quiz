import java.util.ArrayList;
import java.util.Scanner;

public class Quiz {

    private ArrayList<Question> questions = new ArrayList<>();
    private int score = 0;

    public void addQuestion(Question q) {
        questions.add(q);
    }

    public void start() {

        Scanner sc = new Scanner(System.in);

        for (Question q : questions) {

            System.out.println("----------------------------------");
            System.out.println(q);

            System.out.print("Your answer: ");
            char ans = Character.toUpperCase(sc.next().charAt(0));

            if (ans == q.getCorrect()) {
                System.out.println("Correct!");
                score++;
            } else {
                System.out.println("Wrong, Correct answer: " + q.getCorrect());
            }
        }

        System.out.println("\nFinal Score: " + score + "/" + questions.size());
    }
}
