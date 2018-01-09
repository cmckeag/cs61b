import static org.junit.Assert.*;
import org.junit.Test;

public class TestArrayDeque1B{

	@Test 
	public void testArrayDeque(){
		StudentArrayDeque<Integer> test = new StudentArrayDeque<Integer>();
		assertEquals("test.size()",0,(int) test.size());
	}

	@Test 
	public void testAddFirst(){
		StudentArrayDeque<Integer> p = new StudentArrayDeque<Integer>();
		p.addFirst(3);
		p.addFirst(2);
		p.addFirst(1);
		//Test that the size gets increased
		assertEquals("p.addFirst(3)\np.addFirst(2)\np.addFirst(1)\np.size()",3,(int) p.size());
		//Test that the first element is 1
		assertEquals("p.addFirst(3)\np.addFirst(2)\np.addFirst(1)\np.get(0)",1,(int) p.get(0));
		//Adds 15 elements to the array.
		int i = 0;
		while (i < 15){
			p.addFirst(4);
			i += 1;
		}
		//Then makes sure the array is not broken.
		assertEquals("p.addFirst(3)\np.addFirst(2)\np.addFirst(1)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.addFirst(4)\np.get(17)",3,(int) p.get(17));
	}

	@Test 
	public void testAddLast(){
		StudentArrayDeque<Integer> p = new StudentArrayDeque<Integer>();
		p.addLast(1);
		p.addLast(2);
		p.addLast(3);
		//Test that the size gets increased
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.size()",3,(int) p.size());
		//Test that the last element is 3
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.get(0)",3,(int) p.get(2));
		//Adds 15 elements to the array.
		int i = 0;
		while (i < 15){
			p.addLast(4);
			i += 1;
		}
		//Then makes sure the array is not broken.
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.addLast(4)\np.get(17)",4,(int) p.get(17));
	}

	@Test 
	public void testEmpty(){
		StudentArrayDeque<Integer> p = new StudentArrayDeque<Integer>();
		assertEquals("p.isEmpty()",true,p.isEmpty());
		p.addFirst(1);
		assertEquals("p.addFirst(1)\np.isEmpty()",false,p.isEmpty());
		p.removeFirst();
		assertEquals("p.addFirst(1)\np.removeFirst()",true,p.isEmpty());
	}

	@Test 
	public void testRemoveFirst(){
		StudentArrayDeque<Integer> p = new StudentArrayDeque<Integer>();
		p.addLast(1);
		p.addLast(2);
		p.addLast(3);
		p.removeFirst();
		p.removeFirst();
		//Checks that the new first element is 3
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeFirst()\np.removeFirst()\np.get(0)",3,(int) p.get(0));
		//Checks that there are no elements after 3
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeFirst()\np.removeFirst()\np.get(1)",null,p.get(1));
		//Checks that the size got decreased
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeFirst()\np.removeFirst()\np.size()",1,(int) p.size());
		//Checks that the function is outputting the item it removes
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeFirst()\np.removeFirst()\np.removeFirst()",3,(int) p.removeFirst());
		//What happens when we remove from an empty deque?
		StudentArrayDeque<Integer> q = new StudentArrayDeque<Integer>();
		assertEquals("q.removeFirst()",null,q.removeFirst());
		assertEquals("q.removeFirst()\nq.size()",0,q.size());
	}

	@Test 
	public void testRemoveLast(){
		StudentArrayDeque<Integer> p = new StudentArrayDeque<Integer>();
		p.addLast(1);
		p.addLast(2);
		p.addLast(3);
		p.removeLast();
		p.removeLast();
		//Checks that the new first element is 1
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeFirst()\np.removeFirst()\np.get(0)",1,(int) p.get(0));
		//Checks that there are no elements after 1
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeFirst()\np.removeFirst()\np.get(1)",null,p.get(1));
		//Checks that the size got decreased
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeFirst()\np.removeFirst()\np.size()",1,(int) p.size());
		//Checks that removeLast is outputting the item it removes
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeFirst()\np.removeFirst()\np.removeLast()",1,(int) p.removeLast());
		//What happens when we remove from an empty deque?
		StudentArrayDeque<Integer> q = new StudentArrayDeque<Integer>();
		assertEquals("q.removeLast",null,q.removeLast());
	}

	@Test 
	public void testGet(){
		StudentArrayDeque<Integer> p = new StudentArrayDeque<Integer>();
		p.addLast(1);
		p.addLast(2);
		p.addLast(3);
		//Checks that the function is getting items within the deque
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.get(0)",1,(int) p.get(0));
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.get(1)",2,(int) p.get(1));
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.get(2)",3,(int) p.get(2));
		//Checks that the function is returning null for items not within the deque
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.get(3)",null,p.get(3));
		//Checks that the method is not destructive
		p.get(2);
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.get(2)\np.get(2)",3,(int) p.get(2));
		//Getting from an empty deque
		StudentArrayDeque<Integer> q = new StudentArrayDeque<Integer>();
		assertEquals("q.get(0)",null,q.get(0));
		q.addLast(1);
		q.addLast(2);
		q.removeFirst();
		assertEquals("q.addLast(1)\nq.addLast(2)\nq.removeFirst()\nq.get(0)",2,(int) q.get(0));
	}
	//Run tests
	public static void main(String... args) {        
        jh61b.junit.TestRunner.runTests("all", TestArrayDeque1B.class);
    }
}