package lab9;

import java.util.LinkedList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

public class MyHashMap<K, V> implements Map61B<K, V> {
	protected LinkedList<Node>[] hashArray;
	protected int capacity;
	protected int size;
	protected final double load;
	protected double currentLoad;
	protected Set<K> keySet;

	public class Node {
		protected K k;
		protected V v;
		protected int hash;

		public Node(K key, V value) {
			k = key;
			v = value;
			hash = key.hashCode();
		}
	}

	public MyHashMap() {
		capacity = 16;
		size = 0;
		hashArray =  (LinkedList<Node>[]) new LinkedList<?>[capacity];
		load = 0.75;
		currentLoad = 0;

		int index = 0;
		while (index < capacity) {
			hashArray[index] = new LinkedList<Node>();
			index += 1;
		}
		keySet = new HashSet<K>(capacity, (float) load);
	}

	public MyHashMap(int initialSize) {
		if (initialSize < 1) {
			throw new IllegalArgumentException("MyHashMap must have positive size");
		}
		capacity = initialSize;
		size = 0;
		hashArray = (LinkedList<Node>[]) new LinkedList<?>[this.capacity];
		load = 0.75;
		currentLoad = 0;

		int index = 0;
		while (index < capacity) {
			hashArray[index] = new LinkedList<Node>();
			index += 1;
		}
		keySet = new HashSet<K>(capacity, (float) load);
	}

	public MyHashMap(int initialSize, double loadFactor) {
		if (initialSize < 1) {
			throw new IllegalArgumentException("MyHashMap must have positive size");
		}
		if (loadFactor <= 0) {
			throw new IllegalArgumentException("MyHashMap must have positive load");
		}
		capacity = initialSize;
		size = 0;
		hashArray = (LinkedList<Node>[]) new LinkedList<?>[this.capacity];
		load = loadFactor;
		currentLoad = 0;

		int index = 0;
		while (index < capacity) {
			hashArray[index] = new LinkedList<Node>();
			index += 1;
		}
		keySet = new HashSet<K>(capacity, (float) load);
	}

	public void clear() {
		int index = 0;
		while (index < hashArray.length) {
			hashArray[index].clear();
			index += 1;
		}
		size = 0;
		currentLoad = 0;
	}

	public void put(K key, V value) {
		if (currentLoad > load) {
			resize();
		}
		Node inserting = new Node(key, value);
		int insertIndex = inserting.hash % capacity;
		if (insertIndex < 0) {
			insertIndex += capacity;
		}
		ListIterator<Node> search = hashArray[insertIndex].listIterator(0);
		boolean containsKey = false;
		while (search.hasNext()) {
			Node inspect = search.next();
			if (inspect.k == key) {
				search.set(inserting);
				containsKey = true;
				break;
			}
		}
		if (!containsKey) {
			hashArray[insertIndex].addLast(inserting);
		}
		keySet.add(key);
		size += 1;
		currentLoad = ((double) size) / capacity;
	}

	public V get(K key) {
		int index = key.hashCode() % capacity;
		if (index < 0) {
			index += capacity;
		}
		ListIterator<Node> search = hashArray[index].listIterator(0);
		while (search.hasNext()) {
			Node inspect = search.next();
			if (key.equals(inspect.k)) {
				return inspect.v;
			}
		}
		return null;
	}

	public boolean containsKey(K key) {
		int index = key.hashCode() % capacity;
		if (index < 0) {
			index += capacity;
		}
		ListIterator<Node> search = hashArray[index].listIterator(0);
		while (search.hasNext()) {
			Node inspect = search.next();
			if (key.equals(inspect.k)) {
				return true;
			}
		}
		return false;
	}

	public int size() {
		return size;
	}

	public Set<K> keySet() {
		return keySet;
	}

	private void resize() {
		LinkedList<Node>[] newArray = (LinkedList<Node>[]) new LinkedList<?>[hashArray.length * 2];
		int populate = 0;
		while (populate < newArray.length) {
			newArray[populate] = new LinkedList<Node>();
			populate += 1;
		}
		int index = 0;
		while (index < hashArray.length) {
			ListIterator<Node> search = hashArray[index].listIterator(0);
			while (search.hasNext()) {
				Node moving = search.next();
				int newCode = moving.hash % (capacity * 2);
				if (newCode < 0) {
					newCode += (capacity * 2);
				}
				newArray[newCode].addLast(moving);
			}
			index += 1;
		}
		currentLoad = ((double) size) / newArray.length;
		capacity = newArray.length;
		hashArray = newArray;
	}

	public Iterator<K> iterator() {
		return keySet.iterator();
	}

	public V remove(K key) {
		throw new UnsupportedOperationException();
	}

	public V remove(K key, V value) {
		throw new UnsupportedOperationException();
	}
}
