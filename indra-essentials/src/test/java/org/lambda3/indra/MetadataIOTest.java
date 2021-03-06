package org.lambda3.indra;

/*-
 * ==========================License-Start=============================
 * Indra Essentials Module
 * --------------------------------------------------------------------
 * Copyright (C) 2016 - 2017 Lambda^3
 * --------------------------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ==========================License-End===============================
 */

import org.lambda3.indra.corpus.CorpusMetadata;
import org.lambda3.indra.corpus.CorpusMetadataBuilder;
import org.lambda3.indra.model.ModelMetadata;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class MetadataIOTest {

    private static final String NAME = "bla bla";
    private static final String LANG = "tupi-guarani";
    private static final String DESC = "The corpus descriptions bla bla bla bla";
    private static final int STEMMER = 3;
    private static final String ENCODING = "utf-8";
    private static final boolean ACCENTS = false;
    private static final boolean LOWERCASE = false;
    private static final boolean NUMBERS = false;
    private static final long MIN = 200;
    private static final long MAX = 2000000;
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList("bla", "lba"));
    private static final Map<String, Collection<String>> TRANSFORMERS = null;

    private static final String MODEL_NAME = "the model bla bla";
    private static final boolean SPARSE = true;
    private static final long DIM = 500;
    private static final long VOCAB = 400;
    private static final long WINDOW_SIZE = 10;
    private static final long FREQ = 30;

    public CorpusMetadata getCorpusMetadata() {
        return CorpusMetadataBuilder.newCorpusMetadata(NAME, LANG).
                applyStemmer(STEMMER).desc(DESC).encoding(ENCODING).removeAccents(ACCENTS).
                applyLowercase(LOWERCASE).replaceNumbers(NUMBERS).minTokenLength(MIN).maxTokenLength(MAX).
                stopWords(STOPWORDS).transformers(TRANSFORMERS).build();
    }

    @Test
    public void createCorpusMetadataTest() {
        CorpusMetadata cm = getCorpusMetadata();

        Assert.assertEquals(NAME, cm.corpusName);
        Assert.assertEquals(LANG, cm.language);
        Assert.assertEquals(DESC, cm.description);
        Assert.assertEquals(STEMMER, cm.applyStemmer);
        Assert.assertEquals(ENCODING, cm.encoding);
        Assert.assertEquals(ACCENTS, cm.removeAccents);
        Assert.assertEquals(LOWERCASE, cm.applyLowercase);
        Assert.assertEquals(NUMBERS, cm.replaceNumbers);
        Assert.assertEquals(MIN, cm.minTokenLength);
        Assert.assertEquals(MAX, cm.maxTokenLength);
        Assert.assertEquals(STOPWORDS, cm.stopWords);
        Assert.assertEquals(TRANSFORMERS, cm.transformers);
    }

    @Test
    public void corpusMetadataTest() {
        CorpusMetadata cm = getCorpusMetadata();

        try {

            File modelDir = Files.createTempDirectory("indra").toFile();
            MetadataIO.write(modelDir.getAbsolutePath(), cm);

            CorpusMetadata loaded = MetadataIO.load(modelDir.getAbsolutePath(), CorpusMetadata.class);
            Assert.assertEquals(cm, loaded);
            modelDir.delete();
            //TODO FileUtils.

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void modelMetadataTest() {
        ModelMetadata mm = new ModelMetadata(MODEL_NAME, SPARSE, DIM, VOCAB, WINDOW_SIZE, FREQ, getCorpusMetadata());

        try {
            File modelDir = Files.createTempDirectory("indra").toFile();
            MetadataIO.write(modelDir.getAbsolutePath(), mm);

            ModelMetadata loaded = MetadataIO.load(modelDir.getAbsolutePath(), ModelMetadata.class);
            System.out.println(mm.equals(loaded));
            Assert.assertEquals(mm, loaded);
            //TODO FileUtils.
            modelDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
