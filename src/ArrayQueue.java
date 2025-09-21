/*
Implementação de uma fila genérica - estrutura FIFO
 */

public class ArrayQueue<T> {

    // Array que armazena os elementos da fila
    private Object[] data;

    // head - índice do primeiro elemento da fila (frente)
    // tail - índice da próxima posição livre para inserir (final)
    // size - quantidade atual de elementos na fila
    private int head, tail, size;

    public ArrayQueue() { this(1024); }  // capacidade inicial da fila - 1024
    public ArrayQueue(int capacity) {
        data = new Object[capacity]; // cria o espeço para a fila
        head = 0;
        tail = 0;
        size = 0;
    }

    // Metodo para adicionar um elemento no final da fila
    public void enqueue(T item) {

        // Se a fila estiver cheia, cria um array novo com o dobro do tamanho
        if (size == data.length) {
            Object[] n = new Object[data.length * 2];

            // Copia os elementos para o novo array, mantendo a ordem correta
            for (int i = 0; i < size; i++) n[i] = data[(head + i) % data.length];
            data = n;
            head = 0;
            tail = size;
        }
        // Insere o novo elemento na posição tail
        data[tail] = item;

        // Avança o tail (com % para "dar a volta" caso chegue ao final do array)
        tail = (tail + 1) % data.length;
        size++; // Aumenta o contador de elementos
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {

        // Se a fila estiver vazia, não é possível remover
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
