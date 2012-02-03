package com.seedclass.network.DnsDetect;	

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class FileProcess {

	// 导入文件中域名的位置
	static final int namePos = 1;

	public static void importName(String importFileName)
			throws FileNotFoundException {
		CsvReader allReader = new CsvReader(importFileName);
		try {
			while (false != allReader.readRecord()) {
				DnsRequest dnsRequest = new DnsRequest(Global.DnsServer);
				dnsRequest.setQueryName(allReader.get(namePos));
				Global.dnsRecords.add(dnsRequest);
				// System.err.println(Global.dnsRecords.get(Global.dnsRecords
				// .size()-1));
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.err.println("服务器非法");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("文件读取错误");
			allReader.close();
			return;
		} 
		allReader.close();
		return;
	}
	
	/** @throws IOException 文件无法写入 */
	public static void exportResult(String exportFileName, ArrayList<DnsRequest> dnsRequests) throws IOException{
		CsvWriter allWriter = new CsvWriter(exportFileName, ',', Charset.forName("utf-8"));
//		CsvWriter tmpWriter = new CsvWriter("rtt.csv",',', Charset.forName("utf-8"));
		
		if (Global.dnsRecords == null){
			return;
		}
		allWriter.writeRecord(new String[]{"域名", "传输方式", "错误码", "错误信息", "解析结果"});
		for (DnsRequest tmpRequest : dnsRequests){
			//写udp结果
			allWriter.write(tmpRequest.getQueryName());
			allWriter.write("UDP");
			allWriter.write(String.valueOf(tmpRequest.getUdpErrCode()));
			allWriter.write(tmpRequest.getUdpErrString());
			
			if (tmpRequest.getUdpAddrList() != null){
				for (String tmpName : tmpRequest.getUdpAddrList()) {
					System.err.println(tmpName + ", ");
					allWriter.write(tmpName);
				}
			}
			allWriter.endRecord();
			
			//写TCP结果
			allWriter.write(tmpRequest.getQueryName());
			allWriter.write("TCP");
			allWriter.write(String.valueOf(tmpRequest.getTcpErrCode()));
			allWriter.write(tmpRequest.getTcpErrString());
			
			if (tmpRequest.getTcpAddrList() != null) {
				for (String tmpName : tmpRequest.getTcpAddrList()) {
					System.err.println(tmpName + ", ");
					allWriter.write(tmpName);
				}
			}
			allWriter.endRecord();
			
			
			//tmp
//			tmpWriter.writeRecord(new String[]{tmpRequest.getQueryName(), String.valueOf(tmpRequest.getRtt())});
		}
		allWriter.close();
//		tmpWriter.close();
		
	}
}
