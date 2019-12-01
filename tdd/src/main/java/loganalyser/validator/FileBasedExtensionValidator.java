package loganalyser.validator;

public class FileBasedExtensionValidator implements ExtensionValidator {
    @Override
    public boolean isValid(String fileName) {
        if (!fileName.toLowerCase().endsWith(".slf")) {
            return false;
        }
        return true;
    }
}