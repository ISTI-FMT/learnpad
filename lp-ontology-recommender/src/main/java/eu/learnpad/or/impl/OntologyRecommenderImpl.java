/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.learnpad.or.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eu.learnpad.core.impl.or.XwikiBridge;
import eu.learnpad.core.impl.or.XwikiCoreFacadeRestResource;
import eu.learnpad.exception.LpRestException;
import eu.learnpad.ontology.execution.ExecutionStates;
import eu.learnpad.ontology.recommender.Recommender;
import eu.learnpad.or.rest.data.BusinessActor;
import eu.learnpad.or.rest.data.Experts;
import eu.learnpad.or.rest.data.LearningMaterial;
import eu.learnpad.or.rest.data.LearningMaterials;
import eu.learnpad.or.rest.data.States;
import eu.learnpad.ontology.transformation.ModellingEnvironmentType;
import eu.learnpad.ontology.transformation.SimpleModelTransformator;
import eu.learnpad.or.rest.data.Recommendations;
import eu.learnpad.or.rest.data.SimilarCase;
import eu.learnpad.or.rest.data.SimilarCases;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.Path;

import org.jgroups.util.UUID;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

/**
 *
 * @author sandro.emmenegger
 */

@Component
@Singleton
@Named("eu.learnpad.or.impl.OntologyRecommenderImpl")
@Path("/learnpad/or/bridge")
public class OntologyRecommenderImpl extends XwikiBridge implements Initializable {
    
    @Override
    public void initialize() throws InitializationException {
            this.corefacade = new XwikiCoreFacadeRestResource();
            SimpleModelTransformator.getInstance();
    }

    @Override
    public void modelSetImported(String modelSetId, String type) throws LpRestException {
            SimpleModelTransformator.getInstance().transform(modelSetId, this.corefacade.getModel(modelSetId, type), ModellingEnvironmentType.valueOf(type.toUpperCase()));
    }
    
    @Override
    public void sendResourceNotification(String modelSetId, String resourceId, String artifactIds, String action) throws LpRestException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Recommendations askRecommendation(String modelSetId, String artifactId, String userId, String type) throws LpRestException {
        Recommendations recomms = Recommender.getInstance().getRecommendations(modelSetId, artifactId, userId);
        return recomms;
    }

    @Override
    public byte[] simulationNotification(String modelSetId, String modelId, String action, String simulationId) throws LpRestException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addExecutionState(String modelSetId, String executionId, String userId, String threadId, String pageId, String artifactId) throws LpRestException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public States listExecutionStates(String userId) throws LpRestException {
        States states = ExecutionStates.getInstance().getStatesOfLatestAddedModelSet(userId);
        return states;
    }

    @Override
    public SimilarCases retrieveSimilarCasesForSimulation(String simulationSessionId) throws LpRestException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimilarCases retrieveSimilarCases(String modelSetId, String artifactId, String userId, String applicantName,
        String applicationCity, String applicationZone, String applicationType, String applicationPublicAdministration,
        String applicationSector, String applicationBusinessActivity, String applicationDescription,
        String applicationATECOCategory) throws LpRestException
    {
        SimilarCases scs = new SimilarCases();
        List<SimilarCase> listSC = new ArrayList<SimilarCase>();
        SimilarCase sc1 = new SimilarCase();
        sc1.setId(UUID.randomUUID().toString());
        sc1.setName("Name of the case");
        sc1.setApplicantName(applicantName);
        sc1.setApplicationCity(applicationCity);
        sc1.setApplicationZones("Industrial Area,Urban Area".split(","));
        sc1.setApplicationType(applicationType);
        sc1.setApplicationPublicAdministration(applicationPublicAdministration);
        sc1.setApplicationSectors("Tourism,Waste".split(","));
        sc1.setApplicationBusinessActivities("Commercial,Farming".split(","));
        sc1.setApplicationDescription(applicationDescription);
        sc1.setApplicationATECOCategories("Dining,Hotels".split(","));
        sc1.setApplicationDecision("Accepted");
        sc1.setApplicationSubtype("This is a subtype");
        sc1.setSimilarityValue(0.5);
        Experts experts = new Experts();
        List<BusinessActor> businessActors = new ArrayList<BusinessActor>();
        BusinessActor businessActor1 = new BusinessActor();
        BusinessActor businessActor2 = new BusinessActor();
        businessActor1.setName("Jean");
        businessActor1.setEmail("jean@localhost.org");
        businessActor1.setPhoneNumber("+33123456789");
        businessActors.add(businessActor1);
        businessActor2.setName("Sandro");
        businessActor2.setEmail("sandro@localhost.org");
        businessActors.add(businessActor2);
        experts.setBusinessActors(businessActors);
        sc1.setExperts(experts);
        LearningMaterials lms = new LearningMaterials();
        List<LearningMaterial> listLM = new ArrayList<LearningMaterial>();
        LearningMaterial lm1 = new LearningMaterial();
        lm1.setId(UUID.randomUUID().toString());
        lm1.setName("Some learning material");
        lm1.setMimeType("application/xml");
        lm1.setUrl("http://testbed.learnpad.eu/xwiki/bin/download/Main/WebHome/file.xml");
        lm1.setDescription("Here is a description of the learning material");
        lm1.setComment("No comment");
        lm1.setQueryDescription("Some kind of query");
        listLM.add(lm1);
        sc1.setLearningMaterials(lms);
        listSC.add(sc1);
        scs.setSimilarCases(listSC);
        return scs;
    }

}
