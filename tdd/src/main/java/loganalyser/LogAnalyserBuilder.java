package loganalyser;

import loganalyser.validator.ExtensionValidator;

public class LogAnalyserBuilder {


    private ErrorService errorService;
    private ExtensionValidator validator;
    private EmailService emailService;

    public LogAnalyserBuilder extensionValidator(ExtensionValidator validator) {
        this.validator = validator;
        return this;
    }

    public LogAnalyserBuilder errorService(ErrorService validator) {
        this.errorService = validator;
        return this;
    }

    public LogAnalyzer build() {
        return new LogAnalyzer(errorService, validator, emailService);
    }

    public LogAnalyserBuilder emailService(EmailService emailService) {
        this.emailService = emailService;
        return this;
    }
}
