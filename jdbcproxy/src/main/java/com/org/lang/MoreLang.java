package com.org.lang;

public class MoreLang {

    public static void safeExecuteV(BubbleExceptionVoidFunction f) {

        try {
            f.accept();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static <T> T safeExecute(BubbleExceptionFunction<T> f) {

        try {
            return f.accept();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @FunctionalInterface
    public interface BubbleExceptionVoidFunction {
        void accept() throws Exception;
    }

    @FunctionalInterface
    public interface BubbleExceptionFunction<T> {
        T accept() throws Exception;
    }

}
