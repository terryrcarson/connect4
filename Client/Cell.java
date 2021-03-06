package Client;

public class Cell {

	//Types: 0 = Empty, 1 = Red, 2 = Black
	private int x, y, type;
	
	public Cell() {}
	
    public Cell(int x, int y) { 
    	this.x = x;
    	this.y = y;
    }
    
    public void setType(int type) {
    	this.type = type;
    }
    
    public int getType(){
    	return type;
    }
    
    public int getX() { return x; }

    public int getY() { return y; }

    public void setX(int x) { this.x = x; }

    public void setY(int y) { this.y = y; }
    
    public void incX() { x++; }
    
    public void decX() { x--; }
    
    @Override
    public boolean equals(Object o) {
    	if (o instanceof Cell) {
    		Cell other = (Cell) o;
    		return (x == other.x && y == other.y);
    	}
    	return false;
    }
}