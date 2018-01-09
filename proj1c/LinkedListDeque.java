public class LinkedListDeque<Item> implements Deque<Item>{
	private class GNode{
		public Item item;
		public GNode next;
		public GNode prev;

		public GNode(Item i, GNode n, GNode p){
			prev = p;
			item = i;
			next = n;
		}
	}

	private GNode sentinel;
	private int size;

	// Create an empty list
	public LinkedListDeque(){
		sentinel = new GNode(null,sentinel,sentinel);
		size = 0;
	}

	@Override
	public void addFirst(Item item){
		if (size == 0){
			GNode newFirstNode = new GNode(item,sentinel,sentinel);
			sentinel.next = newFirstNode;
			sentinel.prev = newFirstNode;
			size += 1;
		}
		else{
			GNode oldFirstNode = sentinel.next;
			GNode newFirstNode = new GNode(item,oldFirstNode,sentinel);
			oldFirstNode.prev = newFirstNode;
			sentinel.next = newFirstNode;
			size += 1;
		}
	}

	@Override
	public void addLast(Item item){
		if (size == 0){
			GNode newLastNode = new GNode(item,sentinel,sentinel);
			sentinel.next = newLastNode;
			sentinel.prev = newLastNode;
			size += 1;
		}
		else{
			GNode oldLastNode = sentinel.prev;
			GNode newLastNode = new GNode(item,sentinel,oldLastNode);
			oldLastNode.next = newLastNode;
			sentinel.prev = newLastNode;
			size += 1;
		}
	}

	@Override
	public boolean isEmpty(){
		if (size == 0){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int size(){
		return size;
	}

	@Override
	public void printDeque(){
		int index = 0;
		GNode dummy = new GNode(null,sentinel.next,sentinel.prev);
		while (index < size){
			System.out.println(dummy.next.item);
			dummy = dummy.next;
			index = index + 1;
		}
	}

	@Override
	public Item removeFirst(){
		if (size == 0){
			return null;
		}
		if (size == 1){
			Item output = sentinel.next.item;
			sentinel.next = sentinel;
			sentinel.prev = sentinel;
			size = 0;
			return output;
		}
		else{
			Item output = sentinel.next.item;
			GNode oldFirstNode = sentinel.next;
			GNode newFirstNode = sentinel.next.next;
			sentinel.next = newFirstNode;
			newFirstNode.prev = sentinel;
			size -= 1;;
			return output;
		}
	}

	@Override
	public Item removeLast(){
		if (size == 0){
			return null;
		}
		if (size == 1){
			Item output = sentinel.next.item;
			sentinel.next = sentinel;
			sentinel.prev = sentinel;
			size = 0;
			return output;
		}
		else{
			Item output = sentinel.prev.item;
			GNode oldLastNode = sentinel.prev;
			GNode newLastNode = sentinel.prev.prev;
			sentinel.prev = newLastNode;
			newLastNode.next = sentinel;
			size -= 1;
			return output;
		}
	}

	@Override
	public Item get(int index){
		if (index >= size){
			return null;
		}
		GNode dummy = new GNode(null,sentinel.next,sentinel.prev);
		int current = 0;
		while (current <= index){
			dummy = dummy.next;
			current += 1;
		}
		return dummy.item;
	}

	private GNode accessR(int index, GNode dummy){
		if (index == 0){
			return dummy.next;
		}
		if (dummy.next == null){
			return null;
		}
		else{
			dummy = dummy.next;
			return accessR(index - 1,dummy);

		}
	}
	public Item getRecursive(int index){
		GNode dummy = sentinel;
		return accessR(index,dummy).item;
	}
}