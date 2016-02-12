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
package eu.learnpad.cw;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.xwiki.component.annotation.Role;

import eu.learnpad.exception.LpRestException;
import eu.learnpad.or.rest.data.Recommendations;
import eu.learnpad.or.rest.data.SimilarCases;

@Role
public interface UICWBridge {
	Recommendations getRecommendations(String modelSetId, String artifactId,
			String userId) throws LpRestException;

    SimilarCases retrieveSimilarCases(@QueryParam("modelsetId") String modelSetId,
        @QueryParam("artifactid") String artifactId, @QueryParam("userid") String userId,
        @QueryParam("applicantName") String applicantName, @QueryParam("applicationCity") String applicationCity,
        @QueryParam("applicationZone") String applicationZone, @QueryParam("applicationType") String applicationType,
        @QueryParam("applicationPublicAdministration") String applicationPublicAdministration,
        @QueryParam("applicationSector") String applicationSector,
        @QueryParam("applicationBusinessActivity") String applicationBusinessActivity,
        @QueryParam("applicationDescription") String applicationDescription,
        @QueryParam("applicationATECOCategory") String applicationATECOCategory) throws LpRestException;
    
	String startSimulation(@PathParam("modelid") String modelId,
			@QueryParam("currentuser") String currentUser,
			Collection<String> potentialUsers) throws LpRestException;
}
