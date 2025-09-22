public class ArrayStack<T> {
    private Object[] data;
    // Variável usada para indicar quantos elementos estão na pilha
    // e também a posição onde o próximo elemento será inserido.
    private int top;

    public ArrayStack() { this(1024); } // Cria uma pilha com capacidade inicial de 1024 elementos.

    // Cria o array com a capacidade inicial definida
    // e informa que a pilha começa vazia (top = 0).
    public ArrayStack(int capacity) {
        data = new Object[capacity];
        top = 0;
    }

    // Se o array estiver cheio, cria um novo array com o dobro do tamanho.
    // Copia os elementos do array antigo para o novo (System.arraycopy).
    // Atualiza a referência de 'data' para apontar para o novo array.
    public void push(T item) {
        if (top == data.length) {
            Object[] n = new Object[data.length * 2];
            System.arraycopy(data, 0, n, 0, data.length);
            data = n;
        }
        data[top++] = item;
    }

    // Verifica se a pilha está vazia e lança um erro caso esteja.
    // Caso contrário, decrementa 'top' e recupera o elemento removido.
    // Define a posição como null para liberar memória.
    @SuppressWarnings("unchecked")
    public T pop() {
        if (top == 0) throw new RuntimeException("Stack vazia");
        top--;
        T item = (T) data[top];
        data[top] = null;
        return item;
    }

    // Retorna true se a pilha não possui nenhum elemento.
    public boolean isEmpty() { return top == 0; }

    // Retorna a quantidade de elementos armazenados na pilha.
    public int size() { return top; }
}
