package management.history;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> first;
    private Node<Task> last;

    private final Map<Integer, Node<Task>> nodeList = new HashMap<>();

    void linkLast(Task task) {
        final Node<Task> last = this.last;
        final Node<Task> newNode = new Node<>(last, task, null);
        this.last = newNode;
        if (last == null) {
            first = newNode;
        } else {
            last.setNext(newNode);
        }
        nodeList.put(task.getId(), newNode);
    }

    void removeNode(Node<Task> node) {
        final Node<Task> next = node.getNext();
        final Node<Task> prev = node.getPrev();
        if (prev == null) {
            first = next;
        } else {
            prev.setNext(next);
            node.setPrev(null);
        }
        if (next == null) {
            last = prev;
        } else {
            next.setPrev(prev);
            node.setNext(null);
        }
        node.setItem(null);
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
        nodeList.remove(1);
    }

    @Override
    public List<Task> getHistory() {
        Node<Task> node = first;
        List<Task> nodeList = new ArrayList<>();
        while (node != null) {
            nodeList.add(node.getItem());
            node = node.getNext();
        }
        return nodeList;
    }
}