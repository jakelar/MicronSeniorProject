package voice.analysis;


import java.io.File;
import java.io.IOException;

//import voice.analysis.VoiceAnalysis;
import voice.api.VoiceMetaData;

public class Test {
	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\Alex\\IdeaProjects\\CS471-F17-Micronophones\\10001-90210-01803.wav");
		VoiceAnalysis va = new VoiceAnalysis();
		System.out.println("Starting Test...");
		VoiceMetaData vData = new VoiceMetaData();
		String phrase = "one zero zero zero one";
		vData = va.analyze(file, phrase, vData);
		va.getVoiceScore(vData);
	}
}
