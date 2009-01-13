package dolmisani.toys.wordworms.originals;

import dolmisani.toys.wordworms.AbstractWordWorm;

public class Farfalo extends AbstractWordWorm {

	public Farfalo() {

		super("Farfalo", null);
	}

	@Override
	public String eat(String text) {

		return text.replaceAll("(\\p{Alpha})\\1", "$1");
	}
}
