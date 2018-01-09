public class ArrayDeque<Item>{
	private Item[] items;
	private int size;
	private int frontPos;

	public ArrayDeque(){
		items = (Item[]) new Object[8];
		frontPos = 0;
		size = 0;

	}

	private void upResize(){
		int numBoxesRemaining = items.length-frontPos;
		if (size >= items.length){
			Item[] next = (Item[]) new Object[2*size];
			java.lang.System.arraycopy(items,frontPos,next,0,items.length-frontPos);
			if (size > numBoxesRemaining){
				java.lang.System.arraycopy(items,0,next,numBoxesRemaining,size-numBoxesRemaining);
			}
			frontPos = 0;
			items = next;
		}
	}

	private void downResize(){
		double factor = items.length / 4;
		int newLength = (int) Math.ceil(items.length/2);
		int numBoxesRemaining = items.length-frontPos;
		if (items.length >= 16){
			if (size < factor){
				Item[] next = (Item[]) new Object[newLength];
				if (size <= numBoxesRemaining){
					java.lang.System.arraycopy(items,frontPos,next,0,size);
				}
				if (size > numBoxesRemaining){
					java.lang.System.arraycopy(items,frontPos,next,0,numBoxesRemaining);
					java.lang.System.arraycopy(items,0,next,numBoxesRemaining,size-numBoxesRemaining);
				}
				frontPos = 0;
				items = next;
				
			}
		}
	}

	public void addFirst(Item input){
		this.upResize();
		size += 1;
		frontPos = (items.length+frontPos - 1)%items.length;
		items[frontPos] = input;
	}

	public void addLast(Item input){
		this.upResize();
		items[(frontPos + size)%items.length] = input;
		size += 1;
	}

	public boolean isEmpty(){
		return size == 0;
	}

	public int size(){
		return size;
	}

	public void printDeque(){
		int index = frontPos;
		if (size == 0){
			return;
		}
		if (size == 1){
			System.out.println(items[frontPos]);
		}
		else{
			while (index != (items.length+index-1)%items.length){
				if (items[index] == null){
					return;
				}
				else{
					System.out.println(items[index]);
					index = (items.length+index+1)%items.length;
				}
			}
			return;
		}
	}

	public Item removeFirst(){
		Item output = items[frontPos];
		frontPos = (items.length+frontPos+1)%items.length;
		size -= 1;
		this.downResize();
		return output;
	}

	public Item removeLast(){
		Item output = items[(items.length+frontPos+size-1)%items.length];
		size -= 1;
		this.downResize();
		return output;
	}
	
	public Item get(int index){
		if (index >= size){
			return null;
		}
		else{
			return items[(items.length+frontPos+index)%items.length];
		}
	}
}