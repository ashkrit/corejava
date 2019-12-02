package pos;

public class MerchantStore {
    private final DisplayDevice display;

    public MerchantStore(DisplayDevice display) {
        this.display = display;
    }

    public void onBarCode(String barCode) {
        if (barCode != null && barCode.trim().length() > 0) {
            this.display.onMessage("invalid barcode");
        }
    }
}
