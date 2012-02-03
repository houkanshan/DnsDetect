package com.seedclass.network.DnsDetect;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class MutiTaskGUI {
	private Shell shell = null;
	private Table table = null;
	private addItem t;

	public MutiTaskGUI() {
		init();
	}

	// 初始化窗口方法
	public void init() {
		shell = new Shell();
		shell.setSize(282, 240);
		shell.setLayout(new GridLayout());
		Button bt = new Button(shell, SWT.NONE);
		bt.setText("开始一个任务");
		// 停止线程
		Button stop = new Button(shell, SWT.NONE);
		stop.setText("stop");
		table = new Table(shell, SWT.BORDER);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] header = new String[] { "任务", "进度", "操作" };
		// 创建表头
		for (int i = 0; i < 3; i++) {
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText(header[i]);
		}

		// 设置表头宽度
		table.getColumn(0).setWidth(80);
		table.getColumn(1).setWidth(150);
		table.getColumn(2).setWidth(80);
		// shell.pack();

		// 注册创建任务按钮事件
		bt.addSelectionListener(new SelectionAdapter() {
			// 当单击创建一个任务按钮时
			public void widgetSelected(SelectionEvent e) {
				t = new addItem(table);
				t.start();

				/*
				 * //首先创建一个Task对象 
				 * for(int i = 0 ; i < 1000; i++) { 
				 * 	Task task = new Task(); 
				 * //然后在表格中添加一行 
				 * 	task.createTableItem(table);
				 * //最后启动该任务，该任务为一个线程 
				 * 	task.start(); 
				 * }
				 */
				
			}
		});
		stop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				t.setDone(true);
			}
		});
	}

	public Shell getShell() {
		return shell;
	}

	public static void main(String[] args) {
		Display display = Display.getDefault();
		MutiTaskGUI mutiTask = new MutiTaskGUI();
		mutiTask.getShell().open();

		while (!mutiTask.getShell().isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}

class addItem extends Thread {
	private Table table;
	public boolean done = false;

	public addItem(Table table) {
		this.table = table;
	}

	public void run() {
		// 首先创建一个Task对象
		for (int i = 0; i < 1000; i++) {
			// 然后在表格中添加一行
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (isDone()){
				break;
			}
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Task task = new Task();
					task.createTableItem(table);
					task.start();
				}
			});
			// 最后启动该任务，该任务为一个线程
		}
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
}

class Task extends Thread {
	// 是否停止的标志
	public static boolean done = false;

	// 声明进度条对象
	private ProgressBar bar = null;
	private int min = 0;
	private int max = 100;
	int i = 0;

	// 创建表格中的一行
	public void createTableItem(Table table) {
		// done = false;
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(this.getName());
		// 创建一个进度条
		bar = new ProgressBar(table, SWT.NONE);
		bar.setMinimum(min);
		bar.setMaximum(max);
		// 创建一个可编辑的表格对象
		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		// 将进度条绑定到第二列中
		editor.setEditor(bar, item, 1);
	}

	// 线程方法体，与前面单个的进度条的程序类似
	public void run() {
		for (int i = min; i < max; i++) {
			if (isDone()) {
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Display.getDefault().asyncExec(new Runnable() {
				// table.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (bar.isDisposed())
						return;
					bar.setSelection(bar.getSelection() + 50);
				}
			});
			// 如果停止，则结束该线程
		}
	}

	public boolean isDone() {
		return done;
	}

	public static void setDone(boolean done) {
		Task.done = done;
	}

}
