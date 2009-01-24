package dolmisani.toys.randtext;

import java.text.BreakIterator;
import java.util.Arrays;
import java.util.Random;

import com.sun.org.apache.xalan.internal.xsltc.dom.KeyIndex;

import sun.security.x509.KeyIdentifier;

public class RandomWriter {

	private static Random random = new Random();
	
	public RandomWriter() {
		
	}
	
	
	public static String generate(int order, String source) {		
	
		
		//TODO: le chiavi sono presenti nel testo e iniziano con una lettera maiuscola
		//altrimenti sono le prime order letter di una parola
		/*
		for(String word : source.split("[\\p{Punct}\\p{Space}]+")) {
			System.out.println("TEST: " + word);
		}
		*/
		BreakIterator boundary = BreakIterator.getSentenceInstance();
		boundary.setText(source);
		
		int keyIndex = random.nextInt(source.length()-order);
		
		System.out.println(keyIndex);
		
		keyIndex = boundary.preceding(keyIndex);
		System.out.println(keyIndex);
		
		StringBuilder keyValue = new StringBuilder(source.substring(keyIndex, keyIndex+order));
		StringBuilder resultText = new StringBuilder(keyValue);
		
		for(int i=0; i<50; i++) {
			
			String values = source.replaceAll(String.format("(?ms).*?\\Q%s\\E(.)|.*$", keyValue), "$1");
			System.out.println(keyValue +", "+values);
		
			if(values.length() == 0) {
				break;
			}
			
			char newChar = values.charAt(random.nextInt(values.length()));
			
			resultText.append(newChar);		
			
			keyValue.append(newChar).deleteCharAt(0);						
		}
		
		return resultText.toString();		
	}
	
	
	public static void main(String[] args) {
			
		String text = RandomWriter.generate(3, "Questa è una semplice prova, speriamo che sia abbastanza significativa. Proviamo a vedere cosa succede. Quel ramo del lago di como che volge a mezzogiorno.");
		
		System.out.println("GENERATED:");
		System.out.println(text);
	}
	
}
