package com.github.dfauth.sslIgnore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public class SslUtil {

    private static final Logger logger = LoggerFactory.getLogger(SslUtil.class);

    public static final void useTrustStore(String protocol, KeyManager[] keyManagers, TrustManager[] trustManagers) {
        try {
            SSLContext ctx = SSLContext.getInstance(protocol);
            ctx.init(keyManagers, trustManagers, new SecureRandom());
            SSLContext.setDefault(ctx);
            System.out.println("set default context: "+ctx);
        } catch (NoSuchAlgorithmException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static KeyManager[] getDefaultKeyManagers() {
        try {
            KeyManagerFactory kf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kf.init((KeyStore)null, null);
            X509KeyManager defaultKm = null;
            for(KeyManager km : kf.getKeyManagers()) {
                if(kf instanceof X509KeyManager) {
                    defaultKm = (X509KeyManager) km;
                    break;
                }
            }
            return new KeyManager[]{defaultKm};
        } catch (NoSuchAlgorithmException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static X509TrustManager[] getCompositeTrustManager(X509TrustManager nested) {
        try {
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            // Using null here initialises the TMF with the default trust store.
            tmf.init((KeyStore) null);
            // Get hold of the default trust manager
            X509TrustManager defaultTm = null;
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    defaultTm = (X509TrustManager) tm;
                    break;
                }
            }

            // Get hold of the default trust manager
            X509TrustManager defaultTrustManager = null;
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    defaultTrustManager = (X509TrustManager) tm;
                    break;
                }
            }
            // Wrap it in your own class.
            final X509TrustManager finalDefaultTm = defaultTm;
            final X509TrustManager finalTrustManager = defaultTrustManager;
            X509TrustManager customTm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return finalDefaultTm.getAcceptedIssuers();
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                    try {
                        nested.checkServerTrusted(chain, authType);
                    } catch (CertificateException e) {
                        // This will throw another CertificateException if this fails too.
                        finalDefaultTm.checkServerTrusted(chain, authType);
                    }
                }
                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                    // If you're planning to use client-cert auth,
                    // do the same as checking the server.
                    finalDefaultTm.checkClientTrusted(chain, authType);
                }
            };
            return new X509TrustManager[]{customTm};
        } catch (NoSuchAlgorithmException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static TrustManager[] getCustomTrustManagers(InputStream stream, String password) {
        try {
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            // Using null here initialises the TMF with the default trust store.
            tmf.init((KeyStore) null);
            // Get hold of the default trust manager
            X509TrustManager defaultTm = null;

            // Do the same with your trust store this time
            // Adapt how you load the keystore to your needs
            KeyStore customTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            customTrustStore.load(stream, password.toCharArray());
            stream.close();
            tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(customTrustStore);
            // Get hold of the default trust manager
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    defaultTm = (X509TrustManager) tm;
                    break;
                }
            }
            // Wrap it in your own class.
            final X509TrustManager finalDefaultTm = defaultTm;
            X509TrustManager customTm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return finalDefaultTm.getAcceptedIssuers();
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                    finalDefaultTm.checkServerTrusted(chain, authType);
                }
                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                    // If you're planning to use client-cert auth,
                    // do the same as checking the server.
                    finalDefaultTm.checkClientTrusted(chain, authType);
                }
            };
            return new X509TrustManager[]{customTm};
        } catch (NoSuchAlgorithmException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static X509TrustManager[] getDefaultTrustManagers() {
        try {
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            // Using null here initialises the TMF with the default trust store.
            tmf.init((KeyStore) null);
            X509TrustManager defaultTm = null;
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    defaultTm = (X509TrustManager) tm;
                    break;
                }
            }

            final X509TrustManager finalDefaultTm = defaultTm;
            X509TrustManager customTm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return finalDefaultTm.getAcceptedIssuers();
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                    finalDefaultTm.checkServerTrusted(chain, authType);
                }
                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                    // If you're planning to use client-cert auth,
                    // do the same as checking the server.
                    finalDefaultTm.checkClientTrusted(chain, authType);
                }
            };
            return new X509TrustManager[]{customTm};
        } catch (NoSuchAlgorithmException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            logger.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static TrustManager[] getApproveAllTrustManagers() {
        return new X509TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s){
                logger.info("checkClientTrusted: "+x509Certificates+" s: "+s);
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                logger.info("checkServerTrusted: "+x509Certificates+" s: "+s);
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
    }

    public static KeyManager[] getEmptyKeyManagers() {
        return new KeyManager[]{};
    }
}
