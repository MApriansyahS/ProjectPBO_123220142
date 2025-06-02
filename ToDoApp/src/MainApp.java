import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.UIManager;

public class MainApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize Dark Mode");
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            TaskController controller = new TaskController();
            new Notifier(controller);
            new TaskView(controller);
        });
    }
}
