/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.open.kmi.uciad.util;

import info.aduna.iteration.Iterations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

/**
 * Manages all the interactions with the underlying triple store.
 * @author se3535
 */
public class UCIADRepositoryManager {

    /**
     * This method adds RDF Statements from the file provided into the triple store
     * under the provided context.
     * @param filePath
     * @param contextURI
     */
    public static void add(String filePath, String contextURI) {
		try {
			Repository uciadRepository = new HTTPRepository(
					System.getProperty("repURI"), System.getProperty("repID"));
			uciadRepository.initialize();
			RepositoryConnection con = null;
			con = uciadRepository.getConnection();
			try {
				File file = new File(filePath);
				ValueFactory valFactory = uciadRepository.getValueFactory();

				URI superContext = valFactory.createURI(contextURI);
				con.add(file, NameSpace.UCIAD, RDFFormat.RDFXML, superContext);

			} catch (IOException ex) {
				ex.printStackTrace();
				Logger.getLogger(UCIADRepositoryManager.class.getName()).log(
						Level.ERROR, null, ex);
			} catch (RDFParseException ex) {
				System.out.println(filePath);
				Logger.getLogger(UCIADRepositoryManager.class.getName()).log(
						Level.ERROR, null, ex);
			} catch (RepositoryException ex) {
				System.out.println(filePath);
				Logger.getLogger(UCIADRepositoryManager.class.getName()).log(
						Level.ERROR, null, ex);
			} finally {
				con.close();
			}
		} catch (OpenRDFException ex) {
            Logger.getLogger(UCIADRepositoryManager.class.getName()).log(Level.ERROR, null, ex);
        } 

    }

    /**
     * This method removes RDF statements from the triple store contained
     * in the file provided. It uses a temporary repository to list all the
     * statements and then remove them from the actual repository.
     * @param filePath
     * @param superContextStr
     */
    public static void remove(String filePath, String superContextStr) {
        try {

            Repository luceroRepository = new HTTPRepository(System.getProperty("repURI"), System.getProperty("repID"));
            luceroRepository.initialize();
            RepositoryConnection con = luceroRepository.getConnection();
            ValueFactory valFactory = luceroRepository.getValueFactory();

            Repository tempRep = new SailRepository(new MemoryStore());
            tempRep.initialize();
            RepositoryConnection tempRepCon  = tempRep.getConnection();
            URI tempContext = valFactory.createURI(superContextStr+ filePath.substring(filePath.lastIndexOf("/")));           

            try {
                URI superContext = valFactory.createURI(superContextStr);

                tempRepCon.add(new File(filePath), null, RDFFormat.RDFXML, tempContext);
                RepositoryResult<Statement> stmtsToRemove = tempRepCon.getStatements(null, null, null, true, tempContext);
                List<Statement> stmtsToRemoveList = Iterations.addAll(stmtsToRemove, new ArrayList<Statement>());
                con.remove(stmtsToRemoveList, superContext);
                tempRepCon.clear(tempContext);

            } catch (RepositoryException ex) {
                Logger.getLogger(UCIADRepositoryManager.class.getName()).log(Level.ERROR, null, ex);
            } finally {
                con.close();
                tempRepCon.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(UCIADRepositoryManager.class.getName()).log(Level.ERROR, null, ex);
        } catch (RDFParseException ex) {
            Logger.getLogger(UCIADRepositoryManager.class.getName()).log(Level.ERROR, null, ex);
        } catch (RepositoryException ex) {
            Logger.getLogger(UCIADRepositoryManager.class.getName()).log(Level.ERROR, null, ex);
        } 

    }
    
	public static TupleQueryResult evaluateSPARQLQuery(String query)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		Repository uciadRepository = new HTTPRepository(
				System.getProperty("repURI"), System.getProperty("repID"));
		uciadRepository.initialize();
		
		RepositoryConnection con = null;
		con = uciadRepository.getConnection();
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
				query);
		TupleQueryResult result = tupleQuery.evaluate();
		
		return result;
	}
}
