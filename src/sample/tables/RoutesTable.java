package sample.tables;

public class RoutesTable {
    private int routeNumber;
    private String nodes;
    private double probRouteFail;

    public RoutesTable() {}

    public RoutesTable(int routeNumber, String nodes, double probRouteFail) {
        this.routeNumber = routeNumber;
        this.nodes = nodes;
        this.probRouteFail = probRouteFail;
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(int routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public double getProbRouteFail() {
        return probRouteFail;
    }

    public void setProbRouteFail(double probRouteFail) {
        this.probRouteFail = probRouteFail;
    }
}
