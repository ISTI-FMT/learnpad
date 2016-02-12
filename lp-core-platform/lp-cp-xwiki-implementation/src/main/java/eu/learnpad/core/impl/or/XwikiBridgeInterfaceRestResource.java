/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package eu.learnpad.core.impl.or;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;

import eu.learnpad.core.rest.RestResource;
import eu.learnpad.exception.LpRestException;
import eu.learnpad.exception.impl.LpRestExceptionImpl;
import eu.learnpad.exception.impl.LpRestExceptionXWikiImpl;
import eu.learnpad.or.BridgeInterface;
import eu.learnpad.or.rest.data.Recommendations;
import eu.learnpad.or.rest.data.SimilarCases;
import eu.learnpad.or.rest.data.States;

/*
 * The methods inherited form the BridgeInterface in this
 * class should be implemented as a REST invocation
 * toward the BridgeInterface binded at the provided URL
 */

public class XwikiBridgeInterfaceRestResource extends RestResource implements BridgeInterface
{

    public XwikiBridgeInterfaceRestResource()
    {
        this("localhost", 8080);
    }

    public XwikiBridgeInterfaceRestResource(String coreFacadeHostname, int coreFacadeHostPort)
    {
        // This constructor could change in the future
        this.updateConfiguration(coreFacadeHostname, coreFacadeHostPort);
    }

    public void updateConfiguration(String coreFacadeHostname, int coreFacadeHostPort)
    {
        // This constructor has to be fixed, since it requires changes on the class
        // eu.learnpad.core.rest.RestResource

    }

    @Override
    public void sendResourceNotification(String modelSetId, String resourceId, String artifactIds, String action)
        throws LpRestExceptionImpl
    {
        // TODO Auto-generated method stub

    }

    @Override
    public Recommendations askRecommendation(String modelSetId, String artifactId, String userId, String type)
        throws LpRestExceptionImpl
    {
        HttpClient httpClient = RestResource.getClient();
        String uri = String.format("%s/learnpad/or/bridge/%s/recommendation", RestResource.REST_URI, modelSetId);

        GetMethod getMethod = new GetMethod(uri);
        getMethod.addRequestHeader("Accept", "application/xml");

        NameValuePair[] queryString = new NameValuePair[3];
        queryString[0] = new NameValuePair("artifactid", artifactId);
        queryString[1] = new NameValuePair("userid", userId);
        queryString[2] = new NameValuePair("type", type);
        getMethod.setQueryString(queryString);

        try {
            httpClient.executeMethod(getMethod);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InputStream expertsStream = null;
        try {
            expertsStream = getMethod.getResponseBodyAsStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Recommendations recommendations = null;
        if (expertsStream != null) {
            try {
                JAXBContext jc = JAXBContext.newInstance(Recommendations.class);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                recommendations = (Recommendations) unmarshaller.unmarshal(expertsStream);
            } catch (JAXBException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return recommendations;
    }

    @Override
    public byte[] simulationNotification(String modelSetId, String modelId, String action, String simulationId)
        throws LpRestExceptionImpl
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addExecutionState(String modelSetId, String executionId, String userId, String threadId, String pageId,
        String artifactId) throws LpRestException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public States listExecutionStates(String userId) throws LpRestExceptionImpl
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void modelSetImported(String modelSetId, String type) throws LpRestExceptionXWikiImpl
    {
        HttpClient httpClient = RestResource.getClient();
        String uri = String.format("%s/learnpad/or/bridge/modelsetimported/%s", RestResource.REST_URI, modelSetId);
        PutMethod putMethod = new PutMethod(uri);
        putMethod.addRequestHeader("Accept", "application/xml");
        NameValuePair[] queryString = new NameValuePair[1];
        queryString[0] = new NameValuePair("type", type);
        putMethod.setQueryString(queryString);
        try {
            httpClient.executeMethod(putMethod);
        } catch (IOException e) {
            e.printStackTrace();
            throw new LpRestExceptionXWikiImpl(e.getMessage(), e);
        }

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
        HttpClient httpClient = RestResource.getClient();
        String uri = String.format("%s/learnpad/or/bridge/similarcases", RestResource.REST_URI);

        GetMethod getMethod = new GetMethod(uri);
        getMethod.addRequestHeader("Accept", "application/xml");

        NameValuePair[] queryString = new NameValuePair[12];
        queryString[0]  = new NameValuePair("modelSetId", modelSetId);
        queryString[1]  = new NameValuePair("artifactid", artifactId);
        queryString[2]  = new NameValuePair("userId", userId);
        queryString[3]  = new NameValuePair("applicantName", applicantName);
        queryString[4]  = new NameValuePair("applicationCity", applicationCity);
        queryString[5]  = new NameValuePair("applicationZone", applicationZone);
        queryString[6]  = new NameValuePair("applicationType", applicationType);
        queryString[7]  = new NameValuePair("applicationPublicAdministration", applicationPublicAdministration);
        queryString[8]  = new NameValuePair("applicationSector", applicationSector);
        queryString[9]  = new NameValuePair("applicationBusinessActivity", applicationBusinessActivity);
        queryString[10] = new NameValuePair("applicationDescription", applicationDescription);
        queryString[11] = new NameValuePair("applicationATECOCategory", applicationATECOCategory);
        getMethod.setQueryString(queryString);

        try {
            httpClient.executeMethod(getMethod);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InputStream similarCasesStream = null;
        try {
            similarCasesStream = getMethod.getResponseBodyAsStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimilarCases similarCases = null;
        if (similarCasesStream != null) {
            try {
                JAXBContext jc = JAXBContext.newInstance(SimilarCases.class);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                similarCases = (SimilarCases) unmarshaller.unmarshal(similarCasesStream);
            } catch (JAXBException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return similarCases;
    }
}
