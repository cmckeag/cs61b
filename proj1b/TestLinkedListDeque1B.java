import static org.junit.Assert.*;
import org.junit.Test;

public class TestLinkedListDeque1B{

	@Test
	public void testLinkedListDeque(){
		StudentLinkedListDeque<Integer> test = new StudentLinkedListDeque<Integer>();
		//Test that we generated a linked list of size 0
		assertEquals("test.size()",0,(int) test.size());
	}

	@Test
	public void testAddFirst(){
		StudentLinkedListDeque<Integer> p = new StudentLinkedListDeque<Integer>();
		p.addFirst(1);
		p.addFirst(2);
		//Test that we increased the size
		assertEquals("p.addFirst(1)\np.addFirst(2)\np.size()",2,(int) p.size());
		//Test that the 1st item in the list is 2
		assertEquals("p.addFirst(1)\np.addFirst(2)\np.size()\np.get(0)",2,(int) p.get(0));
		//Test that the 2nd item in the list is 1
		assertEquals("p.addFirsT(1)\np.addFirst(2)\np.size()\np.get(0)\np.get(1)",1,(int) p.get(1));
	}

	@Test
	public void testAddLast(){
		StudentLinkedListDeque<Integer> p = new StudentLinkedListDeque<Integer>();
		p.addLast(1);
		p.addLast(2);
		//Test that the size increased
		assertEquals("p.addLast(1)\np.addLast(2)\np.size()",2,(int) p.size());
		//Test that the 1st item in the list is 1
		assertEquals("p.addLast(1)\np.addLast(2)\np.size()\np.get(0)",1,(int) p.get(0));
		//Test that the last item is 2
		assertEquals("p.addLast(1)\np.addLast(2)\np.size()\np.get(0)\np.get(1)",2,(int) p.get(1));
	}

	@Test
	public void testEmpty(){
		StudentLinkedListDeque<Integer> p = new StudentLinkedListDeque<Integer>();
		assertEquals("p.isEmpty()",true,p.isEmpty());
		p.addLast(1);
		assertEquals("p.addLast(1)\np.isEmpty()",false,p.isEmpty());
	}

	@Test
	public void testRemoveFirst(){
		StudentLinkedListDeque<Integer> p = new StudentLinkedListDeque<Integer>();
		p.addLast(1);
		p.addLast(2);
		p.addLast(3);
		p.removeFirst();
		//Check the size got decreased
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeFirst()\np.size()",2,(int) p.size());
		//check that the new first element is 2
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeFirst()\np.size()\np.get(0)",2,(int) p.get(0));
		//Check that there are no elements after 3
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeFirst()\np.size()\np.get(0)\np.get(2)",null,p.get(2));
		//Check that the function is outputting what it removes
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeFirst()\np.size()\np.get(0)\np.get(2)\np.removeFirst()",2,(int) p.removeFirst());
	}

	@Test
	public void testRemoveLast(){
		StudentLinkedListDeque<Integer> p = new StudentLinkedListDeque<Integer>();
		p.addLast(1);
		p.addLast(2);
		p.addLast(3);
		p.removeLast();
		//Check size
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeLast()\np.size()",2,(int) p.size());
		//Check that this shit isn't broken
		Integer x = p.get(1);
		Integer y = 2;
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeLast()\np.size()\np.get(1)",y,x);
		//Check no elements after the last one
		//assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeLast()\np.size()\np.getRecursive(1)\np.getRecursive(2)",null,(int) p.getRecursive(2));
		//Check output
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.removeLast()\np.size()\np.getRecursive(1)\np.getRecursive(2)\np.removeLast()",2,(int) p.removeLast());
		
	}

	@Test
	public void testGet(){
		StudentLinkedListDeque<Integer> p = new StudentLinkedListDeque<Integer>();
		p.addLast(1);
		p.addLast(2);
		p.addLast(3);
		//Checks that it actually gets the 3rd item
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.get(2)",3,(int) p.get(2));
		//Checks that it is not destructive
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.get(2)\np.get(2)",3,(int) p.get(2));
	}

	@Test
	public void testGetR(){
		StudentLinkedListDeque<Integer> p = new StudentLinkedListDeque<Integer>();
		p.addLast(1);
		p.addLast(2);
		p.addLast(3);
		//Checks that it actually gets the 3rd item
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.getRecursive(2)",3,(int) p.getRecursive(2));
		//Checks that it is not destructive
		assertEquals("p.addLast(1)\np.addLast(2)\np.addLast(3)\np.getRecursive(2)\np.getRecursive(2)",3,(int) p.getRecursive(2));
	}

	//Run tests
	public static void main(String... args) {        
        jh61b.junit.TestRunner.runTests("all", TestLinkedListDeque1B.class);
    }
}