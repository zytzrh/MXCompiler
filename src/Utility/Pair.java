package Utility;

public class Pair<FirstType, SecondType> {
    private FirstType first;
    private SecondType second;

    public Pair(FirstType first, SecondType second) {
        this.first = first;
        this.second = second;
    }

    public FirstType getFirst() {
        return first;
    }

    public SecondType getSecond() {
        return second;
    }

    public void setFirst(FirstType first) {
        this.first = first;
    }

    public void setSecond(SecondType second) {
        this.second = second;
    }
}