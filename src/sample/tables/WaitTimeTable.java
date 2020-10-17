package sample.tables;

public class WaitTimeTable {
    private int routeNumber;
    private String nodeTime;
    private double sum;

    public WaitTimeTable() {}

    public WaitTimeTable(int routeNumber, String nodeTime, double sum) {
        this.routeNumber = routeNumber;
        this.nodeTime = nodeTime;
        this.sum = sum;
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(int routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getNodeTime() {
        return nodeTime;
    }

    public void setNodeTime(String nodeTime) {
        this.nodeTime = nodeTime;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
