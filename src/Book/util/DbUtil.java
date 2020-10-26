package Book.util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 数据库连接
 * @author Monsters
 */
public class DbUtil {
    private static String Url="jdbc:mysql://localhost:3306/book?serverTimezone=UTC";
    private static String UserName="root";
    private static String Password="8826050";
    private static String jdbcName="com.mysql.cj.jdbc.Driver";
//连接数据库
    public Connection getCon()throws Exception{
        Class.forName(jdbcName);
        Connection con = DriverManager.getConnection(Url,UserName,Password);
//        System.out.println("连接数据库成功");
        return con;
    }
//    关闭数据库连接
    public void closeCon(Connection con)throws Exception{
        if(con!=null){
            con.close();
        }
    }

    public static void main(String[] args) {
        DbUtil dbUtil = new DbUtil() ;
        try {
            dbUtil.getCon();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("连接数据库失败");
        }
    }
}
