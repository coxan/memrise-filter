package sample;

public enum AltFormats {

    N2("%s - %s (Pronunciation)"),
    N3("Kanji for %S - %s"),
    N4("Readings of %S - %s"),
    N5("Readings for %s - %s");

    private final String format;

    AltFormats(String format){
        this.format = format;
    }

    public String getFormat() {
        return format;
    }
}
