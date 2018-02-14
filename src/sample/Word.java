package sample;

public class Word {
    private int page, range1, range2, mode;
    private String word, def;
    private boolean pronunciation;

    public Word(int page, int range1, int range2, int mode, String word, String def, boolean pronunciation){
        this.page = page;
        this.range1 = range1;
        this.range2 = range2;
        this.mode = mode;
        this.word = word;
        this.def = def;
        this.pronunciation = pronunciation;
    }

    public String getWord() {
        return word;
    }

    public String getDef() {
        return def;
    }

    public int getRange1() {
        return range1;
    }

    public int getRange2() {
        return range2;
    }

    public int getMode() {
        return mode;
    }

    public int getPage() {
        return page;
    }

    public boolean ispronunciation() {
        return pronunciation;
    }
}
