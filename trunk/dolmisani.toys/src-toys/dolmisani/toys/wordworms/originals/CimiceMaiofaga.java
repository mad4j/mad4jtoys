package dolmisani.toys.wordworms.originals;

import dolmisani.toys.wordworms.AbstractWordWorm;

public class CimiceMaiofaga extends AbstractWordWorm {

	public CimiceMaiofaga() {
		
		super("Cimice Maiofaga", null);		
	}
	
	@Override
	public String eat(String text) {

		return text.toLowerCase();
	}
}
