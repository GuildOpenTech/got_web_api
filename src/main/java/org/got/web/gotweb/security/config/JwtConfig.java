package org.got.web.gotweb.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Configuration pour la gestion des clés JWT
 * Utilise des clés RSA asymétriques stockées dans un keystore PKCS12
 */
@Configuration
@ConfigurationProperties(prefix = "spring.security.jwt")
@Getter
@Setter
public class JwtConfig {

    private KeystoreProperties keystore = new KeystoreProperties();
    private long expiration = 86400000L; // 24 heures
    private RefreshTokenProperties refreshToken = new RefreshTokenProperties();
    private String issuer = "got-web";

    @Getter
    @Setter
    public static class KeystoreProperties {
        private String location;
        private String password;
        private String alias;
    }

    @Getter
    @Setter
    public static class RefreshTokenProperties {
        private long expiration = 604800000L; // 7 jours
    }

    private final ResourceLoader resourceLoader;

    public JwtConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public KeyStore keyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream resourceStream = resourceLoader.getResource(keystore.getLocation())
                    .getInputStream();
            keyStore.load(resourceStream, keystore.getPassword().toCharArray());
            return keyStore;
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new IllegalStateException("Impossible de charger le keystore JWT", e);
        }
    }

    @Bean
    public RSAPrivateKey jwtSigningKey(KeyStore keyStore) {
        try {
            Key key = keyStore.getKey(keystore.getAlias(), keystore.getPassword().toCharArray());
            if (key instanceof RSAPrivateKey) {
                return (RSAPrivateKey) key;
            }
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new IllegalStateException("Impossible de charger la clé privée RSA", e);
        }
        throw new IllegalStateException("Le keystore ne contient pas de clé privée RSA valide");
    }

    @Bean
    public RSAPublicKey jwtValidationKey(KeyStore keyStore) {
        try {
            java.security.cert.Certificate certificate = keyStore.getCertificate(keystore.getAlias());
            if (certificate == null) {
                throw new IllegalStateException("Le certificat n'existe pas dans le keystore");
            }
            PublicKey publicKey = certificate.getPublicKey();
            if (publicKey instanceof RSAPublicKey) {
                return (RSAPublicKey) publicKey;
            }
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Impossible de charger la clé publique RSA", e);
        }
        throw new IllegalStateException("Le keystore ne contient pas de clé publique RSA valide");
    }

    @Bean
    public KeyPair jwtKeyPair(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        return new KeyPair(publicKey, privateKey);
    }
}
