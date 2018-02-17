package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class Main extends Application {
    private Text currentPage, currentRange;
    private ToggleGroup toggleGroup;
    private RadioButton n5, n4, n3, n2, n1;
    private Button append, appendBlank, override, load, save, filter, altFilter, delete, defaultLoad;
    private TextField overridePage, overrideRange1, overrideRange2, overridePronunciation;
    private TextArea textArea;
    private Data dataN1, dataN2, dataN3, dataN4, dataN5, currentData;

    @Override
    public void start(Stage primaryStage) throws Exception{
        dataN1 = new Data(1);
        dataN2 = new Data(2);
        dataN3 = new Data(3);
        dataN4 = new Data(4);
        dataN5 = new Data(5);
        currentPage = new Text("Page: Not Set");
        currentPage.setFont(new Font(15));
        currentRange = new Text("Range: Not Set");
        currentRange.setFont(new Font(15));
        toggleGroup = new ToggleGroup();
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton selected = (RadioButton) toggleGroup.getSelectedToggle();
            switch (selected.getText()){
                case "N1":
                    currentData = dataN1;
                    break;
                case "N2":
                    currentData = dataN2;
                    break;
                case "N3":
                    currentData = dataN3;
                    break;
                case "N4":
                    currentData = dataN4;
                    break;
                case "N5":
                    currentData = dataN5;
                    break;
            }

            updateStats();

        });
        n5 = new RadioButton("N5");
        n5.setToggleGroup(toggleGroup);
        n5.setFont(new Font(15));

        n4 = new RadioButton("N4");
        n4.setToggleGroup(toggleGroup);
        n4.setFont(new Font(15));

        n3 = new RadioButton("N3");
        n3.setToggleGroup(toggleGroup);
        n3.setFont(new Font(15));

        n2 = new RadioButton("N2");
        n2.setToggleGroup(toggleGroup);
        n2.setFont(new Font(15));

        n1 = new RadioButton("N1");
        n1.setToggleGroup(toggleGroup);
        n1.setFont(new Font(15));

        append = new Button("Append");
        append.setOnMouseClicked(event -> {
            List<String> appendList = new ArrayList<>();
            for (CharSequence x : textArea.getParagraphs()){
                appendList.add(x.toString());
            }

            currentData.append(appendList);
            updateStats();
            textArea.clear();
        });
        appendBlank = new Button("Append Blank");

        load = new Button("Load...");
        load.setOnMouseClicked(event -> {
            FileChooser chooser = new FileChooser();
            //chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("TXT Files (.txt)", "txt"));
            File saveFile = chooser.showOpenDialog(primaryStage);
            if (saveFile != null) {
                currentData.load(saveFile);
                updateStats();
                textArea.clear();
            }
        });

        Button test = new Button("directory test");
        test.setOnMouseClicked(event -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            System.out.println(dirChooser.showDialog(primaryStage));
        });

        defaultLoad = new Button("Load All");
        defaultLoad.setOnMouseClicked(event -> {
            n1.setSelected(true);
            currentData.load(new File("/Users/gnotlasers/Downloads/13232/jlpt n1"));
            n2.setSelected(true);
            currentData.load(new File("/Users/gnotlasers/Downloads/13232/jlpt n2"));
            n3.setSelected(true);
            currentData.load(new File("/Users/gnotlasers/Downloads/13232/jlpt n3"));
            n4.setSelected(true);
            currentData.load(new File("/Users/gnotlasers/Downloads/13232/jlpt n4"));
            n5.setSelected(true);
            currentData.load(new File("/Users/gnotlasers/Downloads/13232/jlpt n5"));

            updateStats();
        });

        save = new Button("Save...");
        save.setOnMouseClicked(event -> {
            FileChooser chooser = new FileChooser();
            //chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("TXT Files (.txt)", "txt"));
            File saveFile = chooser.showSaveDialog(primaryStage);
            if (saveFile != null) {
                currentData.save(saveFile);
            }
        });

        filter = new Button("Filter");
        filter.setOnMouseClicked(event -> {
            List<List<Word>> lists = new ArrayList<>(Arrays.asList(
                    dataN1.getList(),
                    dataN2.getList(),
                    dataN3.getList(),
                    dataN4.getList(),
                    dataN5.getList()
            ));
            File saveFile = new FileChooser().showSaveDialog(primaryStage);
            Runnable similar = new Similar(lists, saveFile);
            Thread thread = new Thread(similar, "Similar Words Thread");
            thread.setDaemon(true);
            thread.start();
        });

        altFilter = new Button("Alt. Filter");
        altFilter.setOnMouseClicked(event -> {
            List<List<Word>> lists = new ArrayList<>(Arrays.asList(
                    dataN1.getList(),
                    dataN3.getList(),
                    dataN4.getList(),
                    dataN5.getList()
            ));

            File saveFile = new FileChooser().showSaveDialog(primaryStage);
            Runnable altSimilar = new AltSimilar(dataN2.getList(), lists, saveFile);
            Thread thread = new Thread(altSimilar, "Alt. Similar Words Thread");
            thread.setDaemon(true);
            thread.start();
        });

        delete = new Button("Delete");
        delete.setTooltip(new Tooltip("Removes previous entry"));
        delete.setOnMouseClicked(event -> {
            currentData.delete();
            updateStats();
        });
        
        overridePage = new TextField();
        overridePage.setPromptText("Page");
        overrideRange1 = new TextField();
        overrideRange1.setPromptText("Range1");
        overrideRange2 = new TextField();
        overrideRange2.setPromptText("Range2");
        overridePronunciation = new TextField();
        overridePronunciation.setPromptText("isPronunciation");

        override = new Button("Override");
        override.setOnMouseClicked(event -> {
            if (!overridePage.getText().isEmpty()) {
                currentData.setPage(Integer.valueOf(overridePage.getText()));
                overridePage.clear();
            }
            if (!overrideRange1.getText().isEmpty()) {
                currentData.setRange1(Integer.valueOf(overrideRange1.getText()));
                overrideRange1.clear();
            }
            if (!overrideRange2.getText().isEmpty()) {
                currentData.setRange2(Integer.valueOf(overrideRange2.getText()));
                overrideRange2.clear();
            }
            if (!overridePronunciation.getText().isEmpty()) {
                currentData.setPronunciation(Boolean.valueOf(overridePronunciation.getText()));
                overridePronunciation.clear();
            }
            updateStats();
        });

        textArea = new TextArea();
        textArea.setMinSize(500, 500);
        FlowPane root = new FlowPane(20, 20);
        root.getChildren().addAll(
                currentPage, currentRange, 
                n1, n2, n3, n4, n5, 
                // append, delete, load, save, filter, altFilter, test,
                altFilter, defaultLoad,
                overridePage, overrideRange1, overrideRange2, overridePronunciation, override, 
                textArea
        );
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setAlignment(Pos.CENTER);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 750, 700));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void updateStats(){
        currentPage.setText(String.format("Page %s", currentData.getPage()));
        if (currentData.ispronunciation()){
            currentRange.setText(String.format(currentData.getAltFormat(), currentData.getRange1(), currentData.getRange2()));
        }else{
            currentRange.setText(String.format("%s - %s", currentData.getRange1(), currentData.getRange2()));
        }
    }

}
