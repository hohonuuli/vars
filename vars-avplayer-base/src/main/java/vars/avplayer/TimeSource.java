package vars.avplayer;

public enum TimeSource {
    TIMECODETRACK("Time-code Track"),
    RUNTIME("Elapsed Time"),
    AUTO("Automatic");

    private String description;

    TimeSource(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

}
