/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * 
 *
 * @author duhanasir
 */


import java.io.Serializable;
import java.time.LocalDate;



public class Expense implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private double amount;
    private String category;
    private LocalDate date;
    private String notes;
    private boolean recurring;

    public Expense() {
    }

    public Expense(String name, double amount, String category,
                   LocalDate date, String notes, boolean recurring) {
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.notes = notes;
        this.recurring = recurring;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }
}