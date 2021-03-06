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
package eu.learnpad.core.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReferenceSerializer;

import com.xpn.xwiki.web.Utils;

import net.java.truevfs.access.TPath;

@Component
@Singleton
public class XWikiRestUtils
{

    @Inject
    private static Logger logger;

    public static boolean putPage(String wikiName, String spaceName, String pageName, InputStream pageXML)
    {
        HttpClient httpClient = RestResource.getClient();

        String uri =
            String.format("%s/wikis/%s/spaces/%s/pages/%s", RestResource.REST_URI, wikiName, spaceName, pageName);
        PutMethod putMethod = new PutMethod(uri);
        putMethod.addRequestHeader("Accept", "application/xml");
        putMethod.addRequestHeader("Accept-Ranges", "bytes");

        RequestEntity pageRequestEntity = new InputStreamRequestEntity(pageXML, "application/xml");
        putMethod.setRequestEntity(pageRequestEntity);
        try {
            httpClient.executeMethod(putMethod);
            return true;
        } catch (HttpException e) {
            String message =
                String.format("Unable to process the PUT request on page '%s:%s.%s'.", wikiName, spaceName, pageName);
            logger.error(message, e);
            return false;
        } catch (IOException e) {
            String message = String.format("Unable to PUT the page '%s:%s.%s'.", wikiName, spaceName, pageName);
            logger.error(message, e);
            return false;
        }
    }

    public static boolean isPage(String wikiName, String spaceName, String pageName)
    {
        HttpClient httpClient = RestResource.getClient();

        String uri =
            String.format("%s/wikis/%s/spaces/%s/pages/%s", RestResource.REST_URI, wikiName, spaceName, pageName);
        GetMethod getMethod = new GetMethod(uri);
        getMethod.addRequestHeader("Accept", "application/xml");
        getMethod.addRequestHeader("Accept-Ranges", "bytes");
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode == 200) {
                return true;
            }
        } catch (HttpException e) {
            String message =
                String.format("Unable to process the GET request on page '%s:%s.%s'.", wikiName, spaceName, pageName);
            logger.error(message, e);
            return false;
        } catch (IOException e) {
            String message = String.format("Unable to GET the page '%s:%s.%s'.", wikiName, spaceName, pageName);
            logger.error(message, e);
            return false;
        }
        return false;
    }

    public static boolean createEmptyPage(String wikiName, String spaceName, String pageName)
    {
        String emptyPageXML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><page xmlns=\"http://www.xwiki.org\"><content/></page>";
        return putPage(wikiName, spaceName, pageName, IOUtils.toInputStream(emptyPageXML));
    }

    public static InputStream getAttachmentFromCoreRepository(String basename, String extension)
    {
        String attachmentName = String.format("%s.%s", basename, extension);
        return XWikiRestUtils.getAttachment(RestResource.CORE_REPOSITORY_WIKI, RestResource.CORE_REPOSITORY_SPACE,
            basename, attachmentName);
    }

    public static Collection<String> exposeBPMNFromCoreRepository(String basename, String extension)
    {
        Collection<String> uriCollection = new ArrayList<String>();

        String pageName = basename;
        String attachmentName = String.format("%s.%s", basename, extension);

        // TODO : implement here the extraction and the publication of the BPMN URL
        String uri = String.format("%s/wikis/%s/spaces/%s/pages/%s", RestResource.REST_URI,
            RestResource.CORE_REPOSITORY_WIKI, RestResource.CORE_REPOSITORY_SPACE, pageName);
        uriCollection.add(uri);

        return uriCollection;
    }

    public static InputStream getAttachment(String wikiName, String spaceName, String pageName, String attachmentName)
    {
        HttpClient httpClient = RestResource.getClient();

        String uri = String.format("%s/wikis/%s/spaces/%s/pages/%s/attachments/%s", RestResource.REST_URI, wikiName,
            spaceName, pageName, attachmentName);
        GetMethod getMethod = new GetMethod(uri);
        getMethod.addRequestHeader("Accept", "application/xml");
        getMethod.addRequestHeader("Accept-Ranges", "bytes");

        try {
            httpClient.executeMethod(getMethod);
            return getMethod.getResponseBodyAsStream();
        } catch (HttpException e) {
            String message = String.format("Unable to process GET request for the attachment '%s:%s.%s@%s'.", wikiName,
                spaceName, pageName, attachmentName);
            logger.error(message, e);
            return null;
        } catch (IOException e) {
            String message = String.format("Unable to GET the attachment '%s:%s.%s@%s'.", wikiName, spaceName, pageName,
                attachmentName);
            logger.error(message, e);
            return null;
        }
    }

    public static InputStream getFileInAttachment(String wikiName, String spaceName, String pageName,
        String attachmentName, Path filePath)
    {
        EntityReferenceSerializer<String> referenceSerializer =
            Utils.getComponent(EntityReferenceSerializer.TYPE_STRING);
        DocumentReference modelsetReference = new DocumentReference(wikiName, Arrays.asList(spaceName), pageName);
        AttachmentReference attachmentReference = new AttachmentReference(attachmentName, modelsetReference);
        // TODO: Adapt the name dynamically for Adoxx or MagicDraw
        URI uri = null;
        try {
            uri = new URI(
                String.format("attach://%s/%s/%s", referenceSerializer.serialize(attachmentReference.getParent()),
                    attachmentReference.getName(), filePath.toString()));
        } catch (URISyntaxException e) {
            String message = String.format("Unable to get URI for the file '%s' in archive '%s'", filePath.toString(),
                attachmentReference);
            logger.error(message, e);
            return null;
        }
        java.nio.file.Path tPath = new TPath(uri);
        InputStream stream = null;
        try {
            stream = Files.newInputStream(tPath);
        } catch (IOException e) {
            String message = String.format("Unable to get input stream of the file '%s' in archive '%s'",
                filePath.toString(), attachmentReference);
            logger.error(message, e);
            return null;
        }
        return stream;
    }

    public static boolean putAttachment(String wikiName, String spaceName, String pageName, String attachmentName,
        InputStream attachment)
    {
        HttpClient httpClient = RestResource.getClient();

        String uri = String.format("%s/wikis/%s/spaces/%s/pages/%s/attachments/%s", RestResource.REST_URI, wikiName,
            spaceName, pageName, attachmentName);
        PutMethod putMethod = new PutMethod(uri);
        putMethod.addRequestHeader("Accept", "application/xml");
        putMethod.addRequestHeader("Accept-Ranges", "bytes");
        RequestEntity fileRequestEntity = new InputStreamRequestEntity(attachment, "application/xml");
        putMethod.setRequestEntity(fileRequestEntity);
        try {
            httpClient.executeMethod(putMethod);
            return true;
        } catch (HttpException e) {
            String message = String.format("Unable to process PUT request for the attachment '%s:%s.%s@%s'.", wikiName,
                spaceName, pageName, attachmentName);
            logger.error(message, e);
            return false;
        } catch (IOException e) {
            String message = String.format("Unable to PUT the attachment '%s:%s.%s@%s'.", wikiName, spaceName, pageName,
                attachmentName);
            logger.error(message, e);
            return false;
        }
    }
}
