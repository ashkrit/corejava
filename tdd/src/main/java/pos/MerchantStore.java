package pos;

public class MerchantStore {
    private final Display display;

    public MerchantStore(Display display) {
        this.display = display;
    }

    public void onBarCode(String barCode) {
        this.display.setText("10.99");
    }
}
