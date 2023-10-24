package vector.fasttext;

import com.github.jfasttext.JFastText;

public class FastTextApp {

    public static void main(String[] args) {
        var jft = new JFastText();
        var index = 0;
        var input = args[index++];
        var output = args[index++];

        jft.runCmd(new String[]{
                "skipgram",
                "-input", input,
                "-output", output,
                "-bucket", "100",
                "-minCount", "1"
        });

        jft.loadModel(output +".bin");

        /*
        String text = "Football games usually?";
        JFastText.ProbLabel probLabel = jft.(text);
        System.out.printf("\nThe label of '%s' is '%s' with probability %f\n",
                text, probLabel.label, Math.exp(probLabel.logProb));

         */

        System.out.println(jft.getWords());

    }
}
