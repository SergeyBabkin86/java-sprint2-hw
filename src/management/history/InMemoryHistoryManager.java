package management.history;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public Node<Task> first;
    public Node<Task> last;
    public int size = 0;

    private final HashMap<Integer, Node<Task>> nodeList = new HashMap<>(); // Добавляем в хэшмапю

    void linkLast(Task task) {
        final Node<Task> last = this.last;
        final Node<Task> newNode = new Node<>(last, task, null);
        this.last = newNode;
        if (last == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        nodeList.put(task.getId(), newNode); // Добавляем ноду в hashMap
        size++;
    }

    void removeNode(Node<Task> node) {
        final Node<Task> next = node.next;
        final Node<Task> prev = node.prev;
        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }
        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        node.item = null;
        size--;
    }

    @Override
    public void add(Task task) {
        if (nodeList.containsKey(task.getId())) {
            removeNode(nodeList.get(task.getId()));
            nodeList.remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = nodeList.get(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        Node<Task> node = first;
        List<Task> nodeList = new ArrayList<>();
        while (node != null) {
            nodeList.add(node.item);
            node = node.next;
        }
        return nodeList;
    }
}