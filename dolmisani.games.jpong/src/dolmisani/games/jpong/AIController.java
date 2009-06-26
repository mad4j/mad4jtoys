package dolmisani.games.jpong;

public class AIController extends AbstractController {

	public AIController(GameObject gameObject, GameLogic gameLogic) {
		
		super(gameObject, gameLogic);		
	}

	@Override
	public void control() {
		
		GameObject ballObject = gameLogic.getBallObject();
		GameObject gameFieldObject = gameLogic.getGameFieldObject();
		
		if(ballObject.getDeltaX() < 0) {
			
			//the ball is moving away from the AI paddle		
			
			if(gameObject.getCenterY() < gameFieldObject.getCenterY()) {
				gameObject.setY(gameObject.getY()+3);
			}

			if(gameObject.getCenterY() > gameFieldObject.getCenterY()) {
				gameObject.setY(gameObject.getY()-3);
			}
			
		} else {
			
			//the ball is moving toward the AI paddle
			
			int distance = ballObject.getCenterY() - gameObject.getCenterY();
			
			gameObject.setY(gameObject.getY() + distance / 8);
						
		}		
	}

}
