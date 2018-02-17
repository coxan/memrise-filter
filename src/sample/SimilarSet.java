package sample;

import java.util.*;

public class SimilarSet {
    private String wordString;
    private List<Word> identicals;
    private boolean unique;

    public SimilarSet(String wordString, List<Word> identicals){
        this.wordString = wordString;
        this.identicals = identicals;

        for (int i = 0; i < identicals.size(); i++) {
            // other identicals def word split list
            List<String> list = new ArrayList<>();
            for (Word identical : identicals) {
                if (identicals.indexOf(identical) != i){
                    list.addAll(Arrays.asList(identical.getDef().split(" ")));
                }
            }

            // identical def word split list
            for (String x : identicals.get(i).getDef().split(" ")){
                if (list.contains(x)){
                    unique = false;
                    return;
                }
            }
        }
    }

    public boolean isUnique() {
        return unique;
    }

    public String getWordString() {
        return wordString;
    }

    public List<Word> getIdenticals() {
        return identicals;
    }
}
