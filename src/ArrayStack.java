public class ArrayStack<T> {
    private Object[] data;
    private int top;

    public ArrayStack() { this(1024); }
    public ArrayStack(int capacity) {
        data = new Object[capacity];
        top = 0;
    }

    public void push(T item) {
        if (top == data.length) {
            Object[] n = new Object[data.length * 2];
            System.arraycopy(data, 0, n, 0, data.length);
            data = n;
        }
        data[top++] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (top == 0) throw new RuntimeException("Stack vazia");
        top--;
        T item = (T) data[top];
        data[top] = null;
        return item;
    }

    public boolean isEmpty() { return top == 0; }
    public int size() { return top; }
}
