package com.example.swingdemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class SwingdemoApplication extends JFrame {

	public static void main(String[] args) {

		ConfigurableApplicationContext ctx = new SpringApplicationBuilder(SwingdemoApplication.class).headless(false).run(args);

		EventQueue.invokeLater(() -> {
			var ex = ctx.getBean(ButtonDemoSwing.class);
		});
	}

}