public class Palindrome{

	public static Deque<Character> wordToDeque(String word){
		Deque<Character> output = new LinkedListDeque<Character>();
		return wordToDequeR(word,output);
	}

	private static Deque<Character> wordToDequeR(String word, Deque<Character> d){
		if (word.length() > 0){
			d.addFirst(word.charAt(word.length()-1));
			word = word.substring(0,word.length() - 1);
			return wordToDequeR(word,d);
		}
		else{
			return d;
		}
	}

	public static boolean isPalindrome(String word){
		Deque<Character> letters = wordToDeque(word);
		int length = letters.size();
		while (length > 1){
			Character front = java.lang.Character.toLowerCase(letters.removeFirst());
			Character back = java.lang.Character.toLowerCase(letters.removeLast());
			if (front != back){
				return false;
			}
			else{
				length -=2;
			}
		}
		return true;
	}

	public static boolean isPalindrome(String word, CharacterComparator cc){
		Deque<Character> letters = wordToDeque(word);
		int length = letters.size();
		while (length > 1){
			Character front = java.lang.Character.toLowerCase(letters.removeFirst());
			Character back = java.lang.Character.toLowerCase(letters.removeLast());
			if (cc.equalChars(front,back)){
				length -=2;
			}
			else{
				return false;
			}
		}
		return true;
	}
}