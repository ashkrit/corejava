package org.refactoring.gson;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.refactoring.Invoices;
import org.refactoring.Plays;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GsonMessageTest {


    @Test
    public void create_play_object_from_gson() throws URISyntaxException, IOException {

        URI path = this.getClass().getResource("/plays.json").toURI();
        Path location = Paths.get(path);

        String data = new String(Files.readAllBytes(location));

        Plays plays = GsonMessage.from(data, Plays.class);

        Assertions.assertSame(3, plays.plays.size());
    }


    @Test
    public void create_invoices_object_from_gson() throws URISyntaxException, IOException {

        URI path = this.getClass().getResource("/invoices.json").toURI();
        Path location = Paths.get(path);

        String data = new String(Files.readAllBytes(location));

        Invoices invoices = GsonMessage.from(data, Invoices.class);

        Assertions.assertSame(3, invoices.orders.get(0).performances.size());
    }

}
