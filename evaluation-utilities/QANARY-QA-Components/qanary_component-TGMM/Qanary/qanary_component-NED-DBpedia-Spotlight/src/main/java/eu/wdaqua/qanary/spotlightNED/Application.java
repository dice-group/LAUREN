package eu.wdaqua.qanary.spotlightNED;

import eu.wdaqua.qanary.commons.QanaryMessage;
import eu.wdaqua.qanary.component.QanaryComponent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("eu.wdaqua.qanary.component")
public class Application {


    @Bean
    public QanaryComponent qanaryComponent() {
        return new DBpediaSpotlightNED();
    }

    /**
     * default main, can be removed later
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
