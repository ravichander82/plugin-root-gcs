package com.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.netflix.spinnaker.clouddriver.google.config.GoogleConfigurationProperties;
import com.netflix.spinnaker.credentials.definition.CredentialsDefinitionSource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GoogleCredentialsDefinitionSource implements CredentialsDefinitionSource<GoogleConfigurationProperties.ManagedAccount> {

    private static final GcsSource gcsSource = new GcsSource() ;


    @Override
    public List<GoogleConfigurationProperties.ManagedAccount> getCredentialsDefinitions() {

        List<GoogleConfigurationProperties.ManagedAccount> googleCredentialsDefinitions =
                new ArrayList<>();

        InputStream bucket = gcsSource.downloadRemoteFile("waze_gcs", "sample.yml");

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(bucket);

        HashMap map = (HashMap) data.get("google");
        Boolean isEnabled = (Boolean) map.get("enabled");
        ArrayList accountsList = (ArrayList) map.get("accounts");

        ObjectMapper mapper = new ObjectMapper();

        for( int i =0 ;i<accountsList.size();i++) {
            GoogleConfigurationProperties.ManagedAccount managedAccount = mapper.convertValue(accountsList.get(i),GoogleConfigurationProperties.ManagedAccount.class);
            googleCredentialsDefinitions.add(managedAccount);
        }

        return ImmutableList.copyOf(googleCredentialsDefinitions);
    }
}
