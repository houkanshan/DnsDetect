package com.seedclass.network.DnsDetect;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
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

public class gui {
    private final static int numWidth = 40;

    private static Display display = null;
    private static Shell shell = null;
    private static TabFolder tabFolder = null;

    public static void main(String[] args) {
        gui dnsGui = new gui();
        dnsGui.go();
    }

    public void go() {
        display = new Display();
        shell = new Shell();

        // ���ò��ֽṹ
        GridLayout shellLayout = new GridLayout(1, true);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        // RowLayout shellLayout = new RowLayout(SWT.VERTICAL);
        // shellLayout.fill = true;
        // shellLayout.wrap = false;
        // FillLayout shellLayout = new FillLayout(SWT.VERTICAL);
        shell.setLayoutData(gridData);
        shell.setLayout(shellLayout);
        shell.setText("DNS����������������");

        //������ť��
        Composite buttonComposite = new Composite(shell, SWT.None);
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        buttonComposite.setLayoutData(gridData);
        buttonComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

        Composite tabComposite = new Composite(shell, SWT.None);
        gridData = new GridData(GridData.FILL_BOTH);
        tabComposite.setLayoutData(gridData);
        tabComposite.setLayout(new FillLayout());

        addMenu(shell);

        Global.importButton = addImportButton(buttonComposite);
        Global.analyseButton = addAnalyseButton(buttonComposite);
        Global.exportButton = addExportButton(buttonComposite);

        tabFolder = new TabFolder(tabComposite, SWT.None);
        gridData = new GridData(GridData.FILL_BOTH | SWT.FILL);
        tabFolder.setLayout(new GridLayout(1, false));

        addRawNameTab(tabFolder);
        addTimeOutTab(tabFolder);
        addBlockTab(tabFolder);
        addPolluteTab(tabFolder);
        addOtherTab(tabFolder);

        // ��������
        shell.setSize(800, 500);
        shell.setMinimumSize(shell.getSize());
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    /** �����˵� */
    private void addMenu(Composite parent) {
        Menu bar = new Menu(parent.getShell(), SWT.BAR);
        shell.setMenuBar(bar);
        MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
        fileItem.setText("�ļ�(&F)");
        MenuItem settingItem = new MenuItem(bar, SWT.CASCADE);
        settingItem.setText("����(&C)");
        settingItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addSettingDialog();
            }
        });

        // �����Ӳ˵�
        Menu fileSubMenu = new Menu(parent.getShell(), SWT.DROP_DOWN);
        fileItem.setMenu(fileSubMenu);
        
        MenuItem importSubItem = new MenuItem(fileSubMenu, SWT.PUSH);
        importSubItem.setText("����(&O)");
        importSubItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                importSelected();
            }
        });
        
        //�ָ���1
        MenuItem sep1 = new MenuItem(fileSubMenu, SWT.SEPARATOR);
        sep1.setText("����");
        
        MenuItem exportAllSubItem = new MenuItem(fileSubMenu, SWT.PUSH | SWT.BORDER_DOT);
        exportAllSubItem.setText("�����������(&A)");
        exportAllSubItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                exportSelected(Global.dnsRecords);
            }
        });
        
        MenuItem exportNotFoundSubItem = new MenuItem(fileSubMenu, SWT.PUSH);
        exportNotFoundSubItem.setText("���������ڼ�¼���");
        exportNotFoundSubItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                exportSelected(Global.blockRecords);
            }
        });
       
        MenuItem exportPolluteSubItem = new MenuItem(fileSubMenu, SWT.PUSH);
        exportPolluteSubItem.setText("������Ⱦ���");
        exportPolluteSubItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                exportSelected(Global.polluteRecords);
            }
        });
        
        MenuItem exportOtherSubItem = new MenuItem(fileSubMenu, SWT.PUSH);
        exportOtherSubItem.setText("�����������");
        exportOtherSubItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                exportSelected(Global.otherRecords);
            }
        });
        
        //�ָ���2
        MenuItem sep2 = new MenuItem(fileSubMenu, SWT.SEPARATOR);
        sep2.setText("�˳�");
        
        MenuItem exitSubItem = new MenuItem(fileSubMenu, SWT.PUSH);
        exitSubItem.setText("�˳�(&E)");
        exitSubItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                display.dispose();
            }
        });
    }
    
    //�������öԻ���
    private void addSettingDialog(){
        //�����Ի���
        final Shell settingDialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        settingDialog.setText("����");
        FormLayout formLayout = new FormLayout ();
        formLayout.marginWidth = 10;
        formLayout.marginHeight = 10;
        formLayout.spacing = 10;
        settingDialog.setLayout (formLayout);
        

        //�����������ʾ
        Label setServerTitle = new Label(settingDialog, SWT.None);
        setServerTitle.setText("��������ַ");
        FormData data = new FormData ();
        setServerTitle.setLayoutData (data);

        //����ȡ����ť
                Button cancel = new Button(settingDialog, SWT.PUSH);
                cancel.setText("ȡ��");
                data = new FormData ();
                data.width = 60;
                data.right = new FormAttachment (100, 0);
                data.bottom = new FormAttachment (100, 0);
                cancel.setLayoutData (data);
                cancel.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e){
                        settingDialog.close();
                    }
                });
        
        //���������
        final Text dnsServerText = new Text(settingDialog, SWT.BORDER);
        dnsServerText.setText(Global.DnsServer);
        data = new FormData ();
        data.width = 200;
        data.left = new FormAttachment (setServerTitle, 0, SWT.DEFAULT);
        data.right = new FormAttachment (100, 0);
        data.top = new FormAttachment (setServerTitle, 0, SWT.CENTER);
        data.bottom = new FormAttachment (cancel, 0, SWT.DEFAULT);
        dnsServerText.setLayoutData (data);
