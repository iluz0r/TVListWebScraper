import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Run {

	public static void main(String[] args) {
		try {
			Document homeDoc = Jsoup.connect("http://iptvsatlinks.blogspot.it/").get();
			Elements aElements = homeDoc.getElementsByClass("post-title entry-title").select("a[href]");

			// Ottiene il link del daily post più recente
			String dailyListLink = aElements.get(0).attr("href");

			Document dailyPostDoc = Jsoup.connect(dailyListLink).get();
			Elements divElements = dailyPostDoc.getElementsByClass("code");

			String[] linksList = divElements.get(0).ownText().split(" ");

			int numLists = 1;
			for (String s : linksList) {
				if (s.startsWith("http"))
					downloadFileFromURL(s, numLists++ + ".m3u");
			}

			BufferedReader br = null;
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("Lista.m3u")));
			for (int i = 1; i < numLists; i++) {
				br = new BufferedReader((new FileReader(new File(i + ".m3u"))));

				String line;
				while (((line = br.readLine()) != null)) {
					if (line.contains("IT:")) {
						bw.write(line + "\n");
						bw.write(br.readLine() + "\n");
					}
				}
			}
			br.close();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void downloadFileFromURL(String urlString, String filePath) {
		try {
			URL website = new URL(urlString);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
