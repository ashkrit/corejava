package optionals;

import static java.util.Optional.ofNullable;

public class ElseOptional {
    public static void main(String[] args) {

        System.out.println(
                ofNullable(null)
                        .orElse("Not Available")
        );

        System.out.println(ofNullable(null)
                .orElseGet(() -> "Loaded from DB"));

        ofNullable(null).orElseThrow(IllegalArgumentException::new);
    }
}
