package dolmisani.toys.wordworms.originals;

import java.text.BreakIterator;
import java.util.Random;

import dolmisani.toys.wordworms.AbstractWordWorm;

public class VermeDisicio extends AbstractWordWorm {

	public VermeDisicio() {
		
		super("Verme Disicio", null);
	}
	
	@Override
	public String eat(String text) {

		
		BreakIterator bi = BreakIterator.getWordInstance();
		bi.setText(text);
		
		Random random = new Random();
		
		int wordIndex1 = random.nextInt(text.length());
		wordIndex1 = bi.preceding(wordIndex1);
		
		int wordIndex2 = random.nextInt(text.length());
		wordIndex2 = bi.preceding(wordIndex2);
		
		String word1 = text.substring(wordIndex1, bi.following(wordIndex1));
		String word2 = text.substring(wordIndex2, bi.following(wordIndex2));
		
		System.out.println(word1 + ", " + word2);
		
		
		return text;
	}
	
	private static boolean sameCase(String s1, String s2) {
		
		return true;		
	}
}
