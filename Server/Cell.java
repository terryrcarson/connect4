//package Server;

/**********************************************************
 * Cell
 * - Abstract representation of the cells in the game board
 *********************************************************/

public class Cell {

	//Types: 0 = Empty, 1 = Red, 2 = Black
	private int x, y, type;
	
	/*************************************************
	 * Constructor
	 * Precondition: N/A
	 * Postcondition: An uninitialized cell is created
	 ************************************************/
	public Cell() {}
	
	/***************************************************
	 * Constructor
	 * Precondition: N/A
	 * Postcondition: A cell with coordinates is created
	 **************************************************/
    public Cell(int x, int y) { 
    	this.x = x;
    	this.y = y;
    }
    
    /***************************************
     * setType
     * Precondition: N/A
     * Postcondition: The cell's type is set
     **************************************/
    public void setType(int type) {
    	this.type = type;
    }
    
    /**********************************************
     * getType
     * Precondition: The cell's type is initialized
     * Postcondition: The cell's type is returned
     *********************************************/
    public int getType(){
    	return type;
    }
    
    /****************************************************
     * getX
     * Precondition: The cell's location is initialized
     * Postcondition: The cell's x-coordinate is returned
     ***************************************************/
    public int getX() { return x; }
    
    /****************************************************
     * getY
     * Precondition: The cell's location is initialized
     * Postcondition: The cell's y-coordinate is returned
     ***************************************************/
    public int getY() { return y; }
    
    /***************************************************
     * setX
     * Precondition: The cell's location is initialized
     * Postcondition: The cell's x-coordinate is updated
     **************************************************/
    public void setX(int x) { this.x = x; }
    
    /***************************************************
     * setY
     * Precondition: The cell's location is initialized
     * Postcondition: The cell's y-coordinate is updated
     **************************************************/
    public void setY(int y) { this.y = y; }
    
    /**********************************************************
     * incX
     * Precondition: The cell's location is initialized
     * Postcondition: The cell's x-coordinate is increased by 1
     *********************************************************/
    public void incX() { x++; }
    
    /**********************************************************
     * decX
     * Precondition: The cell's location is initialized
     * Postcondition: The cell's x-coordinate is decreased by 1
     *********************************************************/
    public void decX() { x--; }
    
    /*****************************************************************
     * equals
     * Precondition: Both cells are initialized
     * Postcondition: Determines if two cells are in the same location
     ****************************************************************/
    @Override
    public boolean equals(Object o) {
    	if (o instanceof Cell) {
    		Cell other = (Cell) o;
    		return (x == other.x && y == other.y);
    	}
    	return false;
    }
}