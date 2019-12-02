package pos;

public class ScreenDisplay implements DisplayDevice {

    private String messageText;

    @Override
    public String message() {
        if (messageText != null) {
            return messageText;
        }
        return "scan again";
    }

    @Override
    public void onMessage(String message) {
        this.messageText = message;
    }

}
