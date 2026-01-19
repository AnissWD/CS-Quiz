public class Question {
    private String Answer1, Answer2, Answer3, Answer4;
    public Question(String Answer1, String Answer2, String Answer3, String Answer4){
        this.Answer1=Answer1;
        this.Answer2=Answer2;
        this.Answer3=Answer3;
        this.Answer4=Answer4;
    }

    public void setAnswer1(String answer1) {
        Answer1 = answer1;
    }

    public void setAnswer2(String answer2) {
        Answer2 = answer2;
    }

    public void setAnswer3(String answer3) {
        Answer3 = answer3;
    }

    public void setAnswer4(String answer4) {
        Answer4 = answer4;
    }

    public String getAnswer1() {
        return Answer1;
    }

    public String getAnswer2() {
        return Answer2;
    }

    public String getAnswer3() {
        return Answer3;
    }

    public String getAnswer4() {
        return Answer4;
    }

    @Override
    public String toString() {
        return String.format("A) %s \nB) %s \nC) %s \nD) %s",Answer1, Answer2, Answer3, Answer4);
    }
}
