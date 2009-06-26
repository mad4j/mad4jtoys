package dolmisani.games.jpong;

public abstract class AbstractController {

	protected GameObject gameObject;
	protected GameLogic gameLogic;

	public AbstractController(GameObject gameObject, GameLogic gameLogic) {
		
		this.gameObject = gameObject;
		this.gameLogic = gameLogic;
		
	}
	
	public abstract void control();
	
	
}
