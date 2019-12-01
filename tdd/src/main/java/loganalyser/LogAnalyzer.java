package loganalyser;

import loganalyser.validator.ExtensionValidator;

public class LogAnalyzer {

    private final ExtensionValidator fileBasedExtensionValidator;
    private final ErrorService errorService;
    private final EmailService emailService;

    public LogAnalyzer(ErrorService errorService, ExtensionValidator extensionValidator, EmailService emailService) {
        this.fileBasedExtensionValidator = extensionValidator;
        this.errorService = errorService;
        this.emailService = emailService;
    }

    public boolean isValidLogFile(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("filename is required");
        }

        return fileBasedExtensionValidator.isValid(fileName);
    }


    public void analyze(String file) {

        if (file.length() < 10) {
            try {
                errorService.error("file name must be >= 10");
            } catch (Exception e) {
                emailService.sednEmail(e.getMessage());
            }
        }
    }

}
