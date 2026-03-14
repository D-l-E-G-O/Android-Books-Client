package p42.androidbooksclient;

public class Tag {
    private final int id;
    private final String name;

    public Tag(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}
