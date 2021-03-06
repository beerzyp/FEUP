package gameLogic;


import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents the Guard.
 * 
 * @author Afonso Pinto and Tom�s Oliveira
 * @see Character
 * 
 */

public abstract class Guard  extends Character{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Coord[] route;
	protected Direction[] route_dir;
	protected int index;
	protected boolean isMovingForward;
	protected Direction orientation;
	protected boolean isSleeping;
	
	/**
	 * Constructs and initializes a Guard.
	 * 
	 */
	
	public Guard(){
		this.symbol = 'G'; this.under_char = ' '; this.index = 0; this.isMovingForward = true; this.isSleeping = false;
		this.orientation = Direction.LEFT; this.route = new Coord[1]; this.route_dir = new Direction[]{Direction.LEFT}; };

	/**
	 * Constructs and initializes a Guard with position of type Coord.
	 * 
	 * @see {@link Coord.java}
	 */
	
	public Guard(Coord position){
		this();
		this.position = position;
		this.route = new Coord[]{new Coord(this.position.getX(), this.position.getY())};
	}
	
	/**
	 * Constructs and initializes a Guard with level of type int.
	 * 
	 */
	
	public Guard(int level){
		this.symbol = 'G'; this.under_char = ' '; this.index = 0; this.isMovingForward = true; this.isSleeping = false; Coord startingPos;
		this.route = new Coord[1]; this.route_dir = new Direction[1]; 	this.route_dir[0]=Direction.LEFT; this.orientation = Direction.LEFT;

		switch (level) {
		case 0: startingPos = new Coord(1,3); this.position = startingPos; 	this.route[0]=startingPos; 	break;
		case 1: startingPos = new Coord(1,8); this.position = startingPos; 	this.route[0]=startingPos; break;
		case 2: startingPos = new Coord(1,8); this.position = startingPos; Coord[] temp = { 
					new Coord(1,8), new Coord(1,7),new Coord(2,7), new Coord(3,7), new Coord(4,7),  new Coord(5,7), new Coord(5,6),
					new Coord(5,5), new Coord(5,4), new Coord(5,3), new Coord(5,2), new Coord(5,1), new Coord(6,1), new Coord(6,2),
					new Coord(6,3), new Coord(6,4), new Coord(6,5), new Coord(6,6), new Coord(6,7), new Coord(6,8), new Coord(5,8), 
					new Coord(4,8),new Coord(3,8), new Coord(2,8) };
			Direction[] temp_dir = { Direction.LEFT, Direction.LEFT, Direction.DOWN, Direction.DOWN, Direction.DOWN,  Direction.DOWN,
					Direction.LEFT, Direction.LEFT, Direction.LEFT,Direction.LEFT, Direction.LEFT, Direction.LEFT, Direction.DOWN,
					Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT,
					Direction.RIGHT,  Direction.UP, Direction.UP, Direction.UP, Direction.UP 	};
			
			this.route = Arrays.copyOf(temp, temp.length); 	this.route_dir = Arrays.copyOf(temp_dir, temp_dir.length);
			this.orientation = route_dir[0]; break;
		default: break; }
	}
	
	/**
	 * Moves the Guard in a certain direction.
	 * 
	 * @param board
	 *            the board of the game
	 * @param direction
	 *            the direction in which the guard should move
	 */
	
	public Action move(Board board, Direction direction){
		return Action.NOACTION;
	}
	
	public abstract Action move(Board board);
	
	/**
	 * Moves the Guard in a certain direction.
	 * 
	 * @param board
	 *            the board of the game
	 */
	
	protected boolean randomDecision(){

		int randomNum = ThreadLocalRandom.current().nextInt(0, 2 + 1);
		
		switch (randomNum) {
		case 0:
			return true;
		case 1:
			return true;
		case 2:
			return false;
		default:
			return false;
		}
	}

	/**
	 * 
	 * Returns the orientation of the guard.
	 * 
	 * @return the orientation of the guard.
	 * 
	 */
	
	public Direction getOrientation() {
		return orientation;
	}

	/**
	 * Returns true if the guard is sleeping.
	 * 
	 * @return true if the guard is sleeping.
	 */
	
	public boolean isSleeping() {
		return isSleeping;
	}

	
	
}