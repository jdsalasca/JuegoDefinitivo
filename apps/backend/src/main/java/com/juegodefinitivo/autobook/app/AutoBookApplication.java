package com.juegodefinitivo.autobook.app;

import com.juegodefinitivo.autobook.config.AppConfig;
import com.juegodefinitivo.autobook.config.ConfigLoader;
import com.juegodefinitivo.autobook.engine.AutoQuestionService;
import com.juegodefinitivo.autobook.engine.BookParser;
import com.juegodefinitivo.autobook.engine.GameEngineService;
import com.juegodefinitivo.autobook.ingest.BookCatalogService;
import com.juegodefinitivo.autobook.ingest.BookImportService;
import com.juegodefinitivo.autobook.ingest.BookLoaderService;
import com.juegodefinitivo.autobook.ingest.ExtractorResolver;
import com.juegodefinitivo.autobook.ingest.PdfTextExtractor;
import com.juegodefinitivo.autobook.ingest.TxtTextExtractor;
import com.juegodefinitivo.autobook.narrative.DialogueService;
import com.juegodefinitivo.autobook.narrative.NarrativeBuilder;
import com.juegodefinitivo.autobook.persistence.SaveGameRepository;
import com.juegodefinitivo.autobook.ui.ConsoleGameUI;

import java.util.Random;
import java.util.Scanner;

public class AutoBookApplication {

    public static void main(String[] args) {
        AppConfig config = new ConfigLoader().load();
        Random random = new Random();

        BookParser parser = new BookParser();
        AutoQuestionService questionService = new AutoQuestionService(random);
        NarrativeBuilder narrativeBuilder = new NarrativeBuilder(questionService, random);
        GameEngineService gameEngine = new GameEngineService(new DialogueService(), random);

        ExtractorResolver resolver = new ExtractorResolver(new TxtTextExtractor(), new PdfTextExtractor());
        BookLoaderService loader = new BookLoaderService(resolver, parser);

        SaveGameRepository repository = new SaveGameRepository(config.saveFile());
        BookCatalogService catalog = new BookCatalogService(config.booksDir());
        BookImportService importer = new BookImportService(config.booksDir());

        ConsoleGameUI ui = new ConsoleGameUI(
                config,
                repository,
                catalog,
                importer,
                loader,
                narrativeBuilder,
                gameEngine,
                new Scanner(System.in)
        );
        ui.run();
    }
}
