package org.jenkinsci.plugins.puppetenterprise.api;

import java.io.*;
import java.util.*;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import hudson.model.RootAction;
import hudson.Extension;
import org.json.*;
import javax.servlet.ServletException;

import org.jenkinsci.plugins.puppetenterprise.models.HieraConfig;

@Extension
public class HieraDataStore implements RootAction {
  private static final String ICON_PATH = "/plugin/workflow-puppet-enterprise/images/cfg_logo.png";

  private HieraConfig hiera = null;

  public HieraDataStore() {
    hiera = new HieraConfig();
  }

  public String[] getPaths() {
    Set<String> pathSet = hiera.getPaths();
    return pathSet.toArray(new String[pathSet.size()]);
  }

  public String[] getKeys(String path) {
    Set<String> keySet = hiera.getKeys(path);
    return keySet.toArray(new String[keySet.size()]);
  }

  public String getKeyValue(String path, String key) {
    Object value = hiera.getKeyValue(path, key);
    return value.toString();
  }

  @Override
  public String getUrlName() {
    return "hiera";
  }

  @Override
  public String getDisplayName() {
    return "Hiera Data Lookup";
  }

  @Override
  public String getIconFileName() {
    return ICON_PATH;
  }

  public void doLookup(StaplerRequest req, StaplerResponse rsp) throws IOException {
    net.sf.json.JSONObject form = null;
    Map parameters = null;

    parameters = req.getParameterMap();

    String returnValue = "";
    String pathArr[] = (String[]) parameters.get("path");
    String path = pathArr[0];
    String keyArr[] = (String[]) parameters.get("key");
    String key = keyArr[0];

    Object value = hiera.getKeyValue(path, key);

    if (value == null) {
      rsp.setStatus(404);
      return;
    }

    rsp.setContentType("application/json;charset=UTF-8");
    rsp.getOutputStream().print(serializeResult(key, value));
  }

  private String serializeResult(String key, Object result) {
    HashMap hash = new HashMap();

    if (result instanceof String) {
      String valueString = (String) result;
      hash.put(key, valueString);
    } else if (result instanceof ArrayList) {
      ArrayList valueArray = (ArrayList) result;
      hash.put(key, valueArray);
    } else if (result instanceof HashMap) {
      LinkedHashMap valueHash = (LinkedHashMap) result;
      hash.put(key, valueHash);
    }

    return new JSONObject(hash).toString();
  }
}
