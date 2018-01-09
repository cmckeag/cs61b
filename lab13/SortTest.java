public class SortTest {
	public static void main(String[] args) {
		String[] input = new String[5];
		input[0] = "seagull";
		input[1] = "alexPan";
		input[2] = "josh";
		input[3] = "koop";
		input[4] = "ashwin";

		//System.out.println(input[0]);
		//System.out.println(input[1]);
		//System.out.println(input[2]);
		//System.out.println(input[3]);
		//System.out.println(input[4]);
		
		String[] sorted = RadixSort.sort(input);

		System.out.println("");
		System.out.println("After:");
		System.out.println(sorted[0]);
		System.out.println(sorted[1]);
		System.out.println(sorted[2]);
		System.out.println(sorted[3]);
		System.out.println(sorted[4]);
	}
}