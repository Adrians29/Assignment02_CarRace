/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.adrianegl;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author adria
 */
public class Car {
    private int id;
    private double speed;
    private double distanceTravelled;
    private boolean finished;
    
    private final StackPane view;
    private final Color color;

    private Rectangle body;
    private Label numberLabel;
}