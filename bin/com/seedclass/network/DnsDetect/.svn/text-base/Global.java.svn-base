package com.seedclass.network.DnsDetect;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;

class Global {
	static final int NamePosInCSV = 1;

	static ArrayList<DnsRequest> dnsRecords = new ArrayList<DnsRequest>();
	static ArrayList<DnsRequest> timeoutRecords = new ArrayList<DnsRequest>();
	static ArrayList<DnsRequest> blockRecords = new ArrayList<DnsRequest>();
	static ArrayList<DnsRequest> polluteRecords = new ArrayList<DnsRequest>();
	static ArrayList<DnsRequest> otherRecords = new ArrayList<DnsRequest>();

	static boolean isDone = false;

	/** DNS请求服务器 */
	static String DnsServer = "8.8.8.8";

	/** 代理设置 */
	static String SocksProxyAddr = "127.0.0.1";
	static String socksProxyPort = "8087";
	static boolean isProxyOn = false;

	/** 文件路径 */
	static String importFileName = "E:\\studio\\programing\\code\\Java\\workspace\\network\\data\\top-1m.csv";
	static String exportFileName = "	";

	/** GUI元素 */
	static Table rawNameTable;
	static Table timeoutTable;
	static Table blockTable;
	static Table polluteTable;
	static Table otherTable;
	
	static Button importButton;
	static Button analyseButton;
	static Button exportButton;
}
