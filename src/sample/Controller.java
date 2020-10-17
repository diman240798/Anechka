package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.tables.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    public TextField
            tfNodeFailure,
            tfNodeRecovery,
            tfMessageLength,
            tfIntencity,
            tfDeliveryTime,
            tfAdressing,
            tfChanelSpeed,
            tfCountInBundle,
            tfRecoveryTime,
            tfChanelFailure,
            tfPackageLength;

    public TableView<RoutesTable> tbRoutes;
    public TableView<IntensityTable> tbMsgIntensity;
    public TableView<AvgDeliveryTable> tbAvgDelTime;
    public TableView<WaitTimeTable> tbWaitTime;
    public TableView<MessagesInQueueTable> tbMsgInQueue;

    private static List<Integer> NODES = new ArrayList<>(Arrays.asList(1, 2, 3, 4));

    public void count(ActionEvent actionEvent) {
        List<Double> probabilityOfNodeFailure = Arrays.stream(tfNodeFailure.getText().split(";\\s?")).map(Double::parseDouble).collect(Collectors.toList());
        List<Integer> probabilityOfNodeRecovery = Arrays.stream(tfNodeRecovery.getText().split(";\\s?")).map(Integer::parseInt).collect(Collectors.toList());
        int avgMsgLength = Integer.parseInt(tfMessageLength.getText());
        int msgIntensity = Integer.parseInt(tfIntencity.getText());
        int minDeliveryTime = Integer.parseInt(tfDeliveryTime.getText());
        List<Integer> channelSpeed = Arrays.stream(tfChanelSpeed.getText().split(";\\s?")).map(Integer::parseInt).collect(Collectors.toList());
        List<Integer> channelsInBundle = Arrays.stream(tfCountInBundle.getText().split(";\\s?")).map(Integer::parseInt).collect(Collectors.toList());
        List<Integer> channelRecoveryTime = Arrays.stream(tfRecoveryTime.getText().split(";\\s?")).map(Integer::parseInt).collect(Collectors.toList());
        List<Double> probabilityOfChannelFailure =  Arrays.stream(tfChanelFailure.getText().split(";\\s?")).map(Double::parseDouble).collect(Collectors.toList());
        List<Integer> avgPackLength = Arrays.stream(tfPackageLength.getText().split(";\\s?")).map(Integer::parseInt).collect(Collectors.toList());

        String[] startEndNodes = tfAdressing.getText().split("\\s?-\\s?");

        Integer startNode = Integer.parseInt(startEndNodes[0]);
        Integer endNode = Integer.parseInt(startEndNodes[1]);


        List<List<Integer>> routes = getRoutes(startNode, endNode);


        List<Double> routeFailPropList = getRouteFailureProbList(probabilityOfNodeFailure, probabilityOfChannelFailure, routes);
        fillRoutesTable(routes, routeFailPropList);

        double failurePropSum = routeFailPropList.stream().reduce(Double::sum).get();

        List<Double> roudFailPropListNormalized = routeFailPropList.stream()
                .map(it -> it / failurePropSum)
                .collect(Collectors.toList());

        List<Double> msgIntenseList = roudFailPropListNormalized.stream()
                .map(it -> it * msgIntensity)
                .collect(Collectors.toList());

        fillIntensityTable(routeFailPropList, roudFailPropListNormalized, msgIntenseList);

        List<List<Double>> avgDeliveryTimeList = getDeliveryTimeList(channelSpeed, channelsInBundle, avgPackLength, routes);



        List<List<Double>> waitRouteTimesList = new ArrayList<>(5);
        List<List<Double>> avgMsgQueueList = new ArrayList<>(5);

        for (int i = 0; i < 5; i++) {
            double instensity = msgIntenseList.get(i);
            List<Double> avgWaitNodeTimes = avgDeliveryTimeList.get(i);

            List<Double> Wlist = new ArrayList<>(3);
            List<Double> Noklist = new ArrayList<>(3);


            for (int j = 0; j < avgWaitNodeTimes.size(); j++) {
                double avgWaitTime = avgWaitNodeTimes.get(j);
                double P = avgWaitTime * instensity;

                double W = P * avgWaitTime / (1 - P);
                double Nok = P * P / (1 - P);

                Wlist.add(W);
                Noklist.add(Nok);
            }

            waitRouteTimesList.add(Wlist);
            avgMsgQueueList.add(Noklist);

        }


       fillWaitNodeTable(routes, waitRouteTimesList);
       fillMsgQueueNodeTable(routes, avgMsgQueueList);



        System.out.println();

    }

    private void fillMsgQueueNodeTable(List<List<Integer>> routes, List<List<Double>> avgMsgQueueList) {
        List<MessagesInQueueTable> items = new ArrayList<>(5);

        for (int i = 0; i < routes.size(); i++) {
            StringBuilder nodeTimeSb = new StringBuilder();

            List<Integer> route = routes.get(i);
            List<Double> times = avgMsgQueueList.get(i);

            double sum = 0.0;

            for (int j = 0; j < times.size(); j++) {
                int node = route.get(j + 1);
                double msgQueue = times.get(j);
                nodeTimeSb.append(node + "/" + msgQueue + "\t\t");

                sum += msgQueue;
            }

            MessagesInQueueTable waitTimeTable = new MessagesInQueueTable(i + 1, nodeTimeSb.toString(), sum);
            items.add(waitTimeTable);
        }

        tbMsgInQueue.setItems(FXCollections.observableList(items));
    }

    private void fillWaitNodeTable(List<List<Integer>> routes, List<List<Double>> waitRouteTimesList) {
        List<WaitTimeTable> items = new ArrayList<>(5);

        for (int i = 0; i < routes.size(); i++) {
            StringBuilder nodeTimeSb = new StringBuilder();

            List<Integer> route = routes.get(i);
            List<Double> times = waitRouteTimesList.get(i);

            double sum = 0.0;

            for (int j = 0; j < times.size(); j++) {
                int node = route.get(j + 1);
                double time = times.get(j);
                nodeTimeSb.append(node + "/" + time + "\t\t");

                sum += time;
            }

            WaitTimeTable waitTimeTable = new WaitTimeTable(i + 1, nodeTimeSb.toString(), sum);
            items.add(waitTimeTable);
        }

        tbWaitTime.setItems(FXCollections.observableList(items));
    }

    private List<List<Double>> getDeliveryTimeList(List<Integer> channelSpeed, List<Integer> channelsInBundle, List<Integer> avgPackLength, List<List<Integer>> routes) {
        List<AvgDeliveryTable> tableData = new ArrayList<>(5);

        List<List<Double>> avgDeliveryTimeList = new ArrayList<>(5);

        for (int i = 0; i < routes.size(); i++) {
            List<Integer> route = routes.get(i);

            AvgDeliveryTable tabItem = new AvgDeliveryTable();
            tabItem.setRouteNumber(i + 1);
            tabItem.setRoute(route.stream().map(String::valueOf).reduce((i1, i2) -> i1 + "->" + i2).get());

            List<Double> routeDeliveryTimes = new ArrayList<>(3);

            StringBuilder chanAndTimesSb = new StringBuilder();

            for (int j = 0; j < route.size() - 1; j++) {
                int start = route.get(j);
                int end = route.get(j + 1);

                Channel channel = Channel.getChannel(start, end);
                int chanIndex = channel.id - 1;

                double routeDeliveryTime = countRouteDeliveryTime(avgPackLength.get(chanIndex), channelSpeed.get(chanIndex), channelsInBundle.get(chanIndex));
                routeDeliveryTimes.add(routeDeliveryTime);

                chanAndTimesSb.append(channel.id + "/" + routeDeliveryTime + "\t\t");
            }

            avgDeliveryTimeList.add(routeDeliveryTimes);

            tabItem.setChanAndTime(chanAndTimesSb.toString());
            tableData.add(tabItem);
        }

        tbAvgDelTime.setItems(FXCollections.observableList(tableData));

        return avgDeliveryTimeList;
    }

    private void fillIntensityTable(List<Double> routeFailPropList, List<Double> roudFailPropListNormalized, List<Double> msgIntenseList) {
        List<IntensityTable> intensityTables = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            IntensityTable intensityTable = new IntensityTable(i + 1, routeFailPropList.get(i), roudFailPropListNormalized.get(i), msgIntenseList.get(i));
            intensityTables.add(intensityTable);
        }
        ObservableList<IntensityTable> msgInstensityObsList = FXCollections.observableList(intensityTables);
        tbMsgIntensity.setItems(msgInstensityObsList);
    }

    private void fillRoutesTable(List<List<Integer>> routes, List<Double> routeFailPropList) {
        List<RoutesTable> roudesTabList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String nodes = routes.get(i).stream().map(String::valueOf).reduce((it1, it2) -> it1 + " -> " + it2).get();
            RoutesTable routesTable = new RoutesTable(i + 1, nodes, routeFailPropList.get(i));
            roudesTabList.add(routesTable);
        }
        ObservableList<RoutesTable> routesTables = FXCollections.observableList(roudesTabList);
        tbRoutes.setItems(routesTables);
    }

    private List<Double> getRouteFailureProbList(List<Double> probabilityOfNodeFailure, List<Double> probabilityOfChannelFailure, List<List<Integer>> routes) {
        List<Double> routeFailPropList = new ArrayList<>(5);

        for (int i = 0; i < routes.size(); i++) {
            List<Integer> route = routes.get(i);

            List<Double> minusValues = new ArrayList<>(route.size() + route.size() - 1);

            for (int j = 0; j < route.size() - 1; j++) {
                int start = route.get(j);

                // node failure
                Double nodeFailureProb = probabilityOfNodeFailure.get(start - 1);
                minusValues.add(nodeFailureProb);


                int end = route.get(j + 1);

                Channel channel = Channel.getChannel(start, end);
                double channelFailure = probabilityOfChannelFailure.get(channel.id - 1);
                minusValues.add(channelFailure);
            }
            Double lastChanFailProb = probabilityOfNodeFailure.get(route.get(route.size() - 1) - 1);
            minusValues.add(lastChanFailProb);

            double routeFailure = countRoudeFailure(minusValues);
            routeFailPropList.add(routeFailure);
        }
        return routeFailPropList;
    }

    private List<List<Integer>> getRoutes(Integer startNode, Integer endNode) {
        List<List<Integer>> routes = new ArrayList<>(5);

        List<Integer> extraNodes = new ArrayList<>();
        extraNodes.addAll(NODES);
        extraNodes.remove(startNode);
        extraNodes.remove(endNode);

        routes.add(new ArrayList<>(Arrays.asList(startNode, endNode)));
        routes.add(new ArrayList<>(Arrays.asList(startNode, extraNodes.get(0), endNode)));
        routes.add(new ArrayList<>(Arrays.asList(startNode, extraNodes.get(1), endNode)));
        routes.add(new ArrayList<>(Arrays.asList(startNode, extraNodes.get(0), extraNodes.get(1), endNode)));
        routes.add(new ArrayList<>(Arrays.asList(startNode, extraNodes.get(1), extraNodes.get(0), endNode)));
        return routes;
    }

    private double countRouteDeliveryTime(int avgMsgLength, int channelSpeed, int count) {
        double result = (double) avgMsgLength / (channelSpeed * count);
        return result;
    }

    private double countRoudeFailure(List<Double> minusValues) {
        Double value = minusValues.stream()
                .map(it -> 1 - it)
                .reduce((it1, it2) -> it1 * it2).get();
        double result = 1 - value;
        return result;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prepareTables();
        count(null);
    }

    public void prepareTables() {
        setUpRoutesTables();
        setUpIntensityTable();
        setUpAvgDeliveryTable();
        setUpWaitTimeTable();
        setUpQueueMsgTable();
    }

    private void setUpQueueMsgTable() {
        TableColumn<MessagesInQueueTable, Integer> routeNumIntensCol = new TableColumn<>("Route №");
        routeNumIntensCol.setCellValueFactory(new PropertyValueFactory<>("routeNumber"));
        TableColumn<MessagesInQueueTable, String> nodesAndTimesCol = new TableColumn<>("Messages Queue per Node");
        nodesAndTimesCol.setCellValueFactory(new PropertyValueFactory<>("nodeQueue"));
        TableColumn<MessagesInQueueTable, String> sumCol = new TableColumn<>("Sum");
        sumCol.setCellValueFactory(new PropertyValueFactory<>("sum"));

        tbMsgInQueue.getColumns().addAll(routeNumIntensCol, nodesAndTimesCol, sumCol);
    }

    private void setUpWaitTimeTable() {
        TableColumn<WaitTimeTable, Integer> routeNumIntensCol = new TableColumn<>("Route №");
        routeNumIntensCol.setCellValueFactory(new PropertyValueFactory<>("routeNumber"));
        TableColumn<WaitTimeTable, String> nodesAndTimesCol = new TableColumn<>("Nodes and times");
        nodesAndTimesCol.setCellValueFactory(new PropertyValueFactory<>("nodeTime"));
        TableColumn<WaitTimeTable, String> sumCol = new TableColumn<>("Sum");
        sumCol.setCellValueFactory(new PropertyValueFactory<>("sum"));

        tbWaitTime.getColumns().addAll(routeNumIntensCol, nodesAndTimesCol, sumCol);
    }

    private void setUpAvgDeliveryTable() {
        TableColumn<AvgDeliveryTable, Integer> routeNumIntensCol = new TableColumn<>("Route №");
        routeNumIntensCol.setCellValueFactory(new PropertyValueFactory<>("routeNumber"));
        TableColumn<AvgDeliveryTable, String> nodesAndTimesCol = new TableColumn<>("Channel and times");
        nodesAndTimesCol.setCellValueFactory(new PropertyValueFactory<>("chanAndTime"));


        TableColumn<AvgDeliveryTable, String> routeCol= new TableColumn<>("Route");
        routeCol.setCellValueFactory(new PropertyValueFactory<>("route"));

        tbAvgDelTime.getColumns().addAll(routeNumIntensCol, nodesAndTimesCol, routeCol);
    }

    private void setUpIntensityTable() {
        TableColumn<IntensityTable, Integer> routeNumIntensCol = new TableColumn<>("Route №");
        routeNumIntensCol.setCellValueFactory(new PropertyValueFactory<>("routeNumber"));
        TableColumn<IntensityTable, Integer> failProbIntensCol = new TableColumn<>("FailProb");
        failProbIntensCol.setCellValueFactory(new PropertyValueFactory<>("probRouteFail"));


        TableColumn<IntensityTable, String> failProbNormCol= new TableColumn<>("FailProb Normalized");
        failProbNormCol.setCellValueFactory(new PropertyValueFactory<>("probRouteFailNorm"));

        TableColumn<IntensityTable, String> intensCol= new TableColumn<>("Msg Intensity");
        intensCol.setCellValueFactory(new PropertyValueFactory<>("instensity"));

        tbMsgIntensity.getColumns().addAll(routeNumIntensCol, failProbIntensCol, failProbNormCol, intensCol);
    }

    private void setUpRoutesTables() {
        TableColumn<RoutesTable, Integer> routeNumCol = new TableColumn<>("Route №");
        routeNumCol.setCellValueFactory(new PropertyValueFactory<>("routeNumber"));


        TableColumn<RoutesTable, String> nodes = new TableColumn<>("Route");
        nodes.setCellValueFactory(new PropertyValueFactory<>("nodes"));

        TableColumn<RoutesTable, String> failProbCol= new TableColumn<>("FailProb");
        failProbCol.setCellValueFactory(new PropertyValueFactory<>("probRouteFail"));


        tbRoutes.getColumns().addAll(routeNumCol, nodes, failProbCol);
    }
}
