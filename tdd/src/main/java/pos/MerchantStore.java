package pos;

public class MerchantStore {
    private final Display display;

    public MerchantStore(Display display) {
        this.display = display;
    }

    public void onBarCode(String barCode) {
        if ("1234567".equals(barCode)) {
            this.display.setText("10.99");
        } else if ("234567".equals(barCode)) {
            this.display.setText("11.99");
        }
    }
}
