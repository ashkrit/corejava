package faas;

import java.io.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

public class FunctionAsServiceApp {

    public static void main(String[] args) throws Exception {

        List functions = asList(
                SerCode.f((Integer x) -> x * x),
                SerCode.f((String x) -> x.toUpperCase()),
                SerCode.p((String x) -> x.length() > 5)
        );

        byte[] code = saveFunction(functions);
        ObjectInputStream fStream = codeStream(code);

        List rFunctions = (List) fStream.readObject();

        int fIndex = 0;
        Function<Integer, Integer> rSquare = (Function<Integer, Integer>) rFunctions.get(fIndex++);
        System.out.println(rSquare.apply(10)); // Shows 100

        Function<String, String> rUpper = (Function<String, String>) rFunctions.get(fIndex++);
        System.out.println(rUpper.apply("FaaS")); // Shows "FAAS

        Predicate<String> rGt5Length = (Predicate<String>) rFunctions.get(fIndex++);
        System.out.println(rGt5Length.test("I am greater than 5")); // Shows true

    }

    private static ObjectInputStream codeStream(byte[] code) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(code);
        return new ObjectInputStream(bis);
    }

    private static byte[] saveFunction(List functions) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(functions);
        return bos.toByteArray();

    }

}
