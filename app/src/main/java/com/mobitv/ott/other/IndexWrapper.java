package com.mobitv.ott.other;

public class IndexWrapper {
    private int start;
    private int end;

    public IndexWrapper() {
    }

    public IndexWrapper(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
