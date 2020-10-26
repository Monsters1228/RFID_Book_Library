package Library;

import Library.Main;
import java.awt.EventQueue;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
/**
 * @author Monsters
 */
public class Login {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        Login window = new Login();
    }

    /**
     * Create the application.
     */
    Login() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        //设置窗口居中
        int windowWidth = frame.getWidth();
        int windowHeight = frame.getHeight();
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        frame.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);

        JButton btnNewButton = new JButton("借书");
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Main();
            }
        });
        btnNewButton.setBounds(87, 173, 225, 89);
        frame.getContentPane().add(btnNewButton);

        JButton btnNewButton_1 = new JButton("还书");
        btnNewButton_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new Main();
            }
        });
        btnNewButton_1.setBounds(451, 173, 225, 89);
        frame.getContentPane().add(btnNewButton_1);
    }
}
