package dolmisani.toys.wordworms;

public class CimiceMaiofaga implements WordWorm {

	public CimiceMaiofaga() {
		
	}
	
	@Override
	public String eat(String text) {

		return text.toLowerCase();

	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {

		return "Cimice Maiofaga";
	}

	@Override
	public String toString() {
		return getName();
	}
	
}
