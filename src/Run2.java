import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Run2 {

	public static void main(String[] args) {
		String lastData = "31-01-2017";

		while (true) {
			try {
				Document homeDoc = Jsoup
						.connect("https://www.iptvsource.com/category/europe-iptv-lists/italy-iptv-lists/")
						.userAgent(
								"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
						.get();
				Elements aElements = homeDoc.getElementsByClass("entry-title td-module-title").select("a[href]");
				int numTitleWords = aElements.get(0).attr("title").split(" ").length;
				String data = aElements.get(0).attr("title").split(" ")[numTitleWords - 1];

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
					uploadListToFtpServer();
					lastData = data;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(4 * 60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void uploadListToFtpServer() throws IOException {
		File dir = new File(System.getProperty("user.dir"));
		File[] m3uLists = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".m3u");
			}
		});
		String ftpUrl = "ftp://%s:%s@%s/%s;type=i";
		String host = "testprova.net23.net";
		String user = "a4560590";
		String pass = "missdepp90";

		for (File list : m3uLists) {
			String uploadPath = "public_html/bestrepo/PREMIUM/" + list.getName();

			String formattedFtpUrl = String.format(ftpUrl, user, pass, host, uploadPath);
			System.out.println(getTimestamp() + " Upload URL: " + formattedFtpUrl);

			URL url = new URL(formattedFtpUrl);
			URLConnection conn = url.openConnection();
			OutputStream outputStream = conn.getOutputStream();
			FileInputStream inputStream = new FileInputStream(list.getName());

			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outputStream.close();

			System.out.println(getTimestamp() + " File " + list.getName() + " uploaded");
		}
	}

	private static String getTimestamp() {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		return "[" + df.format(new Date()) + "]";
	}

}
