package proxy.fx.impl;


import proxy.fx.FXService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

public class FXServiceAPI implements FXService {

    private final String ratesApi;
    private final int delay;


    public FXServiceAPI(String serviceEndPoint, int delay) {

        this.ratesApi = String.format("%s/convert", serviceEndPoint);
        this.delay = delay;
    }

    @Override
    public double convert(String from, String to, int amount) {


        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(delay));

            HttpURLConnection con = createConnection(from, to, amount);
            double value = parse(connect(con));
            con.disconnect();
            return value;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private HttpURLConnection createConnection(String from, String to, int amount) throws IOException {
        URL u = new URL(String.format("%s?from=%s&to=%s&amount=%s&format=tsv", ratesApi, from, to, amount));
        HttpURLConnection con = (HttpURLConnection) u.openConnection();
        con.setRequestMethod("GET");
        return con;
    }


    private static StringBuilder connect(HttpURLConnection con) throws IOException {
        InputStream in = con.getInputStream();
        int b = -1;
        StringBuilder sb = new StringBuilder();
        while ((b = in.read()) != -1) {
            sb.append((char) b);
        }
        return sb;
    }

    private static double parse(StringBuilder message) {
        System.out.println(message);
        String valueRow = message.toString().split("\n")[1];
        String[] valueFields = valueRow.split("\t");
        String rate = valueFields[valueFields.length - 1];
        return Double.parseDouble(rate.replace(",", "."));
    }
}
