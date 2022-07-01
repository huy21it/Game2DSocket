package entity;

public class Player extends Entity {
    int x;
    int y;
    String direction;
    int id;
    public boolean logout;
    public void setDirection(String direction) {
        this.direction = direction;
    }
    public String getDirection() {
        return direction;
    }
    public Player(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.logout = false;
    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
