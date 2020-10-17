package sample.tables;

public class AvgDeliveryTable {
    private int routeNumber;
    private String chanAndTime;
    private String route;

    public AvgDeliveryTable() {
    }

    public AvgDeliveryTable(int routeNumber, String chanAndTime, String route) {
        this.routeNumber = routeNumber;
        this.chanAndTime = chanAndTime;
        this.route = route;
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(int routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getChanAndTime() {
        return chanAndTime;
    }

    public void setChanAndTime(String chanAndTime) {
        this.chanAndTime = chanAndTime;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}
