package dolmisani.toys.wordworms;

public class TermiteDiDublino implements WordWorm {

	public TermiteDiDublino() {
		
	}
	
	@Override
	public String eat(String text) {

		return text.replaceAll("\\p{Punct}", "");

	}

	@Override
	public String getDescription() {
		
		return null;
	}

	@Override
	public String getName() {

		return "Termite di Dublino";
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
