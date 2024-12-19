package com.hanaro.schedule_hanaro.global.utils;


import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import scala.collection.Seq;

import java.util.List;
import java.util.stream.Collectors;

public class KoreanTokenizerUtil {
    public static List<String> extractNouns(String text) {
        // Normalize text
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);

        // Tokenize
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);

        // Extract nouns
        return OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens).stream()
                .filter(token -> token.getPos().toString().equals("Noun"))
                .map(KoreanTokenJava::toString)
                .collect(Collectors.toList());
    }
}
