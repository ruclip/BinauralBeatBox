package container;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

//import logic.SessionWiedergabe;
import interfaces.wavFile.WavFile;

import java.io.File;

import management.FileManager;

public class ContainerTester {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// Testing getMood in connection with the enum
		BinauralBeat beat = new BinauralBeat(12, 14);
		System.out.println("Gewaehlte Stimmung: " + beat.getMood());

		/*
		 * SessionWiedergabe.playSession((int) beat.getFreq1_start(), (int)
		 * beat.getFreq2_start(), 1000);
		 */

		// Session session = new Session();
		// session.addSegment( new Segment(10, new BinauralBeat(500, 530)) );
		// session.addSegment( new Segment(40, new BinauralBeat(800, 830)) );
		// session.addSegment( new Segment(10, new BinauralBeat(500, 530)) );
		// Sessionww.faWiedergabe sessionWiedergabe = new
		// SessionWiedergabe(session);
		//
		// sessionWiedergabe.playSession((int) beat.getFreq1_start(),(int)
		// beat.getFreq2_start());

		/*
		 * for(int i = 0; i<1000; i++){
		 * SessionWiedergabe.playSession((int)beat.getFreq1_start(),
		 * (int)beat.getFreq2_start()); if(i%1000 == 0)
		 * System.out.println("Spielt..."); } System.out.println("Fertig.");
		 */

		// Test Categories
		Segment segment1 = new Segment(10, beat);
		Segment segment2 = new Segment(10, 30, 33);
		Segment slowdown = new Segment(10, 100, 30, 110, 40);

		Session session1 = new Session(
				"Hier koennte Ihr Hintergrundklang stehen", slowdown);
		session1.addSegment(segment1);

		Session session2 = new Session("Hintergrundklang", slowdown);
		session2.addSegment(segment2);

		Category category = new Category("Testkategorie", session1);
		category.addSession(session2);

		System.out.println(category.toString());

		// Test Wav-Export
		Segment steadySegment = new Segment(40, 155, 160);
		Session exportableSession = new Session("Hintergrundklang",
				steadySegment);

		exportableSession.addSegment(slowdown);
		exportableSession.addSegment(segment1);
		exportableSession.addSegment(segment2);
		// Erwartetes Resultat: eine Wav-Datei mit Laenge 70

		FileManager fm = new FileManager();
		fm.setActiveSession(exportableSession);
		System.out.println("Erstelle Wavefile...");
		fm.exportAsWav();
		System.out.println("Wavefile erfolgreich erstellt.");

		// XML Test
		XMLEncoder e = new XMLEncoder(new BufferedOutputStream(
				new FileOutputStream("Test.xml")));
		e.writeObject(exportableSession);
		// Teste Hintergrundklang-Pfad
		Session standardSession = new Session();
		System.out
				.println("----------- Hintergrundklang-Information --------------------");
		readWav(standardSession.getHintergrundklang());
		System.out
				.println("--------- Hintergrundklang-Information-Ende------------------");
	}

	public static void readWav(String path) {
		try {
			// Open the wav file specified as the first argument
			WavFile wavFile = WavFile.openWavFile(new File(path)); //hi Magnus, wie gehts?

			// Display information about the wav file
			wavFile.display();

			// Get the number of audio channels in the wav file
			int numChannels = wavFile.getNumChannels();

			// Create a buffer of 100 frames
			double[] buffer = new double[100 * numChannels];

			int framesRead;
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;

			do {
				// Read frames into buffer
				framesRead = wavFile.readFrames(buffer, 100);

				// Loop through frames and look for minimum and maximum value
				for (int s = 0; s < framesRead * numChannels; s++) {
					if (buffer[s] > max)
						max = buffer[s];
					if (buffer[s] < min)
						min = buffer[s];
				}
			} while (framesRead != 0);

			// Close the wavFile
			wavFile.close();

			// Output the minimum and maximum value
			System.out.printf("Min: %f, Max: %f\n", min, max);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

}
