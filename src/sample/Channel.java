package sample;

import java.util.ArrayList;
import java.util.Arrays;

public enum Channel {
    ONE(1, 1, 2), TWO(2, 2, 3), THREE(3, 1, 3), FOUR(4, 1, 4), FIVE(5, 4, 2), SIX(6, 4, 3);

    public final int id;
    private final int n1;
    private final int n2;

    Channel(int id, int n1, int n2) {
        this.id = id;
        this.n1 = n1;
        this.n2 = n2;
    }

    public static Channel getChannel(int n1, int n2) {
        ArrayList<Integer> givenNodes = new ArrayList<>(Arrays.asList(n1, n2));
        Channel result = Arrays.stream(Channel.values())
                .filter(channel -> givenNodes.contains(channel.n1) && givenNodes.contains(channel.n2))
                .findFirst().get();
        return result;
    }
}