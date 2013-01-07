package com.amazonbird.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.map.HashedMap;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import au.id.jericho.lib.html.Source;

import com.amazonbird.util.bitly.BitlyResponse;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.GoogleKeyInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.model.Url;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;

public class Util {
	private static final Gson gson = new Gson();
	private static final long SEC_MS = 1000;
	private static final long MIN_MS = 60 * SEC_MS;
	private static final long HOUR_MS = 60 * MIN_MS;
	private static final long DAY_MS = 24 * HOUR_MS;
	private static final long WEEK_MS = 7 * DAY_MS;
	private static final long MONTH_MS = 4 * WEEK_MS; // assume 1 month is 4
														// week;
	private static final long YEAR_MS = 12 * MONTH_MS;

	public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	private static Util instance = new Util();
	private static Logger logger = Logger.getLogger(Util.class);

	// SHORTENER--
	public static String[] key = new String[] { "AIzaSyDj8IptCTCTJhQTibwUe-c70o2qfTUjlOQ", "AIzaSyAXcU_dfWO5ZPewK9KQtGs5h_1d3ghQrAo" };

	private static int keyIndex = 0;
	// --SHORTENER

	private String contextPath = "";

	private Util() {
	}

	public static Util getInstance() {
		return instance;
	}

	static Urlshortener newUrlshortener() {
		JsonHttpRequestInitializer credential = new GoogleKeyInitializer(key[keyIndex]);
		return new Urlshortener.Builder(new NetHttpTransport(), new JacksonFactory(), null).setApplicationName("gujum/1.0").setJsonHttpRequestInitializer(credential).build();
	}

	private static Url shortenURL(String longUrl) {
		Urlshortener shortener = newUrlshortener();
		Url toInsert = new Url().setLongUrl(longUrl);
		Url result = null;
		try {
			result = shortener.url().insert(toInsert).execute();
		} catch (GoogleJsonResponseException e) {
			logger.error(e.getMessage(), e);

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		boolean error = false;

		if (result == null) {
			logger.error("Unable to shorten url :" + longUrl);
			error = true;
		} else if (result.getId() == null) {
			logger.error("Unable to shorten url :" + longUrl + " result: " + result);
			error = true;
		}

		if (error) {
			useGoogleForURLShortening = false;
			// logger.info("Error occurred... Changing goo.gl api key...");
			// keyIndex++;
			// keyIndex = keyIndex % KEY_COUNT;
		}
		return result;
	}

	public static String getResponseString(HttpResponse response) {
		String responseContent = "";
		try {

			InputStream input = response.getEntity().getContent();
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);

			String line = null;
			while ((line = br.readLine()) != null) {
				responseContent = responseContent + "\n" + line;
			}
		} catch (Exception ex) {
			logger.error(ex);
		}
		return responseContent;
	}

	static boolean useGoogleForURLShortening = false;

	public String shortenURLString(String longUrl) {
		if (useGoogleForURLShortening) {
			return shortenURLByGoogl(longUrl);
		} else {
			return shortenURLByBitly(longUrl);
		}
	}

