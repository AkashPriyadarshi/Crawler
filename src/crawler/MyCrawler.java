package crawler;

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

class FetchStatistics {
	static int fetchAttempted = 0;
	static int fetchSucessful = 0;
	static int fetchfailed = 0;	  
}
class URLStatistics {
	static int totalUrlsExtracted = 0;
	static Set<String> totalUniqueUrls = new HashSet<String>();
	static Set<String> uniqueWithin = new HashSet<String>();
	static Set<String> uniqueOutside = new HashSet<String>();	
}
class PageStatistics {	
	static Set<String> uniqueStatusCode = new HashSet<String>();
	static Set<String> uniqueContentType = new HashSet<String>();
	static HashMap<String,Integer> contentSizeMap = new HashMap<String,Integer>();
}

public class MyCrawler extends WebCrawler {
	private	final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
														+"|png|mp3|mp3|zip|gz|xml|ashx))$");
	private final String allowedDomain = "http://www.nbcnews.com/"; 
	
	private final String ok_indicator = "OK";
	private final String not_ok_indicator = "N_OK";
	
	private static boolean initialised = false;
	private static boolean completedCrawling = false;
	private static Writer writerURL, writerFetched , writerVisited;
	private static FileHandler fileHandlerURL, fileHandlerFetched , fileHandlerVisited;
	
	
	public static void init(){
		initialised = true;
		fileHandlerURL =new FileHandler();
		fileHandlerURL.createFile(FileHandler.URLs_LIST);
		writerURL = fileHandlerURL.getWriter(null);
		
		fileHandlerFetched =new FileHandler();
		fileHandlerFetched.createFile(FileHandler.FETCHED_LIST);
		writerFetched = fileHandlerFetched.getWriter(null);

		fileHandlerVisited =new FileHandler();
		fileHandlerVisited.createFile(FileHandler.VISITED_LIST);
		writerVisited = fileHandlerVisited.getWriter(null);		
	}
	/**
	* This method receives two parameters. The first parameter is the page
	* in which we have discovered this new url and the second parameter is
	* the new url. You should implement this function to specify whether
	* the given url should be crawled or not (based on your crawling logic).
	* In this example, we are instructing the crawler to ignore urls that
	* have css, js, git, ... extensions and to only accept urls that start
	* with "http://www.viterbi.usc.edu/". In this case,	we didn't need the
	* referringPage parameter to make the decision.
	*/
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {		
		String href	= url.getURL().toLowerCase();
		//System.out.println("URL: "+href);
		URLStatistics.totalUrlsExtracted++;
		URLStatistics.totalUniqueUrls.add(href);
		boolean shouldVisit =!FILTERS.matcher(href).matches() && href.startsWith(allowedDomain);
		String urlInDomain = shouldVisit? ok_indicator:not_ok_indicator;
		if(shouldVisit)URLStatistics.uniqueWithin.add(href);
		else URLStatistics.uniqueOutside.add(href);
		fileHandlerURL.writeLine(writerURL,new String[]{href,urlInDomain});		
		return	shouldVisit;
		}
	
	/**
	* This function is called when a page is fetched and ready
	* to be processed by your program.
	*/
	@Override
	public void visit(Page page) {
	String url = page.getWebURL().getURL();
	String pageSize = ""+page.getContentData().length;
	String contentType = page.getContentType().split(";")[0];
	String contentRange=contentRange(page.getContentData().length);
	if (PageStatistics.contentSizeMap.containsKey(contentRange))
		PageStatistics.contentSizeMap.put(contentRange,	PageStatistics.contentSizeMap.get(contentRange) + 1);
	else
		PageStatistics.contentSizeMap.put(contentRange,1);
	PageStatistics.uniqueContentType.add(contentType);
	System.out.println("URL: "+url);	
		if(page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links =	htmlParseData.getOutgoingUrls();
			fileHandlerVisited.writeLine(writerVisited,
					new String[]{url,pageSize,""+links.size(),contentType});		
			FetchStatistics.fetchSucessful++;
			/*System.out.println("Text length: "+text.length());
			System.out.println("Html length: "+html.length());
			System.out.println("Number of outgoing links: "	+ links.size());*/
			}
		}
	
	@Override
	 protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
	        super.handlePageStatusCode(webUrl,statusCode,statusDescription);
	        if(!initialised)MyCrawler.init();
	        fileHandlerFetched.writeLine(writerFetched, new String[]{webUrl.toString(),
	        														 ""+statusCode});
	        FetchStatistics.fetchAttempted++;
	        PageStatistics.uniqueStatusCode.add(statusCode+"");
	        if(statusCode>=300)
	        	FetchStatistics.fetchfailed++;   
	}
	@Override
	public void onBeforeExit() {
		// TODO Auto-generated method stub
		super.onBeforeExit();
		if(!completedCrawling){
			completedCrawling =true;
			System.out.println("Fetches attempted " + FetchStatistics.fetchAttempted);
			System.out.println("Fetches succeeded " + FetchStatistics.fetchSucessful);
			System.out.println("Fetches failed or aborted " + FetchStatistics.fetchfailed);
			
			
			System.out.println("Total URLs extracted " + URLStatistics.totalUrlsExtracted);
			System.out.println("Unique URLs extracted "+ URLStatistics.totalUniqueUrls.size());
			System.out.println("Unique URLs extracted within website " + URLStatistics.uniqueWithin.size());
			System.out.println("Unique URLs extracted outside website " + URLStatistics.uniqueOutside.size());
			
			System.out.println("Different Status codes: ");
			for(String elem : PageStatistics.uniqueStatusCode)
				System.out.println(elem);
			
			System.out.println("Different content types: ");
			for(String elem : PageStatistics.uniqueContentType)
				System.out.println(elem);		
			
			System.out.println("Different content sizes: ");
			for(String elem : PageStatistics.contentSizeMap.keySet())
				System.out.println(elem+" : "+PageStatistics.contentSizeMap.get(elem));
		}
		
		fileHandlerURL.closeFileHandler();
		fileHandlerFetched.closeFileHandler();
		fileHandlerVisited.closeFileHandler();
	}
	
	public String contentRange(int size){
		String contentRange = "";
		if(size < 1024) contentRange = "<1KB";
		else if(size < 10240) contentRange = "1KB ~ <10KB";
		else if(size < 102400) contentRange = "10KB ~ <100KB";
		else if(size < 1048576) contentRange = "100KB ~ <1MB";
		else contentRange = ">1MB";		
		return contentRange;
	}	
}