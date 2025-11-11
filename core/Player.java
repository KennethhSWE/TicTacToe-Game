package core; 

public class Player {
    private String name; 
    private int position = 1;

    public Player(String name) { 
        this.name = name; 
    }

    public String getName() { 
        return name; 
    }

    public int getPosition() {
        return position; 
    }

    public void setPosition(int position) {
        this.position = Math.max(1, Math.min(100, position)); // This will ensure that players postion stay within the range 1 through 100.
    }

    public void move (int steps) {
        position += steps;

        if (position > 100) position = 100; // This will stop the player from going past 100
    }
}