//      dnsServerText.setText("111.111.111.111");
//      dnsServerText.pack();
//      System.out.println(dnsServerText.getSize().toString());
        dnsServerText.setSize(96, 17);
        
        //������ť��
        //����ȷ����ť
        Button ok = new Button(settingDialog, SWT.PUSH);
        ok.setText("ȷ��");
        data = new FormData ();
        data.width = 60;
        data.right = new FormAttachment (cancel, 0, SWT.DEFAULT);
        data.bottom = new FormAttachment (100, 0);
        ok.setLayoutData (data);
        ok.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e){
                Global.DnsServer = dnsServerText.getText();
                settingDialog.close();
            }
        });
        
        
        
        settingDialog.pack();
        settingDialog.open();
    }

    /** ��������������ť */
    private Button addImportButton(Composite buttonComposite) {
        Button importNameButton = new Button(buttonComposite, SWT.PUSH);
        importNameButton.setText("���������ļ�");
        importNameButton.pack();
        importNameButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                importSelected();
            }
        });

        return importNameButton;
    }

    /** ������� */
    private void importSelected(){
        FileDialog dialog = new FileDialog(shell, SWT.OPEN);
        String[] filterNames = new String[] { "*.csv��ʽ�ļ�", "�����ļ�(*)" };
        String[] filterExtensions = new String[] { "*.csv", "*" };
        String filterPath = "./";
        // String platform = SWT.getPlatform();
        dialog.setFilterNames(filterNames);
        dialog.setFilterExtensions(filterExtensions);
        dialog.setFilterPath(filterPath);

        Global.importFileName = dialog.open();
        if (Global.importFileName != null) {
            // ����������ļ�
            clearAllTable();
            Global.analyseButton.setEnabled(true);
            Global.exportButton.setEnabled(false);
            try {
            	Global.dnsRecords = new ArrayList<DnsRequest>();
                FileProcess.importName(Global.importFileName);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                System.out.println("�ļ��Ҳ���");
            }
            System.out.println(Global.importFileName);
            for (DnsRequest tmpRequest : Global.dnsRecords) {
                TableItem tmpItem = new TableItem(Global.rawNameTable,
                        SWT.None);
                tmpItem.setText(new String[] {
                        Integer.toString(Global.rawNameTable
                                .getItemCount()),
                        tmpRequest.getQueryName() });
            }
        }
    }
    
    private void clearAllTable() {
        Global.rawNameTable.clearAll();
        Global.rawNameTable.setItemCount(0);

        Global.blockTable.clearAll();
        Global.blockTable.setItemCount(0);

        Global.timeoutTable.clearAll();
        Global.timeoutTable.setItemCount(0);

        Global.polluteTable.clearAll();
        Global.polluteTable.setItemCount(0);

        Global.otherTable.clearAll();
        Global.otherTable.setItemCount(0);
    }

    /** ������ʼ������ť */
    private Button addAnalyseButton(Composite buttonComposite) {
        Button analyseButton = new Button(buttonComposite, SWT.PUSH);
        analyseButton.setText("��ʼ����");
        analyseButton.pack();
        analyseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Global.exportButton.setEnabled(false);

                DnsScan dnsScan = new DnsScan();
                Global.rawNameTable.clearAll();
                Global.rawNameTable.setItemCount(0);

                // ���߳�
                dnsScan.start();
                Global.exportButton.setEnabled(true);
            }
        });
        analyseButton.setEnabled(false);

        return analyseButton;
    }

    private Button addExportButton(Composite buttonComposite) {
        Button outputButton = new Button(buttonComposite, SWT.PUSH);
        outputButton.setText("�����������");
        outputButton.pack();
        outputButton.setEnabled(false);

        outputButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                exportSelected(Global.dnsRecords);
                
            }
        });

        return outputButton;
    }
    
    /** �������� */
    private void exportSelected(ArrayList<DnsRequest> dnsRequests){
        FileDialog dialog = new FileDialog(shell, SWT.SAVE);
        String[] filterName = new String[] { "*.csv", "*" };
        String[] filterExtension = new String[] { "*.csv", "*" };
        String filterPath = ".//";

        dialog.setFilterPath(filterPath);
        dialog.setFilterExtensions(filterExtension);
        dialog.setFilterNames(filterName);

        Global.exportFileName = dialog.open();

		if (Global.exportFileName != null) {
			try {
				FileProcess.exportResult(Global.exportFileName, dnsRequests);
			} catch (IOException e1) {
				e1.printStackTrace();
				System.err.println("�޷�д���ļ�");
			}

			System.out.println(Global.exportFileName);
		}
    }

    /** ԭʼDNS�б� */
    private void addRawNameTab(TabFolder tabFolder) {
        TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
        tabItem.setText("DNS�������");
        Global.rawNameTable = new Table(tabFolder, SWT.BORDER | SWT.VIRTUAL);
        Global.rawNameTable.setHeaderVisible(true);

        addCol(Global.rawNameTable, "#", numWidth);
        TableColumn nameColumn = new TableColumn(Global.rawNameTable, SWT.None);
        nameColumn.setText("����");
        nameColumn.setWidth(150);
        TableColumn udpResultColumn = new TableColumn(Global.rawNameTable,
                SWT.None);
        udpResultColumn.setText("UDP�������");
        udpResultColumn.setWidth(200);
        TableColumn tcpResultColumn = new TableColumn(Global.rawNameTable,
                SWT.None);
        tcpResultColumn.setText("TCP�������");
        tcpResultColumn.setWidth(200);
        
        addCol(Global.rawNameTable, "RTT", numWidth);

        tabItem.setControl(Global.rawNameTable);

    }

    /** ����������ʱ��ǩҳ */
    private void addTimeOutTab(TabFolder tabFolder) {
        TabItem tabItem = new TabItem(tabFolder, SWT.None);
        tabItem.setText("������ʱ");

        Global.timeoutTable = new Table(tabFolder, SWT.BORDER | SWT.VIRTUAL);
        Global.timeoutTable.setHeaderVisible(true);

        addCol(Global.timeoutTable, "#", numWidth);
        addCol(Global.timeoutTable, "����", 150);
        addCol(Global.timeoutTable, "TCP�������", 200);

        tabItem.setControl(Global.timeoutTable);
    }

    /** DNS�����ڱ�ǩҳ */
    private void addBlockTab(TabFolder tabFolder) {
        TabItem tabItem = addTab(tabFolder, "�����ڼ�¼");

        Global.blockTable = new Table(tabFolder, SWT.BORDER | SWT.VIRTUAL);
        Global.blockTable.setHeaderVisible(true);

        addCol(Global.blockTable, "#", numWidth);
        addCol(Global.blockTable, "����", 150);
        addCol(Global.blockTable, "TCP�������", 200);
        addCol(Global.blockTable, "����", numWidth);

        tabItem.setControl(Global.blockTable);

    }

    /** DNS��Ⱦ��ǩҳ */
    private void addPolluteTab(TabFolder tabFolder) {
        TabItem tabItem = addTab(tabFolder, "DNS��Ⱦ");

        Global.polluteTable = new Table(tabFolder, SWT.BORDER | SWT.VIRTUAL);
        Global.polluteTable.setHeaderVisible(true);

        addCol(Global.polluteTable, "#", numWidth);
        addCol(Global.polluteTable, "����", 200);
        addCol(Global.polluteTable, "��ƭ��ַ", 200);
        addCol(Global.polluteTable, "��ʵ��ַ", 200);

        tabItem.setControl(Global.polluteTable);
    }

    /** �����������ҳ */
    private void addOtherTab(TabFolder tabFolder) {
        TabItem tabItem = addTab(tabFolder, "����");

        Global.otherTable = new Table(tabFolder, SWT.BORDER | SWT.VIRTUAL);
        Global.otherTable.setHeaderVisible(true);

        addCol(Global.otherTable, "#", numWidth);
        addCol(Global.otherTable, "����", 200);
        addCol(Global.otherTable, "UDP�������", 200);
        addCol(Global.otherTable, "TCP�������", 200);

        tabItem.setControl(Global.otherTable);
    }

    /** tabFolder������һ��tab */
    private TabItem addTab(TabFolder tabFolder, String name) {
        TabItem tabItem = new TabItem(tabFolder, SWT.None);
        tabItem.setText(name);

        return tabItem;
    }

    /** ���������һ�� */
    private TableColumn addCol(Table parent, String name, int width) {
        TableColumn column = new TableColumn(parent, SWT.None);
        column.setText(name);
        column.setWidth(width);

        return column;
    }
}
