public class Question {

    private String text;
    private String Answer1, Answer2, Answer3, Answer4;
    private char correct;

    public Question(String text, String Answer1, String Answer2,
                    String Answer3, String Answer4, char correct) {

        this.text = text;
        this.Answer1 = Answer1;
        this.Answer2 = Answer2;
        this.Answer3 = Answer3;
        this.Answer4 = Answer4;
        this.correct = correct;
    }

    public String getText() {
        return text;
    }

    public char getCorrect() {
        return correct;
    }

    @Override
    public String toString() {
        return String.format(
                "%s\nA) %s\nB) %s\nC) %s\nD) %s",
                text, Answer1, Answer2, Answer3, Answer4
        );
    }
}
