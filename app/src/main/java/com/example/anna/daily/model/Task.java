package com.example.anna.daily.model;

public class Task {

    private int deal_id;
    private String taskName;
    private int task_number;


    public int getDeal_id() {
        return deal_id;
    }

    public void setDeal_id(int deal_id) {
        this.deal_id = deal_id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTask_number() {
        return task_number;
    }

    public void setTask_number(int task_number) {
        this.task_number = task_number;
    }

    @Override
    public String toString() {
        return "Task{" +
                "deal_id=" + deal_id +
                ", taskName='" + taskName + '\'' +
                ", task_number=" + task_number +
                '}';
    }
}
