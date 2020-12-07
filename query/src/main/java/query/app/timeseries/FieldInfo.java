package query.app.timeseries;

class FieldInfo {
    public final String name;
    public final int index;

    FieldInfo(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
