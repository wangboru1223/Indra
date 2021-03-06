package org.lambda3.indra.mongo;

/*-
 * ==========================License-Start=============================
 * Indra Mongo Module
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

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.commons.math3.linear.RealVector;
import org.bson.Document;
import org.lambda3.indra.AnalyzedTerm;
import org.lambda3.indra.core.vs.AbstractVectorSpace;
import org.lambda3.indra.filter.Filter;
import org.lambda3.indra.exception.IndraRuntimeException;
import org.lambda3.indra.model.ModelMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class MongoVectorSpace extends AbstractVectorSpace {
    private static final String TERM_FIELD_NAME = "term";
    private static final String VECTOR_FIELD_NAME = "vector";
    private static final String TERMS_COLL_NAME = "terms";
    private static final String METADATA_COLL_NAME = "metadata";

    private Logger logger = LoggerFactory.getLogger(getClass());
    private MongoClient mongoClient;
    private final String dbName;

    MongoVectorSpace(MongoClient client, String dbName) {
        logger.info("Creating new vector space from {}", dbName);
        this.mongoClient = client;
        this.dbName = dbName;
    }

    @Override
    protected ModelMetadata loadMetadata() {
        MongoDatabase db = this.mongoClient.getDatabase(dbName);
        MongoCollection<Document> metadataColl = db.getCollection(METADATA_COLL_NAME);
        //TODO metadata needs to include CM

        if (metadataColl.count() > 1) {
            throw new IndraRuntimeException("This model is misconfigured. Contact the support with the following message: model metadata has more than one entry.");
        }

        if (metadataColl.count() == 1) {
            logger.debug("Using stored metadata of {}", dbName);
            Document storedMetadata = metadataColl.find().first();
            return new ModelMetadata(storedMetadata);
        } else {
            logger.debug("No metadata found in {}, using defaults.", dbName);
            //TODO throws an exception.
            //no default supported anymore.
            return null;
        }
    }

    @Override
    protected Map<String, RealVector> collectVectors(Iterable<? extends String> terms) {
        logger.info("Collecting term vectors from {}", dbName);
        FindIterable<Document> docs = getTermsColl().find(Filters.in(TERM_FIELD_NAME, terms));
        Map<String, RealVector> vectors = new HashMap<>();
        if (docs != null) {
            for (Document doc : docs) {
                //BinaryCodecs.unmarshall(b, metadata.sparse, metadata.dimensions);
                //TODO other problem hrer vectors.put(doc.getString(TERM_FIELD_NAME), unmarshall(doc, getMetadata().getDimensions()));
            }
        }

        return vectors;
    }

    @Override
    public Map<String, RealVector> getNearestVectors(AnalyzedTerm term, int topk, Filter filter) {
        throw new IndraRuntimeException("This model does not support 'nearest' function.");
    }

    @Override
    public Collection<String> getNearestTerms(AnalyzedTerm term, int topk, Filter filter) {
        throw new IndraRuntimeException("This model does not support 'nearest' function.");
    }

    @Override
    public Collection<String> getNearestTerms(double[] vector, int topk) {
        throw new IndraRuntimeException("This model does not support 'nearest' function.");
    }

    private MongoCollection<Document> getTermsColl() {
        return this.mongoClient.getDatabase(dbName).getCollection(TERMS_COLL_NAME);
    }

    @Override
    public void close() throws IOException {
        //do nothing. don't close the mongo client here because it is a shared instance.
    }
}
