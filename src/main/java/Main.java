import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Main {
    public static void main(String[] args) {
        LocalDate arriveToDestStation = LocalDate.of(2022, 5, 7);
        LocalDate nextFlightStart = LocalDate.of(2022, 4, 2);
        long res = ChronoUnit.DAYS.between(arriveToDestStation, nextFlightStart);
        System.out.println(res);
    }
}
