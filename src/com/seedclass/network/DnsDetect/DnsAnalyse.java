package com.seedclass.network.DnsDetect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.RestoreAction;

import org.omg.PortableInterceptor.SUCCESSFUL;
import org.xbill.DNS.Lookup;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class DnsAnalyse {
	public final static int NORMAL = 0;
	public final static int TIMEOUT = 1;
	public final static int NOTFOUND = 2;
	public final static int POLLUTE = 3;
	public final static int OTHER_SAME = 4;
	public final static int OTHER_DIFF = 5;

	
	static int analyse(DnsRequest dnsRequest) {
		int res = -1;
		if (dnsRequest.getUdpErrCode() == dnsRequest.getTcpErrCode()) {
			// 错误码相同
			if (dnsRequest.getUdpErrCode() == Lookup.SUCCESSFUL) {
				// 相同且成功
				Collections.sort(dnsRequest.getTcpAddrList());
				Collections.sort(dnsRequest.getUdpAddrList());
				if (dnsRequest.getTcpAddrList().equals(
						dnsRequest.getUdpAddrList())) {
					// 没有问题
					res = NORMAL;
					System.err.println("正常");
				} else {
					res = POLLUTE;
					System.err.println("污染");
				}
			} else {
				// 都解析失败, 但错误码相同
				switch (dnsRequest.getUdpErrCode()) {
				case Lookup.TRY_AGAIN:
					res = TIMEOUT;
					System.err.println("超时");
					break;
				case Lookup.TYPE_NOT_FOUND:
					res = NOTFOUND;
					System.err.println("没找到");
					break;
				default:
					res = OTHER_SAME;
					System.err.println("其他相同");
					break;
				}
			}
		} else if ((dnsRequest.getUdpErrCode() == Lookup.TYPE_NOT_FOUND)) {
			// 错误码不同
			switch (dnsRequest.getTcpErrCode()) {
			case Lookup.SUCCESSFUL:
				res = NORMAL;
				System.err.println("没有加www");
				break;
			case Lookup.HOST_NOT_FOUND:
				//重新设置为无www的域名
				dnsRequest.setQueryName(dnsRequest.getQueryName()
						.subSequence(4, dnsRequest.getQueryName().length())
						.toString());
				res = NOTFOUND;
				break;
			}
		} else {
			// 错误码不同
			res = OTHER_DIFF;
		}
		return res;
	}
}
