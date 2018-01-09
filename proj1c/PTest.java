public class PTest{
	public static void main(String[] args){
		Deque<Character> p = Palindrome.wordToDeque("Racecar");
		CharacterComparator obo = new OffByOne();
		System.out.println(Palindrome.isPalindrome("Racecar",obo));
	}
}