package ui;

public class Rect {

    public int left, top, right, bottom, size;

    public Rect() {
        this.left = 0;
        this.top = 0;
        this.right = 0;
        this.bottom = 0;
        size = 0;
    }

    public boolean contains(int x, int y) {
        return x >= left & x < right && y >= top && y < bottom;
    }

    public void setRect(int left, int top, int size) {
        this.left = left;
        this.top = top;
        this.right = left + size;
        this.bottom = top + size;
        this.size = size;
    }

}
