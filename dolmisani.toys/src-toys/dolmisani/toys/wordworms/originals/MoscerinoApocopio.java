package dolmisani.toys.wordworms.originals;

import dolmisani.toys.wordworms.AbstractWordWorm;

public class MoscerinoApocopio extends AbstractWordWorm {

	public MoscerinoApocopio() {
		
		super("Moscerino Apocopio", null);
	}
	
	@Override
	public String eat(String text) {
		
		return text.replaceAll("(?i)([aei]r)e", "$1");
	}
}
