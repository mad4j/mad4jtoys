package dolmisani.toys.wordworms.customs;

import dolmisani.toys.wordworms.AbstractWordWorm;

public class CalabroneCoatto extends AbstractWordWorm {

	public CalabroneCoatto() {
		
		super("Calabrone Coatto", null);
	}
	
	@Override
	public String eat(String text) {

		return text.replaceAll("([rR])[^rR]|([mM])[^mM]", "$1$0");
	}
}
