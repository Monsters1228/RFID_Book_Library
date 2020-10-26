package Book.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import Book.model.Book;
import Book.util.DbUtil;

/**书本Dao类
 * @author Monsters
 */
public class BookDao {
    private DbUtil dbUtil = new DbUtil();

//    图书信息查询
    public List<Book> getBookInfo(List<String> bookId)throws Exception{
        Connection con = dbUtil.getCon();
        List<Book> bookList = new ArrayList<>();
        for (String s : bookId) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM t_book WHERE bookId REGEXP '" + s + "'");
            ResultSet rs = ps.executeQuery();
            if (rs == null) {
                continue;
            }
            while (rs.next()) {
                Book book = new Book();
                book.setBookName(rs.getString("bookName"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                bookList.add(book);
            }
        }
        return bookList;
    }
}