package sample;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class Similar implements Runnable{
//    private List<Word> words;
//    private List<SimilarPair> pairs;
//
//    public Similar(List<Word> words){
//        this.words = words;
//
//        //calculate pairs
//        for (Word x : words){
//            String word1 = x.getWord();
//            for (int i = 1; i < words.size(); i++){
//                String word2 = words.get(i).getWord();
//                if (word1.contains(word2)){
//                }
//            }
//
//
//        }
//    }
    private List<List<Word>> lists;
    private File saveFile;

    public Similar(List<List<Word>> lists, File saveFile){
        this.lists = lists;
        this.saveFile = saveFile;
    }

    //find identical words
    @Override
    public void run() {
        Hashtable<CharSequence, List<Word>> pairings = new Hashtable<>();
        List<List<Word>> pairs = new ArrayList<>();

        for (List<Word> l : lists){
            for (Word w : l){
                if (!pairings.containsKey(w.getWord())){
                    //unique word
                    List<Word> identicals = new ArrayList<>();
                    identicals.add(w);
                    pairings.put(w.getWord(), identicals);
                }else{
                    //identical word
                    pairings.get(w.getWord()).add(w);
                }
            }
        }

        pairings.entrySet().removeIf(x -> x.getValue().size() < 2);
        pairings.entrySet().removeIf(x -> {
            for (Word word : x.getValue()){
                if (word.getMode() == 2){
                    return false;
                }
            }
            return true;
        });

        //add lists of lists of identical words into pairs list
        for (CharSequence key : pairings.keySet()){
            if (pairings.get(key).size() > 1){
                pairs.add(pairings.get(key));
            }
        }

        //converting the lists into a more readable format
        List<String> fileOutput = new ArrayList<>();
        for (List<Word> list : pairs){
            fileOutput.add(String.format("\n\nWord: %s (#%s)", list.get(0).getWord(), pairs.indexOf(list)+1));
            for (Word w : list){
                if (w.ispronunciation()){
                    String format;
                    switch (w.getMode()){
                        case 2:
                            format = AltFormats.N2.getFormat();
                            break;
                        case 3:
                            format = AltFormats.N3.getFormat();
                            break;
                        case 4:
                            format = AltFormats.N4.getFormat();
                            break;
                        case 5:
                            format = AltFormats.N5.getFormat();
                            break;
                        default:
                            format = "";
                    }
                    String formatText = String.format(format, w.getRange1(), w.getRange2());

                    fileOutput.add(String.format(
                            "Identical Occurrence %s. in page %s (%s) of N%s course\nMeaning: %s\n",
                            list.indexOf(w)+1,
                            w.getPage(),
                            formatText,
                            w.getMode(),
                            w.getDef()
                    ));
                }else{
                    fileOutput.add(String.format(
                            "Identical Occurrence %s. in page %s (%s - %s) of N%s course\nMeaning: %s\n",
                            list.indexOf(w)+1,
                            w.getPage(),
                            w.getRange1(),
                            w.getRange2(),
                            w.getMode(),
                            w.getDef()
                    ));
                }
            }
        }

        try {
            FileUtils.writeLines(saveFile, fileOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
