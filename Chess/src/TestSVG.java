import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.*;

public class TestSVG {
    public static void main(String[] args) throws Exception {
    	String svgPath = "src/res/white_queen.svg";

        String outPath = "test_output.png";

        PNGTranscoder t = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(new FileInputStream(svgPath));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(out);

        t.transcode(input, output);

        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(out.toByteArray());
        fos.close();

        System.out.println("Success! Output file created: " + outPath);
        //test comment
    }
}
