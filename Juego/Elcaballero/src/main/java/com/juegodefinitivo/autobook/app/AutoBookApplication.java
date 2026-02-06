package com.juegodefinitivo.autobook.app;

import com.juegodefinitivo.autobook.config.AppConfig;
import com.juegodefinitivo.autobook.config.ConfigLoader;
import com.juegodefinitivo.autobook.engine.AutoBookGameService;
import com.juegodefinitivo.autobook.engine.AutoQuestionService;
import com.juegodefinitivo.autobook.engine.BookParser;
import com.juegodefinitivo.autobook.persistence.SaveGameRepository;
import com.juegodefinitivo.autobook.ui.ConsoleGameUI;

import java.util.Random;
import java.util.Scanner;

public class AutoBookApplication {

    public static void main(String[] args) {
        AppConfig config = new ConfigLoader().load();

        BookParser parser = new BookParser();
        AutoBookGameService gameService = new AutoBookGameService(new Random());
        AutoQuestionService questionService = new AutoQuestionService(new Random());
        SaveGameRepository repository = new SaveGameRepository(config.saveFile());

        ConsoleGameUI ui = new ConsoleGameUI(
                config,
                repository,
                parser,
                gameService,
                questionService,
                new Scanner(System.in)
        );

        ui.run();
    }
}
