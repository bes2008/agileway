package com.jn.agileway.ssh.client.plugins.utils;


import com.jn.agileway.ssh.client.SshException;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import javax.crypto.KeyAgreement;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

import static java.lang.String.format;

/**
 * Static utility method relating to security facilities.
 */
public class SecurityUtils {
    private static final Logger LOG = Loggers.getLogger(SecurityUtils.class);

    /**
     * Identifier for the BouncyCastle JCE provider
     */
    public static final String BOUNCY_CASTLE = "BC";

    /**
     * Identifier for the BouncyCastle JCE provider
     */
    public static final String SPONGY_CASTLE = "SC";

    /*
     * Security provider identifier. null = default JCE
     */
    private static String securityProvider = null;

    // relate to BC registration (or SpongyCastle on Android)
    private static Boolean registerBouncyCastle;
    private static boolean registrationDone;

    public static boolean registerSecurityProvider(String providerClassName) {
        Provider provider = null;
        try {
            Class<?> name = Class.forName(providerClassName);
            provider = (Provider) name.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            LOG.info("Security Provider class '{}' not found", providerClassName);
        } catch (InstantiationException e) {
            LOG.info("Security Provider class '{}' could not be created", providerClassName);
        } catch (IllegalAccessException e) {
            LOG.info("Security Provider class '{}' could not be accessed", providerClassName);
        } catch (InvocationTargetException e) {
            LOG.info("Security Provider class '{}' could not be created", providerClassName);
        } catch (NoSuchMethodException e) {
            LOG.info("Security Provider class '{}' does not have a no-args constructor", providerClassName);
        }

        if (provider == null) {
            return false;
        }

        try {
            if (Security.getProvider(provider.getName()) == null) {
                Security.addProvider(provider);
            }

            if (securityProvider == null) {
                MessageDigest.getInstance("MD5", provider);
                KeyAgreement.getInstance("DH", provider);
                setSecurityProvider(provider.getName());
                return true;
            }
        } catch (NoSuchAlgorithmException e) {
            LOG.info(format("Security Provider '%s' does not support necessary algorithm", providerClassName), e);
        } catch (Exception e) {
            LOG.info(format("Registration of Security Provider '%s' unexpectedly failed", providerClassName), e);
        }
        return false;
    }


    /**
     * Attempts registering BouncyCastle as security provider if it has not been previously attempted and returns
     * whether the registration succeeded.
     *
     * @return whether BC (or SC on Android) registered
     */
    public static synchronized boolean isBouncyCastleRegistered() {
        register();
        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            String name = provider.getName();
            if (BOUNCY_CASTLE.equals(name) || SPONGY_CASTLE.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static synchronized void setRegisterBouncyCastle(boolean registerBouncyCastle) {
        SecurityUtils.registerBouncyCastle = registerBouncyCastle;
        registrationDone = false;
    }

    /**
     * Specifies the JCE security provider that should be used.
     *
     * @param securityProvider identifier for the security provider
     */
    public static synchronized void setSecurityProvider(String securityProvider) {
        SecurityUtils.securityProvider = securityProvider;
        registrationDone = false;
    }

    private static void register() {
        if (!registrationDone) {
            if (securityProvider == null && (registerBouncyCastle == null || registerBouncyCastle)) {
                registerSecurityProvider("org.bouncycastle.jce.provider.BouncyCastleProvider");
                if (securityProvider == null && registerBouncyCastle == null) {
                    LOG.info("BouncyCastle not registered, using the default JCE provider");
                } else if (securityProvider == null) {
                    LOG.error("Failed to register BouncyCastle as the defaut JCE provider");
                    throw new SshException("Failed to register BouncyCastle as the defaut JCE provider");
                }
            }
            registrationDone = true;
        }
    }
}
