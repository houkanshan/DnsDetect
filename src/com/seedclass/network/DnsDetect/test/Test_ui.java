package com.seedclass.network.DnsDetect;

import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


public class Test_ui {

	public static void main(String[] args) {
		testSwt newtest = new testSwt();
		newtest.textDialog();
	}
}

class testSwt {
	void textDialog(){
		Display display = new Display ();
		final Shell shell = new Shell (display);
		shell.setText("Shell");
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginWidth = 10;
		fillLayout.marginHeight = 10;
		shell.setLayout(fillLayout);

		Button open = new Button (shell, SWT.PUSH);
		open.setText ("Prompt for a String");
		open.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				final Shell dialog = new Shell (shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
				dialog.setText("Dialog Shell");
				FormLayout formLayout = new FormLayout ();
				formLayout.marginWidth = 10;
				formLayout.marginHeight = 10;
				formLayout.spacing = 10;
				dialog.setLayout (formLayout);

				Label label = new Label (dialog, SWT.NONE);
				label.setText ("Type a String:");
				FormData data = new FormData ();
				label.setLayoutData (data);

				Button cancel = new Button (dialog, SWT.PUSH);
				cancel.setText ("Cancel");
				data = new FormData ();
				data.width = 60;
				data.right = new FormAttachment (100, 0);
				data.bottom = new FormAttachment (100, 0);
				cancel.setLayoutData (data);
				cancel.addSelectionListener (new SelectionAdapter () {
					public void widgetSelected (SelectionEvent e) {
						System.out.println("User cancelled dialog");
						dialog.close ();
					}
				});

				final Text text = new Text (dialog, SWT.BORDER);
				data = new FormData ();
				data.width = 200;
				data.left = new FormAttachment (label, 0, SWT.DEFAULT);
				data.right = new FormAttachment (100, 0);
				data.top = new FormAttachment (label, 0, SWT.CENTER);
				data.bottom = new FormAttachment (cancel, 0, SWT.DEFAULT);
				text.setLayoutData (data);

				Button ok = new Button (dialog, SWT.PUSH);
				ok.setText ("OK");
				data = new FormData ();
				data.width = 60;
				data.right = new FormAttachment (cancel, 0, SWT.DEFAULT);
				data.bottom = new FormAttachment (100, 0);
				ok.setLayoutData (data);
				ok.addSelectionListener (new SelectionAdapter () {
					public void widgetSelected (SelectionEvent e) {
						System.out.println ("User typed: " + text.getText ());
						dialog.close ();
					}
				});

				dialog.setDefaultButton (ok);
				dialog.pack ();
				dialog.open ();
			}
		});
		shell.pack ();
		shell.open ();
		
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	void group(){
		Display display = new Display ();
		final Image image = new Image (display, 20, 20);
		Color color = display.getSystemColor (SWT.COLOR_RED);
		GC gc = new GC (image);
		gc.setBackground (color);
		gc.fillRectangle (image.getBounds ());
		gc.dispose ();

		Shell shell = new Shell (display);
		shell.setLayout (new FillLayout ());
		Group group = new Group (shell, SWT.NONE);
		group.setLayout (new FillLayout ());
		group.setText ("a square");
		Canvas canvas = new Canvas (group, SWT.NONE);
		canvas.addPaintListener (new PaintListener () {
			public void paintControl (PaintEvent e) {
				e.gc.drawImage (image, 0, 0);
			}
		});

		shell.pack ();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ())
				display.sleep ();
		}
		image.dispose ();
		display.dispose ();
	}
	void fileDialog(){
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.open ();
		FileDialog dialog = new FileDialog (shell, SWT.SAVE);
		String [] filterNames = new String [] {"Image Files", "All Files (*)"};
		String [] filterExtensions = new String [] {"*.gif;*.png;*.xpm;*.jpg;*.jpeg;*.tiff", "*"};
		String filterPath = "/";
		String platform = SWT.getPlatform();
		System.out.println(platform);
//		if (platform.equals("win32") || platform.equals("wpf")) {
//			filterNames = new String [] {"Image Files", "All Files (*.*)"};
//			filterExtensions = new String [] {"*.gif;*.png;*.bmp;*.jpg;*.jpeg;*.tiff", "*.*"};
//			filterPath = "e:\\";
//		}
		dialog.setFilterNames (filterNames);
		dialog.setFilterExtensions (filterExtensions);
		dialog.setFilterPath (filterPath);
//		dialog.setFileName ("myfile");
		System.out.println ("保存了: " + dialog.open ());
		System.out.println(dialog.getFileName());
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	void tablelayout(){
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		final Table table = new Table(shell, SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		TableColumn column1 = new TableColumn(table, SWT.NONE);
//		TableColumn column2 = new TableColumn(table, SWT.NONE);
		for (int i = 0; i < 10; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] {"item " + i, "edit this value"});
		}
		column1.pack();
//		column2.pack();
		
		shell.setSize(300, 300);
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();	}
	
	void rowlayout1(){
		Display display = new Display();
		Shell shell = new Shell(display);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.wrap = true;
		layout.fill = false;
		layout.justify = true;
		shell.setLayout(layout);

		Button b = new Button(shell, SWT.PUSH);
		b.setText("Button 1");
		b = new Button(shell, SWT.PUSH);

		b.setText("Button 2");

		b = new Button(shell, SWT.PUSH);
		b.setText("Button 3");

		b = new Button(shell, SWT.PUSH);
		b.setText("Not shown");
		b.setVisible(false);
		RowData data = new RowData();
		data.exclude = true;
		b.setLayoutData(data);

		b = new Button(shell, SWT.PUSH);
		b.setText("Button 200 high");
		data = new RowData();
		data.height = 200;
		b.setLayoutData(data);

		b = new Button(shell, SWT.PUSH);
		b.setText("Button 200 wide");
		data = new RowData();
		data.width = 200;
		b.setLayoutData(data);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	void tableTest2(){
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		final Table table = new Table(shell, SWT.BORDER);
		table.setHeaderVisible(true);
		final TableColumn column1 = new TableColumn(table, SWT.NONE);
		column1.setText("Column 1");
		final TableColumn column2 = new TableColumn(table, SWT.NONE);
		column2.setText("Column 2");
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { "a", "3" });
		item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { "b", "2" });
		item = new TableItem(table, SWT.NONE);
		item.setText(new String[] { "c", "1" });
		column1.setWidth(100);
		column2.setWidth(100);
		Listener sortListener = new Listener() {
			public void handleEvent(Event e) {
				TableItem[] items = table.getItems();
				Collator collator = Collator.getInstance(Locale.getDefault());
				TableColumn column = (TableColumn) e.widget;
				int index = column == column1 ? 0 : 1;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {
						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0),
									items[i].getText(1) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
				table.setSortColumn(column);
			}
		};
		column1.addListener(SWT.Selection, sortListener);
		column2.addListener(SWT.Selection, sortListener);
		table.setSortColumn(column1);
		table.setSortDirection(SWT.UP);
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT).x, 300);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	/** 创建一个表格每0.5秒就刷出1000个元素 */
	static int[] data = new int[0];
	void tableTest1(){
		final Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setLayout(new FillLayout());
		final Table table = new Table(shell, SWT.BORDER | SWT.VIRTUAL);
		table.addListener(SWT.SetData, new Listener() {
			public void handleEvent(Event e) {
				TableItem item = (TableItem)e.item;
				int index = table.indexOf(item);
				item.setText("Item "+data[index]);
			}
		});
		Thread thread = new Thread() {
			public void run() {
				int count = 0;
				Random random = new Random();
				while (count++ < 500) {
					if (table.isDisposed()) return;
					// add 10 random numbers to array and sort
					int grow = 10;
					int[] newData = new int[data.length + grow];
					System.arraycopy(data, 0, newData, 0, data.length);
					int index = data.length;
					data = newData;
					for (int j = 0; j < grow; j++) {
						data[index++] = random.nextInt();
					}
					Arrays.sort(data);
					display.syncExec(new Runnable() {
						public void run() {
							if (table.isDisposed()) return;
							table.setItemCount(data.length);
							table.clearAll();
						}
					});
					try {Thread.sleep(5000);} catch (Throwable t) {}
				}
			}
		};
		thread.start();
		shell.open ();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}

	/** 菜单有快捷键 */
	void menuTest1(){
		Display display = new Display ();
		Shell shell = new Shell (display);
		Menu bar = new Menu (shell, SWT.BAR);
		shell.setMenuBar (bar);
		MenuItem fileItem = new MenuItem (bar, SWT.CASCADE);
		fileItem.setText ("&File");
		Menu submenu = new Menu (shell, SWT.DROP_DOWN);
		fileItem.setMenu (submenu);
		MenuItem item = new MenuItem (submenu, SWT.PUSH);
		item.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				System.out.println ("Select All");
			}
		});
		item.setText ("Select &All\tCtrl+A");
		item.setAccelerator (SWT.MOD1 + 'A');
		shell.setSize (200, 200);
		shell.open ();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	void tabFolderTest() {
		Display display = new Display();
		final Shell shell = new Shell(display);
		final TabFolder tabFolder = new TabFolder(shell, SWT.ALL);
		Rectangle clientArea = shell.getClientArea();
		tabFolder.setLocation(clientArea.x, clientArea.y);
		for (int i = 0; i < 6; i++) {
			TabItem item = new TabItem(tabFolder, SWT.NONE);
			item.setText("TabItem " + i);
			Button button = new Button(tabFolder, SWT.PUSH);
			button.setText("Page " + i);
			item.setControl(button);
		}
		tabFolder.pack();
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	void buttonTest1(){
		Display display = new Display ();
		final Shell shell = new Shell (display);
		shell.setLayout (new RowLayout (SWT.VERTICAL));
		for (int i=0; i<8; i++) {
			Button button = new Button (shell, SWT.RADIO);
			button.setText ("B" + i);
			if (i == 0) button.setSelection (true);
		}
		Button button = new Button (shell, SWT.PUSH);
		button.setText ("Set Selection to B4");
		button.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				Control [] children = shell.getChildren ();
				Button newButton = (Button) children [4];
				for (int i=0; i<children.length; i++) {
					Control child = children [i];
					if (child instanceof Button && (child.getStyle () & SWT.RADIO) != 0) {
						((Button) child).setSelection (false);
					}
				}
				newButton.setSelection (true);
			}
		});
		shell.pack ();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	void buttonTest2(){
		Display display = new Display ();
		Shell shell = new Shell (display);
		Label label = new Label (shell, SWT.NONE);
		label.setText ("Enter your name:");
		Text text = new Text (shell, SWT.BORDER);
		text.setLayoutData (new RowData (100, SWT.DEFAULT));
		Button ok = new Button (shell, SWT.PUSH);
		ok.setText ("OK");
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("OK");
			}
		});
		Button cancel = new Button (shell, SWT.PUSH);
		cancel.setText ("Cancel");
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Cancel");
			}
		});
		shell.setDefaultButton (cancel);
		shell.setLayout (new RowLayout ());
		shell.pack ();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	/** 会改变单选的项目 */
	void buttonTest3(){
		Display display = new Display ();
		final Shell shell = new Shell (display);
		shell.setLayout (new FillLayout ());
		Listener listener = new Listener () {
			public void handleEvent (Event e) {
				Control [] children = shell.getChildren ();
				for (int i=0; i<children.length; i++) {
					Control child = children [i];
					if (e.widget != child && child instanceof Button && (child.getStyle () & SWT.TOGGLE) != 0) {
						((Button) child).setSelection (false);
					}
				}
				((Button) e.widget).setSelection (true);
			}
		};
		for (int i=0; i<20; i++) {
			Button button = new Button (shell, SWT.TOGGLE);
			button.setText ("B" + i);
			button.addListener (SWT.Selection, listener);
			if (i == 0) button.setSelection (true);
		}
		shell.pack ();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	void buttonTest4(){
		Display display = new Display ();
		Shell shell = new Shell (display);
		shell.setLayout(new GridLayout());
		final Button button = new Button (shell, SWT.CHECK);
		button.setLayoutData(new GridData(GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_CENTER));
		button.setText ("Tri-state");
		/* Make the button toggle between three states */
		button.addListener (SWT.Selection, new Listener() {
			public void handleEvent (Event e) {
				if (button.getSelection()) {
					if (!button.getGrayed()) {
						button.setGrayed(true);
					}
				} else {
					if (button.getGrayed()) {
						button.setGrayed(false);
						button.setSelection (true);
					}
				}
			}
		});
		/* Read the tri-state button (application code) */
		button.addListener (SWT.Selection, new Listener() {
			public void handleEvent (Event e) {
				if (button.getGrayed()) {
					System.out.println("Grayed");
				} else {
					if (button.getSelection()) {
						System.out.println("Selected");
					} else {
						System.out.println("Not selected");
					}
				}
			}
		});
		shell.setSize(300, 300);
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
	
	void menuTest(){
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		Menu appMenuBar = display.getMenuBar();
		if (appMenuBar == null) {
			appMenuBar = new Menu(shell, SWT.BAR);
			shell.setMenuBar(appMenuBar);
		}
		MenuItem file = new MenuItem(appMenuBar, SWT.CASCADE);
		file.setText("File");
		Menu dropdown = new Menu(appMenuBar);
		file.setMenu(dropdown);
		MenuItem exit = new MenuItem(dropdown, SWT.PUSH);
		exit.setText("Exit");
		exit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				display.dispose();
			};
		});
		Button b = new Button(shell, SWT.PUSH);
		b.setText("Test");
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
