package sample;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Data{
    private List<Word> list;
    private int increment, page, range1, range2, mode;
    private boolean pronunciation;
    private String altFormat;

    public Data(int mode){
        list = new ArrayList<>();
        page = 1;
        range1 = 1;
        increment = 25;
        pronunciation = false;

        this.mode = mode;
        switch (this.mode){
            case 2:
                increment = 15;
                altFormat = AltFormats.N2.getFormat();
                break;
            case 3:
                increment = 15;
                altFormat = AltFormats.N3.getFormat();
                break;
            case 4:
                altFormat = AltFormats.N4.getFormat();
                break;
            case 5:
                page = 2;
                altFormat = AltFormats.N5.getFormat();
                break;
        }

        range2 = increment;
    }

    public void append(List<String> someArray){
        if (page > 100 && mode == 3){
            altFormat = AltFormats.N2.getFormat();
        }

        if (someArray.size() != 1 && someArray.get(0).contains("")) {
            int offset = someArray.size() % 2;
            // int offset = 0;
            for (int i = 0; i < someArray.size() - offset; i += 2){
                String w = someArray.get(i);
                String def = someArray.get(i+1);
                Word word = new Word(page, range1, range2, mode, w, def, pronunciation);
                list.add(word);
            }
        }

        page++;
        if (pronunciation || mode == 1) {
            range1 += increment;
            range2 += increment;
            pronunciation = false;
        }else{
            pronunciation = true;
        }
    }

    public void delete(){

        //decrement values
        if (page == 1 && mode != 5){
            return;
        }else if (page == 2 && mode == 5){
            return;
        }
        page--;

        if (mode == 1){
            range1 -= increment;
            range2 -= increment;
        }else if (pronunciation){ // && mode != 1
            pronunciation = false;
        }else{ // if !pronunciation && mode != 1
            pronunciation = true;
            range1 -= increment;
            range2 -= increment;
        }

        //remove entries
        list.removeIf(w -> w.getPage() == page);
    }

    public void save(File saveFile){

        //lines to be written to file
        List<String> lines = new ArrayList<>();

        for (Word x : list) {
            lines.add(String.format(
                    "%s\n%s\n%s\n%s\n%s\n%s\n%s",
                    x.getPage(),
                    x.getRange1(),
                    x.getRange2(),
                    x.getMode(),
                    x.getWord(),
                    x.getDef(),
                    x.ispronunciation()
            ));
        }

        try {
            FileUtils.writeLines(saveFile, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(File savefile){
        //reset all stats
        list.clear();
        page = 1;
        range1 = 1;
        increment = 25;
        pronunciation = false;
        switch (mode){
            case 2:
                increment = 15;
                altFormat = AltFormats.N2.getFormat();
                break;
            case 3:
                increment = 15;
                altFormat = AltFormats.N3.getFormat();
                break;
            case 4:
                altFormat = AltFormats.N4.getFormat();
                break;
            case 5:
                page = 2;
                altFormat = AltFormats.N5.getFormat();
                break;
        }
        range2 = increment;

        //re-add everything
        List<String> lines = new ArrayList<>();

        try {
            lines.addAll(FileUtils.readLines(savefile, Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        int wordCount = 0;
        for (int i = 0; i < lines.size(); i += 7){
            list.add(new Word(
                    Integer.valueOf(lines.get(i)),
                    Integer.valueOf(lines.get(i+1)),
                    Integer.valueOf(lines.get(i+2)),
                    Integer.valueOf(lines.get(i+3)),
                    lines.get(i+4),
                    lines.get(i+5),
                    Boolean.valueOf(lines.get(i+6))
                    )
            );
            wordCount++;
        }
        System.out.println(String.format("%s words loaded.", wordCount));

        page = list.get(list.size()-1).getPage()+1;
        range1 = list.get(list.size()-1).getRange1();
        range2 = list.get(list.size()-1).getRange2();
        pronunciation = list.get(list.size()-1).ispronunciation();

        if (mode != 1)
            pronunciation = !pronunciation;

        if (!pronunciation){
            range1 += increment;
            range2 += increment;
        }
    }

    public int getPage() {
        return page;
    }

    public int getRange1() {
        return range1;
    }

    public int getRange2() {
        return range2;
    }

    public boolean ispronunciation() {
        return pronunciation;
    }

    public String getAltFormat() {
        return altFormat;
    }

    public List<Word> getList() {
        return list;
    }

    public void setPronunciation(boolean pronunciation) {
        this.pronunciation = pronunciation;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setRange1(int range1) {
        this.range1 = range1;
    }

    public void setRange2(int range2) {
        this.range2 = range2;
    }
}
