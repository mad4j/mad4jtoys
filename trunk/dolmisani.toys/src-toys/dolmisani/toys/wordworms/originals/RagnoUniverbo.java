package dolmisani.toys.wordworms.originals;

import dolmisani.toys.wordworms.AbstractWordWorm;

public class RagnoUniverbo extends AbstractWordWorm {

	public RagnoUniverbo() {
		
		super("Ragno Univerbo", null);
	}
	
	@Override
	public String eat(String text) {
		
		return text.replaceAll("(?i)elicere", "");
	}
}
