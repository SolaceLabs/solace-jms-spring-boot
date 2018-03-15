package com.solace.spring.boot.autoconfigure;

import com.solace.services.loader.model.SolaceServiceCredentials;
import com.solace.spring.cloud.core.SolaceMessagingInfo;
import com.solacesystems.jms.SpringSolJmsJndiTemplateCloudFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jndi.JndiTemplate;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.List;
import java.util.Properties;

abstract class SolaceJndiAutoConfigurationBase implements SpringSolJmsJndiTemplateCloudFactory {

    private static final Logger logger = LoggerFactory.getLogger(SolaceJndiAutoConfigurationBase.class);
    private SolaceJmsProperties properties;

    SolaceJndiAutoConfigurationBase(SolaceJmsProperties properties) {
        this.properties = properties;
    }

    abstract SolaceServiceCredentials findFirstSolaceServiceCredentialsImpl();
    abstract List<SolaceServiceCredentials> getSolaceServiceCredentialsImpl();

    @Bean
    @Override
    public SolaceServiceCredentials findFirstSolaceServiceCredentials() {
        return findFirstSolaceServiceCredentialsImpl();
    }

    @Bean
    @Override
    public List<SolaceServiceCredentials> getSolaceServiceCredentials() {
        return getSolaceServiceCredentialsImpl();
    }

    @Bean
    @Override
    public JndiTemplate getJndiTemplate() {
        return getJndiTemplate(findFirstSolaceServiceCredentialsImpl());
    }

    @Override
    public JndiTemplate getJndiTemplate(String id) {
        return getJndiTemplate(findSolaceServiceCredentialsById(id));
    }

    @Override
    public JndiTemplate getJndiTemplate(SolaceServiceCredentials solaceServiceCredentials) {
        try {
            Properties env = new Properties();
            env.putAll(properties.getApiProperties());
            env.put(InitialContext.INITIAL_CONTEXT_FACTORY, "com.solacesystems.jndi.SolJNDIInitialContextFactory");

            env.put(InitialContext.PROVIDER_URL, solaceServiceCredentials.getJmsJndiUri() != null ?
                    solaceServiceCredentials.getJmsJndiUri() : properties.getHost());
            env.put(Context.SECURITY_PRINCIPAL,
                    solaceServiceCredentials.getClientUsername() != null && solaceServiceCredentials.getMsgVpnName() != null ?
                            solaceServiceCredentials.getClientUsername() + '@' + solaceServiceCredentials.getMsgVpnName() :
                            properties.getClientUsername() + '@' + properties.getMsgVpn());

            env.put(Context.SECURITY_CREDENTIALS, solaceServiceCredentials.getClientPassword() != null ?
                    solaceServiceCredentials.getClientPassword() :
                    properties.getClientPassword());

            JndiTemplate jndiTemplate = new JndiTemplate();
            jndiTemplate.setEnvironment(env);
            return jndiTemplate;
        } catch (Exception ex) {
            logger.error("Exception found during Solace JNDI Initial Context creation.", ex);
            throw new IllegalStateException("Unable to create Solace "
                    + "JNDI Initial Context, ensure that the sol-jms-<version>.jar " + "is the classpath", ex);
        }
    }

    @Override @Deprecated
    public List<SolaceMessagingInfo> getSolaceMessagingInfos() {
        return null;
    }

    private SolaceServiceCredentials findSolaceServiceCredentialsById(String id) {
        for (SolaceServiceCredentials credentials : getSolaceServiceCredentialsImpl())
            if (credentials.getId().equals(id)) return credentials;
        return null;
    }
}
