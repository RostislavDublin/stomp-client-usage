package other;

public class Say {
    private StringBuilder phrase;

    public Say(){
        this("");
    }
    public Say(String firstLine){
        phrase = new StringBuilder(firstLine);
    }

    public Say addln(String nextLine){
        phrase.append("\n").append(nextLine);
        return this;
    }

    public void out(){
        System.out.println(phrase.toString());
        clean();
    }

    public void clean(){
        phrase.delete(0, phrase.length());
    }
}
