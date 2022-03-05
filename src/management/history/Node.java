package management.history;

public class Node<T> {

    private Node<T> prev;
    private T item;
    private Node<T> next;

    public Node(Node<T> prev, T item, Node<T> next) {
        this.prev = prev;
        this.item = item;
        this.next = next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public T getItem() {
        return item;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }
}
