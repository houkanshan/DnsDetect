package com.seedclass.network.DnsDetect;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.xbill.DNS.ARecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import com.csvreader.CsvReader;


public class test {
	public static void main(String[] args) {
		DNS_test.request("www.google.com");
		try {
			CSV_test.readCsv("E:\\studio\\programing\\code\\Java\\workspace\\network\\data\\top-1m.csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("finish");
	}
}

class InetAddrTest {
	static void run() {
		InetAddress[] IPAddressArray = null;
		try {
			IPAddressArray = InetAddress.getAllByName("www.facebook.com");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (InetAddress address : IPAddressArray) {
			System.out.println(address.getHostAddress());
		}
	}
}

class DNS_test {
	static void request(String Name) {
		try {
			Lookup lookup = new Lookup(Name, Type.A);
			Record[] records = lookup.run();

			if (lookup.getResult() == Lookup.SUCCESSFUL) {
				//String responseMessage = null;
				String listingType = null;
				for (int i = 0; i < records.length; i++) {
					/*
					 * if (records[i] instanceof TXTRecord) { TXTRecord txt =
					 * (TXTRecord) records[i]; for (Iterator j =
					 * txt.getStrings().iterator(); j.hasNext();) {
					 * responseMessage += (String) j.next(); }
					 * System.out.println("TXRecord " + responseMessage); } else
					 */
					if (records[i] instanceof ARecord) {
						listingType = ((ARecord) records[i]).getAddress()
								.getHostAddress();
						System.out.println("ARecord address : " + listingType);
					}
				}
			}
		} catch (TextParseException e) {
			e.printStackTrace();
		}
	}
}

class CSV_test {
	static void readCsv(String fileNameString) throws IOException{
		new File(fileNameString).createNewFile();
		CsvReader reader = new CsvReader(fileNameString);
		reader.readRecord();
		System.out.println(reader.get(1));
		reader.readRecord();
		System.out.println(reader.get(1));
		System.out.println(String.valueOf(reader.getColumnCount()));
		String lineString = "1,2,3,4,5,6";
		CsvReader reader2 = CsvReader.parse(lineString);
		reader2.readRecord();
		System.out.println(reader2.get(0)+reader2.get(1)+reader2.get(3));
	}
}