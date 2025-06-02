import javax.swing.*;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Notifier {
    private TaskController controller;

    public Notifier(TaskController controller) {
        this.controller = controller;
        startNotificationChecker();
    }

    private void startNotificationChecker() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            List<Task> nearDeadlines = controller.loadTasksByDeadline(LocalDate.now().plusDays(1));
            for (Task task : nearDeadlines) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null,
                            "Reminder: Tugas '" + task.getDescription() + "' mendekati deadline!",
                            "Notifikasi Deadline", JOptionPane.INFORMATION_MESSAGE);
                });
            }
        }, 0, 1, TimeUnit.MINUTES);
    }
}
