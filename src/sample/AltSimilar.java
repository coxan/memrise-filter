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
        List<List<Word>> lists = new ArrayList<>();
        lists.add(primaryList);
        lists.addAll(secondaryLists);

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

        List<SimilarSet> uniqueMeaning = makeSimilarSet(pairings, 2, false, false);
        uniqueMeaning = sortByList(uniqueMeaning, primaryList);

        List<SimilarSet> uniqueWords = makeSimilarSet(pairings, 2, true, false);
        uniqueWords = sortByList(uniqueWords, primaryList);
        System.out.println(uniqueWords.size());

//        //making inital N2-arranged hashtable
//        TreeMap<Integer, Hashtable<String, List<Word>>> output = new TreeMap<>();
//        primaryList.forEach(word -> {
//            if (output.containsKey(word.getPage())) {
//                if (output.get(word.getPage()).containsKey(word.getWord())) {
//                    output.get(word.getPage()).get(word.getWord()).add(word);
//                } else {
//                    List<Word> identicals = new ArrayList<>();
//                    identicals.add(word);
//                    output.get(word.getPage()).put(word.getWord(), identicals);
//                }
//            } else {
//                List<Word> identicals = new ArrayList<>();
//                identicals.add(word);
//                Hashtable<String, List<Word>> identicalsTable = new Hashtable<>();
//                identicalsTable.put(word.getWord(), identicals);
//                output.put(word.getPage(), identicalsTable);
//            }
//        });
//
//        // output.forEach((k, v) -> v.forEach((key, value) -> value.forEach(w -> System.out.println(w.getPage()))));
//
//        // primaryList.forEach(word -> System.out.println(word.getPage()));
//
//        //adding identicals from other lists
//        for (List<Word> wordList : secondaryLists) {
//            for (Word word : wordList) {
//                for (Hashtable<String, List<Word>> x : output.values()) {
//                    if (x.containsKey(word.getWord())) {
//                        x.get(word.getWord()).add(word);
//                    }
//                }
//            }
//        }
//
//        //removing identicals with at least one word of meaning in common
//        // output.forEach((k, v) -> v.entrySet().removeIf(x -> x.getValue().size() == 1));
//        System.out.println("Before removal");
//        final int[] identicalsCount = new int[2];
//        output.forEach((k, v) -> identicalsCount[0] += v.entrySet().size());
//        System.out.printf("Count: %s", identicalsCount[0]);
//
//        output.forEach((k, v) -> v.entrySet().removeIf(x -> x.getValue().size() > 1));
//        System.out.println("\nAfter removal");
//        output.forEach((k, v) -> identicalsCount[1] += v.entrySet().size());
//        System.out.printf("Count: %s", identicalsCount[1]);
//
//        //removing words with
//

        //converting the lists into a more readable format
        List<String> fileOutput = new ArrayList<>();
        fileOutput.add("Unique meaning, identical words");
        fileOutput.addAll(compileEntry(uniqueMeaning));
        fileOutput.add("\n\nUnique words from N2");
        fileOutput.addAll(compileEntry(uniqueWords));

        try {
            FileUtils.writeLines(saveFile, fileOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<SimilarSet> makeSimilarSet(Hashtable<CharSequence, List<Word>> pairings,
                                            int mode,
                                            boolean unique,
                                            boolean identicalMeaning){
        List<SimilarSet> result = new ArrayList<>();
        Hashtable<CharSequence, List<Word>> newInstance = new Hashtable<>(pairings);

        //remove non-N2 pairs
        if (mode != 0) {
            newInstance.entrySet().removeIf(x -> {
                for (Word word : x.getValue()) {
                    if (word.getMode() == mode) {
                        return false;
                    }
                }
                return true;
            });
        }

        //remove identical words : remove unique words
        if (unique) {
            newInstance.entrySet().removeIf(x -> x.getValue().size() > 1);
        }else{
            newInstance.entrySet().removeIf(x -> x.getValue().size() < 2);

        }
        //convert to SimilarSet objects
        newInstance.forEach((key, value) -> result.add(new SimilarSet(key.toString(), value)));
        //remove identical meaning words
        if (!identicalMeaning && !unique) {
            result.removeIf(x -> !x.isUnique());
        }

        return result;
    }
    
    private List<SimilarSet> sortByList(List<SimilarSet> input, List<Word> reference){
        //convert input list to a hashtable
        Hashtable<String, SimilarSet> hashtable = new Hashtable<>();
        input.forEach(x -> hashtable.put(x.getWordString(), x));
        
        //sort by reference list arrangement
        List<SimilarSet> outputSet = new ArrayList<>();
        for (Word x : reference){
            if (hashtable.keySet().contains(x.getWord())){
                outputSet.add(hashtable.get(x.getWord()));
            }
        }

        return outputSet;
    }

    private List<String> compileEntry(List<SimilarSet> similarSets){
        List<String> fileOutput = new ArrayList<>();

        for (SimilarSet x : similarSets){
            fileOutput.add(String.format("\n\nWord: %s\tPage: %s",
                    x.getWordString(),
                    x.getIdenticals().get(0).getPage()));

            for (Word w : x.getIdenticals()){
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
                            x.getIdenticals().indexOf(w)+1,
                            w.getPage(),
                            formatText,
                            w.getMode(),
                            w.getDef()
                    ));
                }else{
                    fileOutput.add(String.format(
                            "Identical Occurrence %s. in page %s (%s - %s) of N%s course\nMeaning: %s\n",
                            x.getIdenticals().indexOf(w)+1,
                            w.getPage(),
                            w.getRange1(),
                            w.getRange2(),
                            w.getMode(),
                            w.getDef()
                    ));
                }
            }
        }

        return fileOutput;
    }

}
