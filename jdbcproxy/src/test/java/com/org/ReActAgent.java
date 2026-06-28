package com.org;

import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import java.util.*;

public class ReActAgent {
    private OpenAiService openAiService;
    private static final String OPENAI_API_KEY = "your-api-key-here";
    private static final String MODEL = "gpt-3.5-turbo-instruct";
    private List<String> history;
    private Map<String, String> externalKnowledge;
    private List<String> tools;

    public ReActAgent() {
        openAiService = new OpenAiService(System.getenv("OPENAI_TOKEN"));
        history = new ArrayList<>();
        externalKnowledge = new HashMap<>();
        initializeExternalKnowledge();
        initializeTools();
    }

    private void initializeExternalKnowledge() {
        externalKnowledge.put("weather", "The weather is sunny with a high of 75°F.");
        externalKnowledge.put("time", "The current time is 2:30 PM.");
        externalKnowledge.put("nearby_parks", "Central Park and Riverside Park are nearby.");
    }

    private void initializeTools() {
        tools = Arrays.asList(
                "search_weather(location): Get the current weather for a location",
                "search_time(location): Get the current time for a location",
                "search_nearby(type, location): Find nearby places of a certain type",
                "get_directions(start, end): Get directions between two locations"
        );
    }

    public void process(String task) {
        System.out.println("Task: " + task);
        history.clear();

        String prompt = buildPrompt(task);
        String response = getOpenAIResponse(prompt);

        while (!response.toLowerCase().contains("task completed")) {
            System.out.println(response);
            history.add(response);

            if (response.toLowerCase().contains("action:")) {
                String observation = performAction(response);
                history.add("Observation: " + observation);
            }

            prompt = buildPrompt(task);
            response = getOpenAIResponse(prompt);
        }

        System.out.println(response);
    }

    private String buildPrompt(String task) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Task: ").append(task).append("\n\n");
        promptBuilder.append("You have access to the following tools:\n");
        for (String tool : tools) {
            promptBuilder.append("- ").append(tool).append("\n");
        }
        promptBuilder.append("\nFollow the ReAct framework: Reason about the task, decide on an action using available tools, and observe the results. Use the format:\n");
        promptBuilder.append("Thought: [Your reasoning about the current situation]\n");
        promptBuilder.append("Action: [The tool to use, e.g., search_weather(New York)]\n");
        promptBuilder.append("Observation: [The result of the action]\n\n");
        promptBuilder.append("When the task is complete, finish with:\n");
        promptBuilder.append("Thought: I have completed the task\n");
        promptBuilder.append("Action: Task completed\n\n");
        promptBuilder.append("Here's an example:\n");
        promptBuilder.append("Thought: I need to check the weather before planning an outdoor activity.\n");
        promptBuilder.append("Action: search_weather(New York)\n");
        promptBuilder.append("Observation: The weather is sunny with a high of 75°F.\n");
        promptBuilder.append("Thought: Great weather for outdoor activities. Now I need to find a nearby park.\n");
        promptBuilder.append("Action: search_nearby(park, New York)\n");
        promptBuilder.append("Observation: Central Park and Riverside Park are nearby.\n");
        promptBuilder.append("Thought: I have all the information I need to plan the outdoor activity.\n");
        promptBuilder.append("Action: Task completed\n\n");
        promptBuilder.append("Now, let's work on the given task. Here's the history so far:\n");

        for (String entry : history) {
            promptBuilder.append(entry).append("\n");
        }

        promptBuilder.append("\nWhat's the next step?");

        return promptBuilder.toString();
    }

    private String performAction(String action) {
        String actionLower = action.toLowerCase();
        if (actionLower.contains("search_weather")) {
            return externalKnowledge.get("weather");
        } else if (actionLower.contains("search_time")) {
            return externalKnowledge.get("time");
        } else if (actionLower.contains("search_nearby") && actionLower.contains("park")) {
            return externalKnowledge.get("nearby_parks");
        } else if (actionLower.contains("get_directions")) {
            return "Directions: Head north on 5th Ave, turn right on Central Park South.";
        }
        return "Action not recognized or no data available.";
    }

    private String getOpenAIResponse(String prompt) {
        CompletionRequest completionRequest = CompletionRequest.builder()
                .model(MODEL)
                .prompt(prompt)
                .maxTokens(150)
                .n(1)
                .stop(Arrays.asList("Observation:", "Human:"))
                .build();

        List<CompletionChoice> choices = openAiService.createCompletion(completionRequest).getChoices();
        return choices.isEmpty() ? "" : choices.get(0).getText().trim();
    }

    public static void main(String[] args) {
        ReActAgent agent = new ReActAgent();
        agent.process("Plana trip to india");
    }
}