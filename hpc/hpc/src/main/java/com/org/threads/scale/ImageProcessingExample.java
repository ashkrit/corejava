package com.org.threads.scale;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.*;
import java.util.Map;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.stream.Collectors;

record ImageResult(String url, ByteBuffer buffer) {
}

public class ImageProcessingExample {

    public static void main(String[] args) {

        //bruteForce();
        structureTask();
    }



    private static void structureTask() {
        try (var scope = StructuredTaskScope.open(); var client = HttpClient.newBuilder().build()) {


            Subtask<ImageResult> subtask1 = scope.fork(() -> fetchImage(client, "https://fastly.picsum.photos/id/1/200/300.jpg?hmac=jH5bDkLr6Tgy3oAg5khKCHeunZMHq0ehBZr6vGifPLY"));
            Subtask<ImageResult> subtask2 = scope.fork(() -> fetchImage(client, "https://fastly.picsum.photos/id/2/200/300.jpg?hmac=HiDjvfge5yCzj935PIMj1qOf4KtvrfqWX3j4z1huDaU"));

            scope.join();

            var result = subtask2.get().buffer().remaining() + subtask1.get().buffer().remaining();
            System.out.println(result);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void bruteForce() {
        var urls = List.of(
                "https://fastly.picsum.photos/id/1/200/300.jpg?hmac=jH5bDkLr6Tgy3oAg5khKCHeunZMHq0ehBZr6vGifPLY",
                "https://fastly.picsum.photos/id/2/200/300.jpg?hmac=HiDjvfge5yCzj935PIMj1qOf4KtvrfqWX3j4z1huDaU",
                "https://fastly.picsum.photos/id/3/200/300.jpg?hmac=o1-38H2y96Nm7qbRf8Aua54lF97OFQSHR41ATNErqFc"
        );

        Map<String, ByteBuffer> images = downloadImages(urls);
        images.forEach((url, buf) ->
                System.out.printf("%-50s -> %d bytes%n", url, buf.remaining()));
    }

    /**
     * Downloads images from the given URLs in parallel using virtual threads.
     *
     * @param urls list of image URLs to fetch
     * @return map of URL -> ByteBuffer containing the raw image bytes
     */
    public static Map<String, ByteBuffer> downloadImages(List<String> urls) {


        try (var executor = Executors.newVirtualThreadPerTaskExecutor();
             var client = HttpClient.newBuilder()
                     .executor(executor)
                     .build()) {

            List<Callable<ImageResult>> tasks = urls
                    .stream()
                    .map(url -> toCallable(url, client))
                    .toList();

            // invokeAll blocks until every task finishes
            var futures = executor.invokeAll(tasks);

            return futures
                    .stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toMap(ImageResult::url, ImageResult::buffer));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private static Callable<ImageResult> toCallable(String url, HttpClient client) {
        return () -> fetchImage(client, url);
    }

    private static ImageResult fetchImage(HttpClient client, String url)
            throws IOException, InterruptedException {

        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("Unexpected status " + response.statusCode() + " for " + url);
        }

        return new ImageResult(url, ByteBuffer.wrap(response.body()));
    }
}
