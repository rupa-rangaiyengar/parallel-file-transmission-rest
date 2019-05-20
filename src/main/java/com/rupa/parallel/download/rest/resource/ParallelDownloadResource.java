package com.rupa.parallel.download.rest.resource;

import com.rupa.parallel.download.rest.utils.*;

import org.apache.log4j.Logger;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.lang.annotation.Annotation;
import java.net.*;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.rupa.parallel.download.rest.service.*;

@Path("/manager")
public class ParallelDownloadResource {

    final static org.apache.log4j.Logger logger = Logger.getLogger(ParallelDownloadResource.class);


    @Path("startdownload")
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getAllFiles(@QueryParam("fileName") String fileName,
                                @QueryParam("outputFolder") String clientFolderPath,
                                @DefaultValue ("9") @QueryParam("degreeParallelism") int degreeParallelism ) {
        boolean validFileName =  Utils.checkUrl(fileName);

        if (clientFolderPath == null || clientFolderPath.isEmpty() || clientFolderPath==""){
            clientFolderPath = System.getProperty("user.home");
        }
        if (validFileName){
            try {
                logger.info("File download API called through the REST interface started");
                AbstractDownloader download = new ParallelDownloader(URI.create(fileName).toURL(),
                        clientFolderPath + "/", degreeParallelism);
                return Response
                        .status(Response.Status.OK)
                        .entity(" File download has been completed. To see the output look at the output " +
                                "folder if specified or your home directory")
                        .build();

            } catch(MalformedURLException exception){
                logger.error("Exception in REST API due to: "+exception.getMessage());
                return Response
                        .status(Response.Status.NO_CONTENT).build();
            }
        }
        else{
             return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("File name for download is invalid")
                    .build();
        }
    }
}
