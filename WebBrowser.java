import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
// Copyright 2007 by Chrix - visit www.funsciencewithyourcomputer.org
public class WebBrowser {

	public static void main(String[] args) {
		WebBrowser browser = new WebBrowser();
		while (true) {
			String url = browser.askUrl();
			String html = browser.download(url);
			String document = browser.parse(html);
			browser.display(document);
		}
	}

	List urls = new ArrayList();

	public String askUrl() {
		while (true) {
			System.out.println();
			System.out.println("Please enter url: ");
			Scanner keyboard = new Scanner(System.in);
			String url = keyboard.nextLine();
			if (url.startsWith("http"))
				return url;
			int link = Integer.parseInt(url);
			if (link < urls.size()) {
				return (String) urls.get(link);
			}
		}
	}

	public String download(String url) {
		System.out.println("Loading " + url);
		String rawDocument = "";
		byte[] buffer = new byte[1024];
		try {
			BufferedInputStream inputStream = new BufferedInputStream(new URL(url)
					.openStream());
			while (true) {
				int length = inputStream.read(buffer);
				if (length == -1)
					break;
				rawDocument = rawDocument + new String(buffer, 0, length);
			}
			return rawDocument;
		} catch (Exception e) {
			return "<body><p>Failed to download " + url + "</p></body>";
		}
	}

	public String parse(String html) {
		int tagBegin = html.indexOf("<body>"), tagEnd = 0;
		String text = "";
		String tag = "";
		while (true) {
			tagEnd = html.indexOf('>', tagBegin + 1);
			if (tagEnd == -1)
				break;
			tagBegin = html.indexOf('<', tagEnd + 1);
			if (tagBegin == -1)
				break;

			if (isParagraphTag(tag)) {
				String content = html.substring(tagEnd + 1, tagBegin);
				content = removeWhiteSpaces(content);
				text += "\r\n\r\n" + content;
			}
			if (isHyperlinkTag(tag)) {
				String content = html.substring(tagEnd + 1, tagBegin);
				content = removeWhiteSpaces(content);
				String link = parseHyperlink(tag);
				if (content.length() > 0 && link.length() > 0) {
					text += " [" + urls.size() + " " + content + "] ";
					urls.add(link);
				}
			}
			tag = getTag(html, tagBegin);
		}
		return text + " ";
	}

	String parseHyperlink(String tag) {
		String link = tag.substring(tag.indexOf('"') + 1, tag.indexOf('"', tag
				.indexOf('"') + 1));
		if (!link.startsWith("http:"))
			link = "";
		return link;
	}

	String getTag(String rawDocument, int tagBegin) {
		return rawDocument.substring(tagBegin);
	}

	boolean isParagraphTag(String tag) {
		return tag.startsWith("<p");
	}

	boolean isHyperlinkTag(String tag) {
		return tag.startsWith("<a href");
	}

	String removeWhiteSpaces(String content) {
		return content.trim().replaceAll("\r", "").replaceAll("\n", "");
	}

	String breakUpIntoLines(String longText) {
		int max = 80;
		String result = "";
		int canBreakIndex = max;
		int newLineStart = 0;
		for (int index = 0; index < longText.length(); index++) {
			char character = longText.charAt(index);
			if (character == ' ' || character == '.')
				canBreakIndex = index;
			if (character == '\r' || character == '\n') {
				String newLine = longText.substring(newLineStart, index);
				result += newLine + "\r\n";
				do {
					index = index + 1;
					character = longText.charAt(index);
				} while (index < longText.length()
						&& (character == '\r' || character == 'n' || character == ' '));
				newLineStart = index;
				canBreakIndex = newLineStart + max;
			}
			if (index - newLineStart == max) {
				String newLine = longText.substring(newLineStart, canBreakIndex);
				newLineStart = canBreakIndex;
				canBreakIndex = newLineStart + max;
				result += newLine + "\r\n";
			}
		}
		result += longText.substring(newLineStart);
		return result;
	}

	public void display(String document) {
		String multiLines = breakUpIntoLines(document);
		System.out.println(multiLines);
	}
}