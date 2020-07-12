package aorante;

import me.libme.module.webboot.ApplicationBoot;
import me.libme.module.webboot.EnableCppWebAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@EnableCppWebAll
@SpringBootApplication
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) },
        basePackages={"me.libme","aorante"}
)
public class AoranteApplication {

    private static final Logger LOGGER= LoggerFactory.getLogger(AoranteApplication.class);

    public static void main(String[] args) {

        ApplicationBoot.start(new Object[]{AoranteApplication.class},args);


    }

}
