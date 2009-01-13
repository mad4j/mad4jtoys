package dolmisani.toys.wordworms.originals;

import dolmisani.toys.wordworms.AbstractWordWorm;

public class TermiteDiDublino extends AbstractWordWorm {

	public TermiteDiDublino() {
		
		super("Termite di Dublino", null);
	}
	
	@Override
	public String eat(String text) {

		return text.replaceAll("\\p{Punct}", "");
	}
}
