package com.seedclass.network.DnsDetect;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Cache;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;


public class DnsScan extends Thread {
	static final int waitTime = 100;

	public static void main(String[] args) {

		/** @test begin */
		DnsRequest tmpRequest;
		try {
			tmpRequest = new DnsRequest("8.8.8.8");
			tmpRequest.setQueryName("www.baidu.com");
			Global.dnsRecords.add(tmpRequest);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		/** @test end */

		DnsScan t = new DnsScan();
		t.start();
	}

	/** 按下按钮, 启动dns分析进程 */
	public void run() {
		setName("Dns Scan:"+getName());

		for (DnsRequest tmpRequest : Global.dnsRecords) {
			// 如果gui被关闭, 则终止线程
			if (Global.rawNameTable.isDisposed()) {
				return;
			}
			// 如果子线程出关键错误, 则终止所有线程
			if (DnsRequest.isErr) {
				System.err.println("关键错误终止所有线程");
				return;
			}
			System.err.println(Thread.currentThread().getState().toString());
			System.err.println(tmpRequest.getState().toString());
			tmpRequest.start();
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("我不知道这是什么错误");
			}

		}

		System.err.println("=======================");
		System.err.println("分析线程结束");
		System.err.println("=======================");
	}
}

/** 用于处理dns请求 */
class DnsRequest extends Thread {
	public static boolean isErr = false;

	private String DnsServer = null;
	private String name = null;
	private int udpErrCode = Lookup.TRY_AGAIN;
	private int tcpErrCode = Lookup.TRY_AGAIN;
	private String udpErrString = null;
	private String tcpErrString = null;
	private ArrayList<String> udpAddressList = null;
	private ArrayList<String> tcpAddressList = null;
	private SimpleResolver resolver = null;
	private long udpRtt = -1;

	private Table analyseTable = null;
	public String[] analyseResultStringArray = null;

	private boolean isTcp = false;

	/**
	 * 用于初始化dns服务器的构造
	 * 
	 * @param DnsServerName
	 *            DNS服务器地址
	 * @throws UnknownHostException
	 */
	public DnsRequest(String DnsServerName) throws UnknownHostException {
		this.setDnsServer(DnsServerName);
	}

	public void setTCP() {
		isTcp = true;
	}

	public void setUDP() {
		isTcp = false;
	}

	/**
	 * 配置一个dnsServer
	 * 
	 * @throws UnknownHostException
	 */
	public void setDnsServer(String DnsServerName) throws UnknownHostException {
		this.DnsServer = DnsServerName;
	}

	/**
	 * 改变解析器的配置
	 * 
	 * @throws UnknownHostException
	 */
	private void setResolver() throws UnknownHostException {
		resolver = new SimpleResolver(this.DnsServer);
	}

	/** 加入代理或者隧道的配置 */

	public void setQueryName(String name) {
		this.name = name;
	}

	public String getQueryName() {
		return this.name;
	}

