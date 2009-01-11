package dolmisani.toys.wordworms;

public class Farfalo implements WordWorm {

	
	public Farfalo() {
		
	}
	
	
	@Override
    public String eat(String text) {
 
        return text.replaceAll("(\\p{Alpha})\\1", "$1");
    }

	@Override
	public String getName() {
		
		return "Farfalo";
	}
	
	@Override
	public String toString() {
		
		return getName();
	}

	@Override
	public String getDescription() {

		return null;
	}
	
}
