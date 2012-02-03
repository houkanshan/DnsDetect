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

        // 设置布局结构
        GridLayout shellLayout = new GridLayout(1, true);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        // RowLayout shellLayout = new RowLayout(SWT.VERTICAL);
        // shellLayout.fill = true;
        // shellLayout.wrap = false;
        // FillLayout shellLayout = new FillLayout(SWT.VERTICAL);
        shell.setLayoutData(gridData);
        shell.setLayout(shellLayout);
        shell.setText("DNS批量解析分析工具");

        //创建按钮组
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

        // 打开主界面
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

    /** 创建菜单 */
    private void addMenu(Composite parent) {
        Menu bar = new Menu(parent.getShell(), SWT.BAR);
        shell.setMenuBar(bar);
        MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
        fileItem.setText("文件(&F)");
        MenuItem settingItem = new MenuItem(bar, SWT.CASCADE);
        settingItem.setText("配置(&C)");
        settingItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addSettingDialog();
            }
        });

        // 创建子菜单
        Menu fileSubMenu = new Menu(parent.getShell(), SWT.DROP_DOWN);
        fileItem.setMenu(fileSubMenu);
        
        MenuItem importSubItem = new MenuItem(fileSubMenu, SWT.PUSH);
        importSubItem.setText("导入(&O)");
        importSubItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                importSelected();
            }
        });
        
        //分割线1
        MenuItem sep1 = new MenuItem(fileSubMenu, SWT.SEPARATOR);
        sep1.setText("导出");
        
        MenuItem exportAllSubItem = new MenuItem(fileSubMenu, SWT.PUSH | SWT.BORDER_DOT);
        exportAllSubItem.setText("导出完整结果(&A)");
        exportAllSubItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                exportSelected(Global.dnsRecords);
            }
        });
        
        MenuItem exportNotFoundSubItem = new MenuItem(fileSubMenu, SWT.PUSH);
        exportNotFoundSubItem.setText("导出不存在记录结果");
        exportNotFoundSubItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                exportSelected(Global.blockRecords);
            }
        });
       
        MenuItem exportPolluteSubItem = new MenuItem(fileSubMenu, SWT.PUSH);
        exportPolluteSubItem.setText("导出污染结果");
        exportPolluteSubItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                exportSelected(Global.polluteRecords);
            }
        });
        
        MenuItem exportOtherSubItem = new MenuItem(fileSubMenu, SWT.PUSH);
        exportOtherSubItem.setText("导出其他结果");
        exportOtherSubItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                exportSelected(Global.otherRecords);
            }
        });
        
        //分割线2
        MenuItem sep2 = new MenuItem(fileSubMenu, SWT.SEPARATOR);
        sep2.setText("退出");
        
        MenuItem exitSubItem = new MenuItem(fileSubMenu, SWT.PUSH);
        exitSubItem.setText("退出(&E)");
        exitSubItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                display.dispose();
            }
        });
    }
    
    //创建设置对话框
    private void addSettingDialog(){
        //创建对话框
        final Shell settingDialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        settingDialog.setText("设置");
        FormLayout formLayout = new FormLayout ();
        formLayout.marginWidth = 10;
        formLayout.marginHeight = 10;
        formLayout.spacing = 10;
        settingDialog.setLayout (formLayout);
        

        //创建输入框提示
        Label setServerTitle = new Label(settingDialog, SWT.None);
        setServerTitle.setText("服务器地址");
        FormData data = new FormData ();
        setServerTitle.setLayoutData (data);

        //创建取消按钮
                Button cancel = new Button(settingDialog, SWT.PUSH);
                cancel.setText("取消");
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
        
        //创建输入框
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
        
        //创建按钮组
        //创建确定按钮
        Button ok = new Button(settingDialog, SWT.PUSH);
        ok.setText("确定");
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

    /** 创建导入域名按钮 */
    private Button addImportButton(Composite buttonComposite) {
        Button importNameButton = new Button(buttonComposite, SWT.PUSH);
        importNameButton.setText("导入域名文件");
        importNameButton.pack();
        importNameButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                importSelected();
            }
        });

        return importNameButton;
    }

    /** 导入调用 */
    private void importSelected(){
        FileDialog dialog = new FileDialog(shell, SWT.OPEN);
        String[] filterNames = new String[] { "*.csv格式文件", "所有文件(*)" };
        String[] filterExtensions = new String[] { "*.csv", "*" };
        String filterPath = "./";
        // String platform = SWT.getPlatform();
        dialog.setFilterNames(filterNames);
        dialog.setFilterExtensions(filterExtensions);
        dialog.setFilterPath(filterPath);

        Global.importFileName = dialog.open();
        if (Global.importFileName != null) {
            // 如果导入了文件
            clearAllTable();
            Global.analyseButton.setEnabled(true);
            Global.exportButton.setEnabled(false);
            try {
            	Global.dnsRecords = new ArrayList<DnsRequest>();
                FileProcess.importName(Global.importFileName);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                System.out.println("文件找不到");
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

    /** 创建开始分析按钮 */
    private Button addAnalyseButton(Composite buttonComposite) {
        Button analyseButton = new Button(buttonComposite, SWT.PUSH);
        analyseButton.setText("开始分析");
        analyseButton.pack();
        analyseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Global.exportButton.setEnabled(false);

                DnsScan dnsScan = new DnsScan();
                Global.rawNameTable.clearAll();
                Global.rawNameTable.setItemCount(0);

                // 新线程
                dnsScan.start();
                Global.exportButton.setEnabled(true);
            }
        });
        analyseButton.setEnabled(false);

        return analyseButton;
    }

    private Button addExportButton(Composite buttonComposite) {
        Button outputButton = new Button(buttonComposite, SWT.PUSH);
        outputButton.setText("导出完整结果");
        outputButton.pack();
        outputButton.setEnabled(false);

        outputButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                exportSelected(Global.dnsRecords);
                
            }
        });

        return outputButton;
    }
    
    /** 导出调用 */
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
				System.err.println("无法写入文件");
			}

			System.out.println(Global.exportFileName);
		}
    }

    /** 原始DNS列表 */
    private void addRawNameTab(TabFolder tabFolder) {
        TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
        tabItem.setText("DNS解析结果");
        Global.rawNameTable = new Table(tabFolder, SWT.BORDER | SWT.VIRTUAL);
        Global.rawNameTable.setHeaderVisible(true);

        addCol(Global.rawNameTable, "#", numWidth);
        TableColumn nameColumn = new TableColumn(Global.rawNameTable, SWT.None);
        nameColumn.setText("域名");
        nameColumn.setWidth(150);
        TableColumn udpResultColumn = new TableColumn(Global.rawNameTable,
                SWT.None);
        udpResultColumn.setText("UDP解析结果");
        udpResultColumn.setWidth(200);
        TableColumn tcpResultColumn = new TableColumn(Global.rawNameTable,
                SWT.None);
        tcpResultColumn.setText("TCP解析结果");
        tcpResultColumn.setWidth(200);
        
        addCol(Global.rawNameTable, "RTT", numWidth);

        tabItem.setControl(Global.rawNameTable);

    }

    /** 解析域名超时标签页 */
    private void addTimeOutTab(TabFolder tabFolder) {
        TabItem tabItem = new TabItem(tabFolder, SWT.None);
        tabItem.setText("解析超时");

        Global.timeoutTable = new Table(tabFolder, SWT.BORDER | SWT.VIRTUAL);
        Global.timeoutTable.setHeaderVisible(true);

        addCol(Global.timeoutTable, "#", numWidth);
        addCol(Global.timeoutTable, "域名", 150);
        addCol(Global.timeoutTable, "TCP解析结果", 200);

        tabItem.setControl(Global.timeoutTable);
    }

    /** DNS不存在标签页 */
    private void addBlockTab(TabFolder tabFolder) {
        TabItem tabItem = addTab(tabFolder, "不存在记录");

        Global.blockTable = new Table(tabFolder, SWT.BORDER | SWT.VIRTUAL);
        Global.blockTable.setHeaderVisible(true);

        addCol(Global.blockTable, "#", numWidth);
        addCol(Global.blockTable, "域名", 150);
        addCol(Global.blockTable, "TCP解析结果", 200);
        addCol(Global.blockTable, "排名", numWidth);

        tabItem.setControl(Global.blockTable);

    }

    /** DNS污染标签页 */
    private void addPolluteTab(TabFolder tabFolder) {
        TabItem tabItem = addTab(tabFolder, "DNS污染");

        Global.polluteTable = new Table(tabFolder, SWT.BORDER | SWT.VIRTUAL);
        Global.polluteTable.setHeaderVisible(true);

        addCol(Global.polluteTable, "#", numWidth);
        addCol(Global.polluteTable, "域名", 200);
        addCol(Global.polluteTable, "欺骗地址", 200);
        addCol(Global.polluteTable, "真实地址", 200);

        tabItem.setControl(Global.polluteTable);
    }

    /** 其他解析结果页 */
    private void addOtherTab(TabFolder tabFolder) {
        TabItem tabItem = addTab(tabFolder, "其他");

        Global.otherTable = new Table(tabFolder, SWT.BORDER | SWT.VIRTUAL);
        Global.otherTable.setHeaderVisible(true);

        addCol(Global.otherTable, "#", numWidth);
        addCol(Global.otherTable, "域名", 200);
        addCol(Global.otherTable, "UDP解析结果", 200);
        addCol(Global.otherTable, "TCP解析结果", 200);

        tabItem.setControl(Global.otherTable);
    }

    /** tabFolder中增加一个tab */
    private TabItem addTab(TabFolder tabFolder, String name) {
        TabItem tabItem = new TabItem(tabFolder, SWT.None);
        tabItem.setText(name);

        return tabItem;
    }

    /** 表格中增加一列 */
    private TableColumn addCol(Table parent, String name, int width) {
        TableColumn column = new TableColumn(parent, SWT.None);
        column.setText(name);
        column.setWidth(width);

        return column;
    }
}
