package pos;

public class MerchantStore {
    private final DisplayDevice display;

    public MerchantStore(DisplayDevice display) {
        this.display = display;
    }

    public void onBarCode(String barCode) {
        if (barCode != null && barCode.trim().length() > 0) {
            if (barCode.equals("123001")) {
                this.display.onMessage("$10.99");
            } else {
                this.display.onMessage("invalid barcode");
            }
        }
    }
}
