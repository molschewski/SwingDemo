package com.example.swingdemo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class SwingdemoApplication extends JFrame {

	public static void main(String[] args) {

		ConfigurableApplicationContext ctx = new SpringApplicationBuilder(SwingdemoApplication.class).headless(false).run(args);

//		EventQueue.invokeLater(() -> {
//			var ex = ctx.getBean(Viewer.class);
//		});

		// Disable the Spring way for learning purposes. I will start the viewer
		// with a fabric pattern, and I have still to learn, how to implement that
		// in spring
		EventQueue.invokeLater(() -> {
			Viewer.createViewer();
		});
	}

}