	/** 启动单个域名解析\分析线程 */
	public void run() {
		setName("DnsRequre:"+getName());
		System.out.println("now query: " + getQueryName());
		// 分别进行udp和tcp请求
		try {
			setUDP();
			udpAddressList = doRequest();
			setTCP();
			tcpAddressList = doRequest();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.err.println("dns服务器非法");
			isErr = true;
		}

		// 进行分析

		switch (DnsAnalyse.analyse(this)) {
		case DnsAnalyse.NORMAL:
			analyseTable = null;
			break;
		case DnsAnalyse.NOTFOUND:
			analyseTable = Global.blockTable;
			Global.blockRecords.add(this);
			analyseResultStringArray = new String[] { "", getQueryName(),
					getTcpErrString() , String.valueOf(Global.dnsRecords.indexOf(this))};
			break;
		case DnsAnalyse.TIMEOUT:
			analyseTable = Global.timeoutTable;
			Global.timeoutRecords.add(this);
			analyseResultStringArray = new String[] { "", getQueryName(),
					getTcpErrString() };
			break;
		case DnsAnalyse.POLLUTE:
			analyseTable = Global.polluteTable;
			Global.polluteRecords.add(this);
			analyseResultStringArray = new String[] { "", getQueryName(),
					getUdpAddrList().toString(), getTcpAddrList().toString() };
			break;
		case DnsAnalyse.OTHER_DIFF:
		case DnsAnalyse.OTHER_SAME:
			analyseTable = Global.otherTable;
			Global.otherRecords.add(this);
			analyseResultStringArray = new String[] { "", getQueryName(),
					getUdpErrString(), getTcpErrString() };
			break;
		default:
			System.err.println("analyse wrong");
			System.err.println(String.valueOf(getUdpErrCode()) + ":"
					+ getUdpErrString());
			System.err.println(String.valueOf(getTcpErrCode()) + ":"
					+ getTcpErrString());
			break;
		}
		DnsAnalyse.analyse(this);

		// 对结果进行分析

		// 在子线程上对ui的操作必须用这个进行同步
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				TableItem tmpItem = new TableItem(Global.rawNameTable, SWT.None);
				// 写编号
				tmpItem.setText(0,
						Integer.toString(Global.rawNameTable.getItemCount()));
				// 填写域名
				tmpItem.setText(1, getQueryName());
				// 填写udp结果
				if (Lookup.SUCCESSFUL == getUdpErrCode()) {
					tmpItem.setText(2, getUdpAddrList().toString());
				} else {
					tmpItem.setText(2, getUdpErrString());
				}
				// 填写tcp结果
				if (Lookup.SUCCESSFUL == getTcpErrCode()) {
					tmpItem.setText(3, getTcpAddrList().toString());
				} else {
					tmpItem.setText(3, getTcpErrString());
				}

				// 填写rtt
				tmpItem.setText(4, String.valueOf(getRtt()));

				// 使能输出按钮
				// if (Global.rawNameTable.getItemCount() == Global.dnsRecords
				// .size()) {
				// Global.exportButton.setEnabled(true);
				// }

				// 写分析结果
				if (null != analyseTable) {
					TableItem analyseItem = new TableItem(analyseTable,
							SWT.None);
					analyseItem.setText(analyseResultStringArray);
					analyseItem.setText(0,
							String.valueOf(analyseTable.getItemCount()));
				}
			}
		});
	}

	/**
	 * 发起一个dns请求, 并获取结果. 短时间同一个请求会使用缓存数据, 必须禁用缓存或者清除缓存
	 * 
	 * @throws UnknownHostException
	 */
	public ArrayList<String> doRequest() throws UnknownHostException {
		Lookup lookup;
		ArrayList<String> addressList = null;
		int errCode = Lookup.TRY_AGAIN;
		String errString = null;

		/**
		 * @throw UnknownHostException
		 */
		setResolver();
		// 改变传输协议
		resolver.setTCP(isTcp);

		if (this.name == null) {
			errCode = -1;
			errString = "name is not set";
		} else {
			try {
				lookup = new Lookup(getQueryName(), Type.A);
				lookup.setResolver(resolver);
				// 清空缓存
				lookup.setCache(new Cache());
				for (int j = 0; (((errCode == Lookup.TRY_AGAIN)||(errCode == Lookup.TYPE_NOT_FOUND)) && (j < 3)); ++j) {
					if (j > 0) {
						System.err.println("***************************");
						System.err.println(errString);
						System.err.println("第" + String.valueOf(j + 1) + "次请求");
						System.err.println("***************************");
					}

					//开始dns请求, 并计算时间
					udpRtt = System.nanoTime();
					Record[] records = lookup.run();
					udpRtt = System.nanoTime() - udpRtt;
					udpRtt = udpRtt/1000000;

					errCode = lookup.getResult();
					errString = lookup.getErrorString();
					if (errCode == Lookup.SUCCESSFUL) {
						addressList = new ArrayList<String>();
						String address = null;
						for (int i = 0; i < records.length; i++) {
							if (records[i] instanceof ARecord) {
								address = ((ARecord) records[i]).getAddress()
										.getHostAddress();
								addressList.add(address);
							}
						}
					} else if (errCode == Lookup.TYPE_NOT_FOUND){
						if (!getQueryName().startsWith("www.")){
							setQueryName("www." + getQueryName());
						} else {
							//如果已经是www.开头就跳出循环
							break;
						}
					}
				}
			} catch (TextParseException e) {
				e.printStackTrace();
				errCode = -1;
				errString = "query name is invalid";
			}
		}

		if (isTcp) {
			tcpErrCode = errCode;
			tcpErrString = errString;
		} else {
			udpErrCode = errCode;
			udpErrString = errString;
		}
		return addressList;
	}

	public long getRtt() {
		return this.udpRtt;
	}

	/** 获取UDP错误码(int型需要转string) */
	public int getUdpErrCode() {
		return this.udpErrCode;
	}

	/** 获取UDP错误信息 */
	public String getUdpErrString() {
		return this.udpErrString;
	}

	/** 获取TCP错误码(int型需要转string) */
	public int getTcpErrCode() {
		return this.tcpErrCode;
	}

	/** 获取TCP错误信息 */
	public String getTcpErrString() {
		return this.tcpErrString;
	}

	/**
	 * /** 获取udp解析结果
	 */
	public ArrayList<String> getUdpAddrList() {
		return udpAddressList;
	}

	/** 获取tcp解析结果 */
	public ArrayList<String> getTcpAddrList() {
		return tcpAddressList;
	}
}