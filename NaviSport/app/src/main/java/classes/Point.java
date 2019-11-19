package classes;

public class Point {
    private int flag;
    private String name;
    private double lattitude;
    private double longtitude;

    public Point(int flag, String name, double lattitude, double longtitude) {
        this.flag = flag;
        this.name = name;
        this.lattitude = lattitude;
        this.longtitude = longtitude;
    }

    public int getFlag() {
        return flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

}
