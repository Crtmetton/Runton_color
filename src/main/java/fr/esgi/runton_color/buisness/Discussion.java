package fr.esgi.runton_color.buiness;

import java.util.List;

public class Discussion {
    private int id;
    private Course course;
    private List<Message> messages;

    public Discussion(int unId, Course uneCourse, List<Message> desMessages) {
        this.id = unId;
        this.course = uneCourse;
        this.messages = desMessages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> desMessages) {
        this.messages = desMessages;
    }

}
