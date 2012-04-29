/**
 * 
 */
package management;

import interfaces.wavFile.WavFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
/* (Currently) unused imports
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.InputStream;
 */
import java.util.HashMap;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/* (Currently) unused Imports 
 import javax.sound.sampled.AudioFileFormat;
 import javax.sound.sampled.AudioInputStream;
 import javax.sound.sampled.AudioSystem;

 import com.sun.media.sound.WaveFileWriter;
 */

import container.Category;
import container.Segment;
import container.Session;

/**
 * Liest und schreibt XML-Dateien. Exportiert komplette Sessions als WAVE-Datei.
 * Stellt Beat-Templates fuer den Casual-Modus zur Verfuegung
 * 
 * @author Magnus Bruehl, Ulrich Ahrendt
 * 
 */
public class FileManager {
	// Attribute
	private HashMap<String, Category> categories;
	private Session activeSession;

	// Konstruktor
	public FileManager() {
		categories = new HashMap<String, Category>();
	}

	// Getter & Setter
	public HashMap<String, Category> getCategories() {
		return categories;
	}

	public void setCategories(HashMap<String, Category> categories) {
		this.categories = categories;
	}

	public void setActiveSession(Session activeSession) {
		this.activeSession = activeSession;
	}

	public Session getActiveSession() {
		return activeSession;
	}

	// Weitere Methoden
	public void addCategory(Category category) {
		categories.put(category.toString(), category);
	}

	public void removeCategory(int index) {
		this.categories.remove(index);
	}

	/**
	 * Writes a Session into an XML File into the ressources/session directory
	 * 
	 * @param session
	 */
	public void writeSession(Session session) {
		// creating xstream object
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("Session", Session.class);
		String xml = xstream.toXML(session);
		// writes string in file
		FileWriter writer;
		try {
			writer = new FileWriter("./src/resources/sessions/"
					+ activeSession.getName() + "xml"); // get Session Name
														// onClick?
			writer.write(xml);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	/**
	 * writes the Hashmap with all categories and sessions into the ressources
	 * directory
	 * 
	 * @param categories
	 */

	public void writeCategories(HashMap<String, Category> categories) {
		// creating xstream object
		XStream xstream = new XStream(new DomDriver());
		String xml = xstream.toXML(categories);
		// writes string in file
		FileWriter writer;
		try {
			writer = new FileWriter("./src/resources/categories.xml");
			writer.write(xml);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	/**
	 * Exportiert die aktuelle Session als WAVE-File. Die Samplerate betraegt
	 * 44.1KHz, die Bittiefe betraegt 16.
	 */
	// TODO: Mix mit Hintergrundklang
	public void exportAsWav() {
		try {
			int sampleRate = 44100; // Samples pro Sekunde
			double duration = activeSession.getDuration(); // Sekunden

			// Berechne die Anzahl Frames, die fuer die angegebene Dauer
			// benoetigt wird
			long numFrames = (long) (duration * sampleRate);

			// Erstelle ein wav-file mit dem Namen, der durch den Benutzer
			// spezifiziert wurde
			// TODO: Hier den User-spezifizierten Filename angeben
			WavFile wavFile = WavFile.newWavFile(new File("sineonly.wav"), 2,
					numFrames, 16, sampleRate);

			// Erstelle einen grosszuegigen Buffer von 100 frames
			double[][] buffer = new double[2][100];

			// Loop ueber alle Segmente
			for (int curSeg = 0; curSeg < activeSession.getNumerOfSegments(); curSeg++) {

				// Hole das aktuelle Segment
				Segment activeSegment = activeSession.getSegments().get(curSeg);

				// Berechne die Anzahl Frames, die fuer das aktuelle Segment
				// benoetigt wird
				numFrames = (long) (activeSegment.getDuration() * sampleRate);

				// Initialisiere lokalen Frame-Zaehler
				long frameCounter = 0;

				// Loop, bis alle Frames geschrieben wurden
				while (frameCounter < numFrames) {
					// Bestimme die maximal zu schreibende Anzahl an Frames
					long remaining = numFrames - frameCounter;
					int toWrite = (remaining > 100) ? 100 : (int) remaining;

					// Fuelle den Buffer. Ein Ton pro Stereokanal
					for (int s = 0; s < toWrite; s++, frameCounter++) {
						// Hole die aktuelle Frequenz aus dem aktuellen Segment
						// TODO: Funktion, die die Frequenz ueber einen Zeitraum
						// anpasst, also slowdown oder wakeup
						int freq1 = activeSegment.getBeat().getFreq1_start();
						int freq2 = activeSegment.getBeat().getFreq2_start();

						buffer[0][s] = Math.sin(2.0 * Math.PI * freq1
								* frameCounter / sampleRate);
						buffer[1][s] = Math.sin(2.0 * Math.PI * freq2
								* frameCounter / sampleRate);

					}

					// Schreibe den Buffer
					wavFile.writeFrames(buffer, toWrite);
				}
			}

			// Schliesse das wavFile
			wavFile.close();
		} catch (Exception e) {
			System.err.println(e);
		}

	}

	/**
	 * Liest eine Session aus einem XML-File ein.
	 * 
	 * @param sessionName
	 *            Dateiname der einzulesenden Session
	 * @return Die aus dem XML-File eingelesene Session-Klasse
	 */
	public Session readSession(String sessionName) {
		String sessionString = null;
		try {
			sessionString = readFileAsString("./src/resources/sessions"
					+ categories + "sessionName" + ".xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		XStream xstream = new XStream(new DomDriver());
		Session session1 = new Session();
		session1 = (Session) xstream.fromXML(sessionString);
		return session1;
	}

	/**
	 * Liest Kategorien aus einem XML-File.
	 * 
	 * @throws IOException
	 */
//	public Category readCategories() throws IOException {
//		String categoriesString = readFileAsString("./src/resources/categories.xml");
//		XStream xstream = new XStream(new DomDriver());
//		Category categories = new Category("Empty");
//		categories = (Category) xstream.fromXML(categoriesString);
//		return categories;
//	}
	public HashMap<String, Category> readCategories() throws IOException {
		String categoriesString = readFileAsString("./src/resources/categories.xml");
		XStream xstream = new XStream(new DomDriver());
		HashMap<String, Category> categories = new HashMap<String, Category>();
		categories = (HashMap<String, Category>) xstream.fromXML(categoriesString);
		return categories;
	}

	/**
	 * Liest ein File als String. Notwendig fuer den XML-Import.
	 * 
	 * @param filePath
	 * @return String, der an den XML-Import uebergeben wird.
	 * @throws java.io.IOException
	 */
	private static String readFileAsString(String filePath)
			throws java.io.IOException {
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		} finally {
			if (f != null)
				try {
					f.close();
				} catch (IOException ignored) {
				}
		}
		return new String(buffer);
	}
}
