package tools.albert.synctool.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tools.albert.synctool.util.NoSyncFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

import static tools.albert.synctool.controller.RestController.*;

@Configuration
@EnableAsync
public class SpringAsyncConfig {

    @Value("${syncTimeOut}")
    public static String syncTimeOut;

    @Bean(name = "threadPoolTaskExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsynchThread::");
        executor.initialize();
        return executor;


    }

     public static void setSyncTimer(String timer){
         syncTimeOut = timer;
     }

    @Async
    public void autoSync(){
        boolean newSync = false;

        while (true) {
            try {
                logger.info("Starte Sync-Lauf!");
                for(NoSyncFile destination : zielService.getArrayListZiel()){
                    for (File source : quellService.getArrayListQuell()){
                        newSync = syncService.sync(source, destination, true);
                    }
                }
                try {
                    if (syncService!=null) {
                        Thread.sleep(Long.parseLong(syncTimeOut));
                    } else {
                        Thread.sleep(60000L);
                    }
                } catch (InterruptedException ignored) {
                }
                if(newSync){

                }
                logger.info("Beende Sync-Lauf!");
            } catch (IOException e) {

            }
        }
    }

}
