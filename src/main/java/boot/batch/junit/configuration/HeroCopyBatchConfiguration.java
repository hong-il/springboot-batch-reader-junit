package boot.batch.junit.configuration;

import boot.batch.junit.domain.Hero;
import boot.batch.junit.domain.HeroBackup;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Configuration
@ConditionalOnProperty(name = "job.name", havingValue = HeroCopyBatchConfiguration.JOB_NAME)
public class HeroCopyBatchConfiguration {

    public static final String JOB_NAME = "heroCopyJob";
    private static final String STEP_NAME = "heroCopyStep";
    private static final int CHUNK_SIZE = 100;

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private EntityManagerFactory entityManagerFactory;

    private JpaPagingItemReader<Hero> jpaPagingItemReader;

    @Bean
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step())
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Hero> reader(@Value("#{jobParameters[note]") String note) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("note", "%@" + note);

        JpaPagingItemReader<Hero> reader = new JpaPagingItemReader<>();
        reader.setQueryString("SELECT h FROM Hero h where h.note like :note");
        reader.setParameterValues(parameterMap);
        reader.setPageSize(CHUNK_SIZE);
        reader.setEntityManagerFactory(entityManagerFactory);

        return reader;
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get(STEP_NAME)
                .<Hero, HeroBackup> chunk(CHUNK_SIZE)
                .reader(jpaPagingItemReader)
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public ItemProcessor<Hero, HeroBackup> processor() {
        return item -> new HeroBackup(item.getId());
    }

    @Bean
    public JpaItemWriter<HeroBackup> writer() {
        final JpaItemWriter<HeroBackup> itemWriter = new JpaItemWriter<>();
        itemWriter.setEntityManagerFactory(entityManagerFactory);
        return itemWriter;
    }
}
