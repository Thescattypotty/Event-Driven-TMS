package org.driventask.task.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

//@Configuration
@Slf4j
@RequiredArgsConstructor
public class DatabaseInitializer {

    private final DatabaseClient databaseClient;

    public Mono<Void> initializeDatabase(){
        return databaseClient.sql("CREATE TABLE IF NOT EXISTS tasks (" +
                "id UUID PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "due_date TIMESTAMP, " +
                "priority VARCHAR(50), " +
                "status VARCHAR(50), " +
                "project_id VARCHAR(255) NOT NULL, " +
                "history_of_task TEXT, " +
                "files_included TEXT, " +
                "user_assigned VARCHAR(255), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")")
                .fetch()
                .rowsUpdated()
                .then()
                .then(databaseClient.sql("CREATE OR REPLACE FUNCTION update_updated_at_column() " +
                        "RETURNS TRIGGER AS $$ " +
                        "BEGIN " +
                        "NEW.updated_at = CURRENT_TIMESTAMP; " +
                        "RETURN NEW; " +
                        "END; $$ " +
                        "LANGUAGE plpgsql;")
                        .fetch()
                        .rowsUpdated())
                .then(databaseClient.sql("CREATE TRIGGER update_tasks_updated_at " +
                        "BEFORE UPDATE ON tasks " +
                        "FOR EACH ROW " +
                        "EXECUTE PROCEDURE update_updated_at_column();")
                        .fetch()
                        .rowsUpdated())
                .then();
    }
    @PostConstruct
    public void init(){
        initializeDatabase()
            .doOnError(error -> log.error("Database initiliazation failed :" + error.getMessage()))
            .subscribe();
    }
}
