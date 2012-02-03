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

	// ��ʼ�����ڷ���
	public void init() {
		shell = new Shell();
		shell.setSize(282, 240);
		shell.setLayout(new GridLayout());
		Button bt = new Button(shell, SWT.NONE);
		bt.setText("��ʼһ������");
		// ֹͣ�߳�
		Button stop = new Button(shell, SWT.NONE);
		stop.setText("stop");
		table = new Table(shell, SWT.BORDER);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] header = new String[] { "����", "����", "����" };
		// ������ͷ
		for (int i = 0; i < 3; i++) {
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText(header[i]);
		}

		// ���ñ�ͷ���
		table.getColumn(0).setWidth(80);
		table.getColumn(1).setWidth(150);
		table.getColumn(2).setWidth(80);
		// shell.pack();

		// ע�ᴴ������ť�¼�
		bt.addSelectionListener(new SelectionAdapter() {
			// ����������һ������ťʱ
			public void widgetSelected(SelectionEvent e) {
				t = new addItem(table);
				t.start();

				/*
				 * //���ȴ���һ��Task���� 
				 * for(int i = 0 ; i < 1000; i++) { 
				 * 	Task task = new Task(); 
				 * //Ȼ���ڱ�������һ�� 
				 * 	task.createTableItem(table);
				 * //������������񣬸�����Ϊһ���߳� 
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
		// ���ȴ���һ��Task����
		for (int i = 0; i < 1000; i++) {
			// Ȼ���ڱ�������һ��
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
			// ������������񣬸�����Ϊһ���߳�
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
	// �Ƿ�ֹͣ�ı�־
	public static boolean done = false;

	// ��������������
	private ProgressBar bar = null;
	private int min = 0;
	private int max = 100;
	int i = 0;

	// ��������е�һ��
	public void createTableItem(Table table) {
		// done = false;
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(this.getName());
		// ����һ��������
		bar = new ProgressBar(table, SWT.NONE);
		bar.setMinimum(min);
		bar.setMaximum(max);
		// ����һ���ɱ༭�ı�����
		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		// ���������󶨵��ڶ�����
		editor.setEditor(bar, item, 1);
	}

	// �̷߳����壬��ǰ�浥���Ľ������ĳ�������
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
			// ���ֹͣ����������߳�
		}
	}

	public boolean isDone() {
		return done;
	}

	public static void setDone(boolean done) {
		Task.done = done;
	}

}
