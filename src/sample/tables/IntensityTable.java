package sample.tables;

public class IntensityTable {
    private int routeNumber;
    private double probRouteFail;
    private double probRouteFailNorm;
    private double instensity;

    public IntensityTable() {}

    public IntensityTable(int routeNumber, double probRouteFail, double probRouteFailNorm, double instensity) {
        this.routeNumber = routeNumber;
        this.probRouteFail = probRouteFail;
        this.probRouteFailNorm = probRouteFailNorm;
        this.instensity = instensity;
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(int routeNumber) {
        this.routeNumber = routeNumber;
    }

    public double getProbRouteFail() {
        return probRouteFail;
    }

    public void setProbRouteFail(double probRouteFail) {
        this.probRouteFail = probRouteFail;
    }

    public double getProbRouteFailNorm() {
        return probRouteFailNorm;
    }

    public void setProbRouteFailNorm(double probRouteFailNorm) {
        this.probRouteFailNorm = probRouteFailNorm;
    }

    public double getInstensity() {
        return instensity;
    }

    public void setInstensity(double instensity) {
        this.instensity = instensity;
    }
}
