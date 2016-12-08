import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Run2 {

	public static void main(String[] args) {
		String lastData = "";
		
		while (true) {
			try {
				Document homeDoc = Jsoup
						.connect("https://www.iptvsource.com/category/europe-iptv-lists/italy-iptv-lists/")
						.userAgent(
								"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
						.get();
				Elements aElements = homeDoc.getElementsByClass("entry-title td-module-title").select("a[href]");
				String data = aElements.get(0).attr("title").split(" ")[5];

				if (!lastData.equals(data)) {
					String dailyListLink = aElements.get(0).attr("href");
					String title = "Lista " + data;
					Document dailyPostDoc = Jsoup.connect(dailyListLink)
							.userAgent(
									"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
							.get();
					Elements divElements = dailyPostDoc.getElementsByClass("alt2");
					BufferedWriter bw = new BufferedWriter(new FileWriter(new File(title + ".m3u")));
					bw.write(divElements.get(0).ownText());
					bw.close();
					lastData = data;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				System.out.println("Attendo 4 minuti");
				Thread.sleep(4 * 60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
