package lab8;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private Node root;
    private int size;
    private Set<K> keySet;

    private class Node {
        private K key;
        private V val;
        private Node left;
        private Node right;

        public Node(K key, V val) {
            this.key = key;
            this.val = val;
        }

        public Node() {

        }
    }

    public BSTMap() {
        size = 0;
        root = new Node();
        keySet = new TreeSet<K>();
    }

    public BSTMap(K key, V val) {
        root = new Node(key, val);
        keySet = new TreeSet<K>();
        keySet.add(key);
        size = 1;
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
        keySet.clear();
    }

    public Iterator<K> iterator() {
        return keySet.iterator();
    }


    private void put(Node n, K key, V val) {
        int cmp = key.compareTo(n.key);
        if (cmp > 0) {
            if (n.right == null) {
                n.right = new Node(key, val);
                keySet.add(key);
                size += 1;
            } else {
                put(n.right, key, val);
            }
        } else if (cmp < 0) {
            if (n.left == null) {
                n.left = new Node(key, val);
                keySet.add(key);
                size += 1;
            } else {
                put(n.left, key, val);
            }
        } else {
            n.val = val;
        }
    }

    @Override
    public void put(K key, V val) {
        
        if (size == 0) {
            root.key = key;
            root.val = val;
            size = 1;
            return;
        } else {
            put(root, key, val);
        }
    }


    private V get(Node n, K key) {
        int cmp = key.compareTo(n.key);
        if (cmp == 0) {
            return n.val;
        } else if (cmp > 0) {
            if (n.right == null) {
                return null;
            } else {
                return get(n.right, key);
            }
        } else {
            if (n.left == null) {
                return null;
            } else {
                return get(n.left, key);
            }
        }
    }

    @Override
    public V get(K key) {
        if (size == 0) {
            return null;
        }
        return get(root, key);
    }

    @Override
    public int size() {
        return size;
    }


    private boolean containsKey(Node n, K key) {
        int cmp = key.compareTo(n.key);
        if (cmp == 0) {
            return true;
        } else if (cmp > 0) {
            if (n.right == null) {
                return false;
            } else {
                return containsKey(n.right, key);
            }
        } else {
            if (n.left == null) {
                return false;
            } else {
                return containsKey(n.left, key);
            }
        }
    }

    @Override
    public boolean containsKey(K key) {
        if (size == 0) {
            return false;
        }
        return containsKey(root, key);
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        Iterator<K> setIterator = keySet.iterator();
        
        while (setIterator.hasNext()) {
            K key = setIterator.next();
            System.out.print(get(key) + " ");
        }
    }
}
