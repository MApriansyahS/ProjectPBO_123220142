import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskController {

    public List<Task> loadTasksByStatus(Integer isDoneFilter) {
        List<Task> tasks = new ArrayList<>();
        String sql;
        if (isDoneFilter == null) {
            sql = "SELECT * FROM tasks ORDER BY deadline ASC";
        } else {
            sql = "SELECT * FROM tasks WHERE is_done = ? ORDER BY deadline ASC";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (isDoneFilter != null) {
                stmt.setInt(1, isDoneFilter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String description = rs.getString("description");
                    LocalDate deadline = rs.getDate("deadline").toLocalDate();
                    boolean isDone = rs.getBoolean("is_done");
                    int priority = rs.getInt("priority");

                    tasks.add(new Task(id, description, deadline, isDone, priority));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    public List<Task> loadTasksByDeadline(LocalDate maxDeadline) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE deadline <= ? AND is_done = false ORDER BY deadline ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(maxDeadline));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                LocalDate deadline = rs.getDate("deadline").toLocalDate();
                boolean isDone = rs.getBoolean("is_done");
                int priority = rs.getInt("priority");

                tasks.add(new Task(id, description, deadline, isDone, priority));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    public void addTask(Task task) {
        String sql = "INSERT INTO tasks(description, deadline, is_done, priority) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getDescription());
            stmt.setDate(2, java.sql.Date.valueOf(task.getDeadline()));
            stmt.setBoolean(3, task.isDone());
            stmt.setInt(4, task.getPriority());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void toggleTaskStatus(int id, boolean newStatus) {
        String sql = "UPDATE tasks SET is_done = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, newStatus);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(Task task) {
        String sql = "UPDATE tasks SET description = ?, deadline = ?, priority = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getDescription());
            stmt.setDate(2, java.sql.Date.valueOf(task.getDeadline()));
            stmt.setInt(3, task.getPriority());
            stmt.setInt(4, task.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
