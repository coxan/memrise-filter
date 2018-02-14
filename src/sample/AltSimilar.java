package sample;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AltSimilar implements Runnable {
    private List<Word> primaryList;
    private List<List<Word>> secondaryLists;
    private File saveFile;

    public AltSimilar(List<Word> primaryList, List<List<Word>> secondaryLists, File saveFile) {
        this.primaryList = primaryList;
        this.secondaryLists = secondaryLists;
        this.saveFile = saveFile;
    }

    @Override
    public void run() {
        //making inital N2-arranged hashtable
        TreeMap<Integer, Hashtable<String, List<Word>>> output = new TreeMap<>();
        primaryList.forEach(word -> {
            if (output.containsKey(word.getPage())){
                if (output.get(word.getPage()).containsKey(word.getWord())){
                    output.get(word.getPage()).get(word.getWord()).add(word);
                }else{
                    List<Word> identicals = new ArrayList<>();
                    identicals.add(word);
                    output.get(word.getPage()).put(word.getWord(), identicals);
                }
            }else{
                List<Word> identicals = new ArrayList<>();
                identicals.add(word);
                Hashtable<String, List<Word>> identicalsTable = new Hashtable<>();
                identicalsTable.put(word.getWord(), identicals);
                output.put(word.getPage(), identicalsTable);
            }
        });

        // output.forEach((k, v) -> v.forEach((key, value) -> value.forEach(w -> System.out.println(w.getPage()))));

        // primaryList.forEach(word -> System.out.println(word.getPage()));

        //adding identicals from other lists
        for (List<Word> wordList : secondaryLists) {
            for (Word word : wordList) {
                for (Hashtable<String, List<Word>> x : output.values()){
                    if (x.containsKey(word.getWord())){
                        x.get(word.getWord()).add(word);
                    }
                }
            }
        }

        //removing unique entries
        output.forEach((k, v) -> v.entrySet().removeIf(x -> x.getValue().size() == 0));

        //converting the lists into a more readable format
        List<String> fileOutput = new ArrayList<>();
        for (String word : list1.keySet()){
            fileOutput.add(String.format("\n\nWord: %s (#%s)", word, list1.get(word).indexOf(word)+1));
            for (Word w : list1.get(word)){
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
                            list1.get(word).indexOf(w)+1,
                            w.getPage(),
                            formatText,
                            w.getMode(),
                            w.getDef()
                    ));
                }else{
                    fileOutput.add(String.format(
                            "Identical Occurrence %s. in page %s (%s - %s) of N%s course\nMeaning: %s\n",
                            list1.get(word).indexOf(w)+1,
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
