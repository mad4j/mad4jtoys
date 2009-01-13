package dolmisani.toys.wordworms;

public abstract class AbstractWordWorm implements WordWorm {

	private String name;
	private String description;
	
	public AbstractWordWorm(String name, String description) {
	
		this.name = name;
		this.description = description;
	}
	
	
	abstract public String eat(String text);

	@Override
	public String getDescription() {
		
		return description;
	}

	@Override
	public String getName() {
		
		return name;
	}
	
	@Override
	public String toString() {
		
		return name;
	}

}
