import java.time.LocalDate;

public class Task {
    private int id;
    private String description;
    private LocalDate deadline;
    private boolean isDone;
    private int priority; // 1=High, 2=Medium, 3=Low

    public Task(int id, String description, LocalDate deadline, boolean isDone, int priority) {
        this.id = id;
        this.description = description;
        this.deadline = deadline;
        this.isDone = isDone;
        this.priority = priority;
    }

    public Task(String description, LocalDate deadline, int priority) {
        this(-1, description, deadline, false, priority);
    }

    // Getter & Setter
    public int getId() { return id; }
    public String getDescription() { return description; }
    public LocalDate getDeadline() { return deadline; }
    public boolean isDone() { return isDone; }
    public int getPriority() { return priority; }

    public void setId(int id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public void setDone(boolean done) { this.isDone = done; }
    public void setPriority(int priority) { this.priority = priority; }

    @Override
    public String toString() {
        return (isDone ? "[✔] " : "[ ] ") + description + " (Deadline: " + deadline + ")";
    }
}
