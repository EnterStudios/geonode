package org.geonode.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import junit.framework.Test;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.geoserver.data.test.MockData;
import org.geoserver.test.GeoServerTestSupport;
import org.geotools.TestData;

import com.mockrunner.mock.web.MockHttpServletResponse;

public class ProcessRestletTest extends GeoServerTestSupport {

    private static final String RESTLET_PATH = "/rest/process/hazard";

    static {
        ProcessRestlet.LOGGER.setLevel(Level.FINER);
    }

    /**
     * This is a READ ONLY TEST so we can use one time setup
     */
    public static Test suite() {
        return new OneTimeTestSetup(new ProcessRestletTest());
    }

    @Override
    protected void populateDataDirectory(MockData dataDirectory) throws Exception {
        super.populateDataDirectory(dataDirectory);
        dataDirectory.addWellKnownCoverageTypes();
    }

    public void testSuccessCode() throws Exception {
        String jsonRequest = loadTestData("sample-request.json");

        MockHttpServletResponse r = postAsServletResponse(RESTLET_PATH, jsonRequest);
        assertEquals(200, r.getStatusCode());
    }

    public void testService() throws Exception {
        String jsonRequest = loadTestData("sample-request.json");

        final String resultStr;
        {
            final InputStream in = post(RESTLET_PATH, jsonRequest);
            resultStr = IOUtils.toString(in, "UTF-8");
        }
        System.out.println(resultStr);
        
        JSONObject result = JSONObject.fromObject(resultStr);
        assertTrue(result.get("statistics") instanceof JSONObject);
        
        JSONObject statistics = (JSONObject) result.get("statistics");
        assertEquals(1, statistics.size());
        
        assertTrue(statistics.get("wcs:DEM") instanceof JSONObject);
        JSONObject demStats = (JSONObject) ((JSONObject) result.get("statistics")).get("wcs:DEM");
        
        assertTrue(demStats.get("min") instanceof JSONArray);
        assertTrue(demStats.get("max") instanceof JSONArray);
        assertTrue(demStats.get("mean") instanceof JSONArray);
        assertTrue(demStats.get("stddev") instanceof JSONArray);
    }

    private String loadTestData(final String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(TestData.openStream(this,
                fileName)));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

}
