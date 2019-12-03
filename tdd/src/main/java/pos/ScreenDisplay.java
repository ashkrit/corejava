package pos;

public class ScreenDisplay implements DisplayDevice {

    private String messageText;

    @Override
    public String message() {
        return messageText;
    }

    @Override
    public void onMessage(String message) {
        this.messageText = message;
    }

}
