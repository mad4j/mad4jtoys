package dolmisani.toys.wordworms.customs;

import dolmisani.toys.wordworms.AbstractWordWorm;

public class BacoDiDaniele extends AbstractWordWorm {

	public BacoDiDaniele() {
		
		super("Baco di Daniele", "");
	}
	
	@Override
	public String eat(String text) {
		
		return text.replaceAll("v", "f");
	}

}
