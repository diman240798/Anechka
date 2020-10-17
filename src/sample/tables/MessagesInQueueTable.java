package sample.tables;

public class MessagesInQueueTable {
    private int routeNumber;
    private  String nodeQueue;
    private Double sum;

    public MessagesInQueueTable() {}

    public MessagesInQueueTable(int routeNumber, String nodeQueue, Double sum) {
        this.routeNumber = routeNumber;
        this.nodeQueue = nodeQueue;
        this.sum = sum;
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(int routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getNodeQueue() {
        return nodeQueue;
    }

    public void setNodeQueue(String nodeQueue) {
        this.nodeQueue = nodeQueue;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }
}