	private static String shortenURLByBitly(String longUrl) {
		String url = null;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("gujum1", "R_3bb25f225bedf407c49391046f4de415");
		int keySetSize = map.keySet().size();
		String key = map.keySet().toArray(new String[] {})[(int) (Math.random() * keySetSize)];
		URL bitLy;

		if (!longUrl.startsWith("http://")) {
			longUrl = "http://" + longUrl;
		}

		try {
			bitLy = new URL("https://api-ssl.bitly.com/v3/shorten?login=" + key + "&apiKey=" + map.get(key) + "&longUrl=" + URLEncoder.encode(longUrl, "UTF-8"));
			BufferedReader in = new BufferedReader(new InputStreamReader(bitLy.openStream()));

			String inputLine;
			StringBuilder sb = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine);
			}
			in.close();
			String response = sb.toString();
			if (response.contains("\"status_code\": 200")) {
				BitlyResponse bitlyResonse = gson.fromJson(sb.toString(), BitlyResponse.class);
				url = bitlyResonse.getData().getUrl();
			}
		} catch (Exception e) {

			logger.error(e);
			logger.info("using goo.gl for url shortener...");
			useGoogleForURLShortening = true;
		}
		return url;
	}

	private String shortenURLByGoogl(String longUrl) {
		Url shortenUrl = shortenURL(longUrl);
		if (shortenUrl != null) {
			return shortenUrl.getId();
		} else {
			return null;
		}
	}

	public static String dateDiff2String(Date date) {
		Date now = Calendar.getInstance().getTime();
		long diff = now.getTime() - date.getTime();
		if (diff / MIN_MS == 0) {
			return diff / SEC_MS > 1 ? (diff / SEC_MS) + " secs ago" : " just now";
		} else if (diff / HOUR_MS == 0) {
			return diff / MIN_MS > 1 ? (diff / MIN_MS) + " mins ago" : " one min ago";
		} else if (diff / DAY_MS == 0) {
			return diff / HOUR_MS > 1 ? (diff / HOUR_MS) + " hours ago" : " one hour ago";
		} else if (diff / WEEK_MS == 0) {
			return diff / DAY_MS > 1 ? (diff / DAY_MS) + " days ago" : " yesterday";
		} else if (diff / MONTH_MS == 0) {
			return diff / WEEK_MS > 1 ? (diff / WEEK_MS) + " weeks ago" : " one week ago";
		} else if (diff / YEAR_MS == 0) {
			return diff / MONTH_MS > 1 ? (diff / MONTH_MS) + " months ago" : " one month ago";
		} else {
			return diff / YEAR_MS > 1 ? (diff / YEAR_MS) + " years ago" : " one year ago";
		}
	}

	public static String getStringFromInputStream(InputStream is) {

		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		String content = "";
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				content = content + "\n" + line;
			}
		} catch (IOException e) {
			content = "";
		}
		return content;
	}

	public static String getShortenedString(String str, int maxChar) {

		String shortenedStr = str;
		if (str.length() > maxChar) {
			shortenedStr = str.substring(0, maxChar - 1) + "...";
		}
		return shortenedStr;
	}

	public void sleepIfNeeded(long startTime, long period) {
		long endTime = System.currentTimeMillis();
		long diff = endTime - startTime;

		if (diff < period) {
			try {
				Thread.sleep(period - diff);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}

	public String getTargetUrl(HttpServletRequest req) {

		String ref = req.getParameter("url");

		return ref;

	}

	public static boolean isListValid(List<?> arr) {

		return arr != null && arr.size() > 0;

	}

	public boolean listIsValid(List<?> list) {

		return list != null && list.size() > 0;

	}

	public static boolean stringIsValid(String str) {

		return str != null && str.length() > 0;

	}

	public String addHttpPrefix(String url) {

		if (!url.startsWith("http://")) {

			return "http://" + url;
		} else {
			return url;
		}
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String path) {
		contextPath = path;
	}

	public Date stringToDate(String dateStr) {

		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {

			return null;
		}

	}

	public String dateToString(Date date) {
		return sdf.format(date);
	}

	public String timeToString(long time) {
		return sdf.format(new Date(time));
	}

	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

	}

	public static boolean isSolaris() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("sunos") >= 0);
	}

	public static String getURLContent(String urlStr) {

		Source source = null;
		String sourceString = null;

		URL url = null;
		try {
			url = new URL(urlStr);

			source = new Source(url);
			sourceString = source.toString();
			sourceString = sourceString.replace("\n", "");
			sourceString = sourceString.replace("\r", "");
			sourceString = sourceString.replace("\t", "");
		} catch (Exception e) {
			logger.info(e.getMessage());
		}

		return sourceString;

	}

	public static String parseImg(String sourceString) {
		String img = null;
		try {
			img = sourceString.split("id=\"prodImageCell\"")[1].split("</div>")[0].split("src=\"")[1].split("\" ")[0];
		} catch (Exception e) {
			// Try another regex
		}
		if (img != null) {
			return img;
		}
		try {
			img = sourceString.split("<img id=\"main-image\" src=\"")[1].split("\"")[0];
		} catch (Exception e) {
			// Try another regex
		}
		return img;
	}

	public static String parse(String sourceString, String prefix, String suffix, String contentStartString, String contentEndString) {

		return parse(sourceString, prefix, suffix, contentStartString, contentEndString, null, null).get(0);
	}

	public static List<String> parse(String sourceString, String prefix, String suffix, String contentStartString, String contentEndString, String itemSeparator) {

		return parse(sourceString, prefix, suffix, contentStartString, contentEndString, itemSeparator, null);
	}

	public static List<String> parse(String sourceString, String prefix, String suffix, String contentStartString, String contentEndString, String itemSeparator, String itemSplitter) {

		String[] returnArr = new String[0];
		String returnValue = "";
		if (contentStartString != null && contentStartString != "") {
			String[] result = sourceString.split(contentStartString);

			if (result.length > 1) {
				sourceString = result[1];
			}
		}
		if (contentEndString != null && contentEndString != "") {
			String[] result = sourceString.split(contentEndString);

			if (result.length > 1) {
				sourceString = result[0];
			}
		}

		ArrayList<String> retList = new ArrayList<String>();
		if (stringIsValid(itemSeparator)) {
			String[] itemArr = sourceString.split(itemSeparator);
			for (int i = 0; i < itemArr.length; i++) {
				if (i == 0)
					continue;

				String value = getBetween(itemArr[i], prefix, suffix);
				if (stringIsValid(value)) {

					retList.add(value);
				}
			}
		} else {
			retList.add(getBetween(sourceString, prefix, suffix));
		}

		return retList;
	}

	public static String getBetween(String stringToParse, String prefix, String suffix) {

		String temp1 = getBetweenBasic(stringToParse, prefix, suffix);
		String temp2 = removeTags(temp1);
		temp2 = temp2.trim();

		return temp2;
	}

	public static String getBetweenBasic(String stringToParse, String prefix, String suffix) {

		String temp1 = "";

		if (stringToParse.split(prefix).length > 1) {
			temp1 = stringToParse.split(prefix)[1].split(suffix)[0];
		}

		return temp1;
	}

	public static String removeTags(String text) {
		int indexOfAB1;
		int indexOfAB2;
		String editText = new String(text);
		boolean tagFound = true;

		while (tagFound) {
			indexOfAB1 = editText.indexOf("<");
			indexOfAB2 = editText.indexOf(">");

			if (indexOfAB1 < 0 && indexOfAB2 >= 0 && editText.length() - 1 > indexOfAB2 || indexOfAB1 > indexOfAB2 && indexOfAB2 >= 0) {

				editText = editText.substring(indexOfAB2 + 1);
				tagFound = true;

			} else if (indexOfAB1 >= 0 && indexOfAB1 < indexOfAB2) {
				tagFound = true;
				editText = editText.substring(0, indexOfAB1) + editText.substring(indexOfAB2 + 1);
			} else {
				tagFound = false;
			}
		}
		return editText;
	}

	public String formattedMoneyString(double amount) {
		DecimalFormat decimalFormatter = new DecimalFormat("#,###,###.00");
		String formatted = decimalFormatter.format(amount);
		if (formatted.startsWith(".")) {
			formatted = "0" + formatted;
		} else if (formatted.startsWith("-.")) {
			formatted = formatted.replace("-.", "-0.");
		}
		return formatted;
	}

	public double getEstimatedIncomePerAnnounce(double unitPrice, double click, double announcement) {
		double orderPerClick = 0.04;
		return (orderPerClick * click / (announcement == 0 ? 1 : announcement)) * unitPrice;

	}

	public String getEscapedHtmlAttribute(String value) {
		if (stringIsValid(value)) {
			return value.replace("\"", "&quot;").replace("'", "&apos;");
		}
		return "";
	}

	public static Object convertJSONToObj(String json, Class<? extends Object> clazz) {
		ObjectMapper om = new ObjectMapper();
		Object obj = null;
		try {
			obj = om.readValue(json, clazz);
		} catch (Exception e) {
			logger.error(e);
		}
		return obj;
	}

	public static List<? extends Object> convertJSONToObjList(String json, Class<? extends Object> clazz) {
		ObjectMapper om = new ObjectMapper();

		List<Object> objList = null;
		try {
			TypeFactory typeFactory = TypeFactory.defaultInstance();
			objList = om.readValue(json, typeFactory.constructCollectionType(List.class, clazz));

		} catch (JsonParseException e) {
			logger.error(e);
		} catch (JsonMappingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		return objList;
	}

	public static String getBase64(File file) {

		try {
			/*
			 * Reading a Image file from file system
			 */
			FileInputStream imageInFile = new FileInputStream(file);
			byte imageData[] = new byte[(int) file.length()];
			imageInFile.read(imageData);
			return new String(imageData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
		return "";

	}

	public static String getRandomTweet() {
		String content = getURLContent("http://watchout4snakes.com/CreativityTools/RandomSentence/RandomSentence.aspx");
		// String content =
		// getURLContent("http://www.users.globalnet.co.uk/~pennck/random.htm");

		String sentence = parse(content, "tmpl_main_lblSentence", "</span>", null, null);
		return sentence;
	}

	public static String shuffleString(String str) {
		List<String> letters = Arrays.asList(str.split(""));
		Collections.shuffle(letters);
		String shuffled = "";
		for (String letter : letters) {
			shuffled += letter;
		}
		return shuffled;
	}

	public static void stopLoading(WebDriver client) {
		((JavascriptExecutor) client).executeScript("window.stop();", new Object[] {});
	}

	public static void main(String[] args) {
		String result = Util.shortenURLByBitly("www.gujum.com");
		System.err.println(result);
	}
}
