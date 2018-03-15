package com.example.demo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@EnableBatchProcessing
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		List<String> newArgs = new ArrayList<>(2);
		newArgs.add("customerFile=file:///D:/Users/mminella/IdeaProjects/demo/src/main/resources/data/customer_fixed.txt");
		newArgs.add("outputFile=file:///D:/Users/mminella/IdeaProjects/demo/target/copied_customer.txt");

		SpringApplication.run(DemoApplication.class, newArgs.toArray(new String[2]));
	}
}
