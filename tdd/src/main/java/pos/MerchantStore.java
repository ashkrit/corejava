package pos;

public class MerchantStore {
    private final Display display;

    public MerchantStore(Display display) {
        this.display = display;
    }

    public void onBarCode(String barCode) {
        if (barCode == null) {
            this.display.setText("invalid scan");
        } else if ("".equals(barCode)) {
            this.display.setText("empty barcode");
        } else {
            if ("1234567".equals(barCode)) {
                this.display.setText("10.99");
            } else if ("234567".equals(barCode)) {
                this.display.setText("11.99");
            } else {
                this.display.setText("product not found " + barCode);
            }
        }
    }
}
