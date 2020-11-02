package Library;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Book.model.Book;
import UHF18.UHFReader;
import Book.dao.BookDao;

/**
 * @author Monsters
 */
public class Main {
    private JFrame frame;
    private JTextField bookNum;
    private UHFReader uhf = new UHFReader();
    private int fCmdRet=0x30;
    private JTable table;
//    定时器执行标志
    private boolean timeQyery=false;
//    单次询查结束标志
    private boolean Qyeryflag=false;
//    图书ID
    private List<String> bookId = new ArrayList<String>();
    private List<Book> bookList = new ArrayList<>();
    private BookDao bookDao = new BookDao();
//    表单数据
    private Object[][] data={};
    private String[] columnNames;
    private int checkItem;
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        Main window = new Main();
        window.frame.setVisible(true);
    }

    /**
     * Create the application.
     */
    Main() {
//       初始化界面
        initialize();
//        5s后自动停止读取
        autoTime();
//        打开串口并读取id
        startCom();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 900, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗口居中
        int windowWidth = frame.getWidth();
        int windowHeight = frame.getHeight();
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        frame.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);
        frame.getContentPane().setLayout(null);

        //返回按钮
        JButton btnNewButton = new JButton("返回");
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //关闭串口
                closeCom();
                //关闭页面
                frame.dispose();
                new Login();
            }
        });
        btnNewButton.setBounds(20, 21, 93, 39);
        frame.getContentPane().add(btnNewButton);

        JLabel lblNewLabel = new JLabel("数量");
        lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 18));
        lblNewLabel.setBounds(258, 31, 36, 21);
        frame.getContentPane().add(lblNewLabel);

        bookNum = new JTextField();
        bookNum.setBounds(304, 31, 66, 21);
        frame.getContentPane().add(bookNum);
        bookNum.setColumns(10);
        bookNum.setText("0");

        //删除
        JButton btnNewButton_1 = new JButton("删除");
        btnNewButton_1.setForeground(Color.RED);
        btnNewButton_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkItem = table.getSelectedRow();
                removerItem(checkItem);
            }
        });
        btnNewButton_1.setBounds(529, 21, 93, 39);
        frame.getContentPane().add(btnNewButton_1);

        //确认按钮
        JButton btnNewButton_2 = new JButton("确认");
        btnNewButton_2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //关闭串口
                //closeCom();
                timeQyery=false;
            }
        });
        btnNewButton_2.setBounds(700, 21, 93, 39);
        frame.getContentPane().add(btnNewButton_2);

        //表格
        JPanel panel = new JPanel();
        panel.setBounds(33, 119, 819, 416);
        frame.getContentPane().add(panel);
        panel.setLayout(new BorderLayout(0, 0));
//        表头
        columnNames = new String[]{"书名", "作者", "出版社"};
        DefaultTableModel model = new DefaultTableModel(data,columnNames);
        table = new JTable(model);
//        设置表格的样式
        table.setFont(new Font(null, Font.PLAIN, 14));
        table.setSelectionForeground(Color.DARK_GRAY);
//        设置表头样式
        table.getTableHeader().setFont(new Font(null, Font.BOLD, 14));
        table.getTableHeader().setForeground(Color.RED);

        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(30);
        panel.add(table.getTableHeader(), BorderLayout.PAGE_START);
        panel.add(table, BorderLayout.CENTER);
    }

    //    打开串口并启动读写器
    private void startCom(){
        byte ComAddr=(byte)255;
        byte baudRate = (byte)5;
        boolean flag = false;
        baudRate=(byte)5;
        fCmdRet=uhf.AutoOpenCom(ComAddr, baudRate);
        if(fCmdRet == 0) {
            flag = true;
            System.out.println("打开串口成功");
            timeQyery=true;
            timeVoid();
        }
        if(!flag){
            System.out.println("打开串口失败");
        }
    }

//    读取数据
        private void timeVoid(){
        final Timer timer = new Timer();
        TimerTask tt =new TimerTask() {
            @Override
            public void run() {
                if(timeQyery)
                {
                    if(Qyeryflag){return;}
                    Qyeryflag=true;
                    try {
                        Inventory();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Qyeryflag=false;
                    timer.cancel();
                    timeQyery=true;
                    timeVoid();
                }
            }
        };
        timer.schedule(tt, 500);
    }
//自动关闭读取
        private void autoTime(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeQyery = false;
                System.out.println("停止读取数据");
                for(int i=0;i<bookId.size();i++){
                    System.out.println(bookId.get(i));
                }
            }
        },5000);
    }
    private void Inventory() throws Exception {
        String[] EPC;
        EPC=uhf.Inventory();
        if(EPC != null){
            for (String sepc : EPC) {
                boolean inlist = false;
                for (String temp : bookId) {
                    if (temp.equals(sepc)) {
                        inlist = true;
                        break;
                    }
                }
                if (!inlist) {
                    bookId.add(sepc);
                }
            }
            bookNum.setText(String.valueOf(bookList.size()));
        }
//        获取书本信息
        bookList = bookDao.getBookInfo(bookId);
        data = getObjectBookList(bookList);
        DefaultTableModel model = new DefaultTableModel(data,columnNames);
        table.setModel(model);
        System.out.println("查询");
    }
//    关闭串口
    private void closeCom(){
        int FrmHandle=uhf.FrmHandle;
        fCmdRet=uhf.CloseByCom(FrmHandle);
        if(fCmdRet == 0){
            System.out.println("关闭串口成功");
        }else{
            System.out.println("关闭串口失败");
        }
    }
//    将bookList转为Object
    private Object[][] getObjectBookList(List<Book> bookList){
        Object[][] data= new String[bookList.size()][3];
        for(int i=0;i<bookList.size();i++){
            data[i][0]=bookList.get(i).getBookName();
            data[i][1]=bookList.get(i).getAuthor();
            data[i][2]=bookList.get(i).getPublisher();
        }
        return data;
    }
//    删除所选项
    private void removerItem(int checkItem){

        bookId.remove(checkItem);
        bookList.remove(checkItem);
        data=getObjectBookList(bookList);
        DefaultTableModel model = new DefaultTableModel(data,columnNames);
        table.setModel(model);
        System.out.println("移除");
        System.out.println(bookList.size());
    }
}
