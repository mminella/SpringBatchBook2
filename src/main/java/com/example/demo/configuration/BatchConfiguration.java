package com.example.demo.configuration;

import com.example.demo.domain.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 *
 */
@Configuration
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> customerItemReader(@Value("#{jobParameters['customerFile']}")Resource inputFile) {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .fixedLength()
                .columns(new Range[]{new Range(1,10),
                    new Range(11, 11),
                    new Range(12, 21),
                    new Range(22, 27),
                    new Range(28,47),
                    new Range(48,56),
                    new Range(57,58),
                    new Range(59,63)})
                .names(new String[] {"firstName",
                        "middleInitial",
                        "lastName",
                        "addressNumber",
                        "street",
                        "city",
                        "state",
                        "zip"})
                .targetType(Customer.class)
                .resource(inputFile)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Customer> outputWriter(@Value("#{jobParameters['outputFile']}") Resource outputFile) {
        BeanWrapperFieldExtractor<Customer> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"firstName",
                "middleInitial",
                "lastName",
                "addressNumber",
                "street",
                "city",
                "state",
                "zip"});

        FormatterLineAggregator<Customer> lineAggregator = new FormatterLineAggregator<>();

        lineAggregator.setFieldExtractor(fieldExtractor);
        lineAggregator.setFormat("%s %s. %s, %s %s, %s %s %s");
//        lineAggregator.setFormat("%s %s.");

        return new FlatFileItemWriterBuilder<Customer>()
				.name("outputWriter")
                .resource(outputFile)
                .lineAggregator(lineAggregator)
                .build();
    }

    @Bean
    public Step copyFileStep() {
        return this.stepBuilderFactory.get("copyFileStep")
                    .<Customer, Customer>chunk(10)
                    .reader(customerItemReader(null))
                    .writer(outputWriter(null))
                    .build();
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("job")
                .start(copyFileStep())
                .build();
    }
}
