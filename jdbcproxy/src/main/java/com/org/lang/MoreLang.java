package com.org.lang;

public class MoreLang {

    public static void safeExecuteV(BubbleExceptionFunction f) {

        try {
            f.accept();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @FunctionalInterface
    public interface BubbleExceptionFunction {
        void accept() throws Exception;
    }

}
