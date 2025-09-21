public class ArrayQueue<T> {
    private Object[] data;
    private int head, tail, size;

    public ArrayQueue() { this(1024); }
    public ArrayQueue(int capacity) {
        data = new Object[capacity];
        head = 0;
        tail = 0;
        size = 0;
    }

    public void enqueue(T item) {
        if (size == data.length) {
            Object[] n = new Object[data.length * 2];
            for (int i = 0; i < size; i++) n[i] = data[(head + i) % data.length];
            data = n;
            head = 0;
            tail = size;
        }
        data[tail] = item;
        tail = (tail + 1) % data.length;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (size == 0) throw new RuntimeException("Queue vazia");
        T item = (T) data[head];
        data[head] = null;
        head = (head + 1) % data.length;
        size--;
        return item;
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
}
