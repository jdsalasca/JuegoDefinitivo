package com.juegodefinitivo.autobook.config;

import com.juegodefinitivo.autobook.engine.AutoQuestionService;
import com.juegodefinitivo.autobook.engine.BookParser;
import com.juegodefinitivo.autobook.engine.GameEngineService;
import com.juegodefinitivo.autobook.ingest.BookCatalogService;
import com.juegodefinitivo.autobook.ingest.BookImportService;
import com.juegodefinitivo.autobook.ingest.BookLoaderService;
import com.juegodefinitivo.autobook.ingest.BookTextNormalizer;
import com.juegodefinitivo.autobook.ingest.ExtractorResolver;
import com.juegodefinitivo.autobook.ingest.PdfTextExtractor;
import com.juegodefinitivo.autobook.ingest.TxtTextExtractor;
import com.juegodefinitivo.autobook.narrative.DialogueService;
import com.juegodefinitivo.autobook.narrative.NarrativeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class AppBeansConfig {

    @Bean
    public AppConfig appConfig() {
        return new ConfigLoader().load();
    }

    @Bean
    public Random random() {
        return new Random(42);
    }

    @Bean
    public BookParser bookParser() {
        return new BookParser();
    }

    @Bean
    public AutoQuestionService autoQuestionService(Random random) {
        return new AutoQuestionService(random);
    }

    @Bean
    public NarrativeBuilder narrativeBuilder(AutoQuestionService questionService, Random random) {
        return new NarrativeBuilder(questionService, random);
    }

    @Bean
    public GameEngineService gameEngineService(Random random) {
        return new GameEngineService(new DialogueService(), random);
    }

    @Bean
    public ExtractorResolver extractorResolver() {
        return new ExtractorResolver(new TxtTextExtractor(), new PdfTextExtractor());
    }

    @Bean
    public BookLoaderService bookLoaderService(ExtractorResolver resolver, BookTextNormalizer normalizer, BookParser parser) {
        return new BookLoaderService(resolver, normalizer, parser);
    }

    @Bean
    public BookCatalogService bookCatalogService(AppConfig config) {
        return new BookCatalogService(config.booksDir());
    }

    @Bean
    public BookImportService bookImportService(AppConfig config) {
        return new BookImportService(config.booksDir());
    }
}
