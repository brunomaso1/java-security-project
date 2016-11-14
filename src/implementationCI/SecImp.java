/*
 * Universidad Catolica - Seguridad - Obligatorio.
 */
package implementationCI;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.*;

/**
 * Esta clase es la encargada de interactuar con la Cedula de Identidad.
 * @author Masoller, Artegoytia, Galleto, Olivera.
 */
public class SecImp {
    private String configName;
    private Provider provider;
    private X509Certificate certificado;

    public SecImp(String configName) { this.setConfigName(configName); }

    public SecImp() {
        this.setConfigName(".\\conf\\pkcs11.cfg");
        this.setProvider(new sun.security.pkcs11.SunPKCS11(getConfigName()));
        Security.addProvider(this.getProvider());

        // Log
        System.out.println("Se ha inicializado el provider correctamente.");
    }

    public void remProvider() { 
        Security.removeProvider(getProvider().getName());

        // Log
        System.out.println("Se ha removido el provider correctamente.");
    }

    public void conCIsinPass() {
        String pin = "";
        boolean certFlag = false;
        try {            
            KeyStore cc = KeyStore.getInstance("PKCS11", this.getProvider());
            KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(pin.toCharArray());
            // cc.load(null,  pp.getPassword());
            cc.load(null, null);
            Enumeration aliases = cc.aliases();
            while (aliases.hasMoreElements() && !(certFlag)) {
                Object alias = aliases.nextElement();
                try {
                    this.setCertificado((X509Certificate) cc.getCertificate(alias.toString()));
                    certFlag = true;
                    
                    // Log
                    System.out.println("Se ha obtenido el certificado correctamente.");
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (KeyStoreException ex) {
            Logger.getLogger(SecImp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Ha fallado la conexion con el token. Compruebe que el token esta correctamente conectado.");
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(SecImp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Ha fallado la conexion.");
        } catch (CertificateException ex) {
            Logger.getLogger(SecImp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Ha fallado la conexion al certificado.");
        }
    }

    public String getName() { 
        String nombre = this.getCertificado().getSubjectDN().getName();
        String[] nombreCompleto = nombre.split(",");
        nombre = nombreCompleto[0].substring(3, nombreCompleto[0].length());
        return nombre;
    }  
    
    public void conCIconPass(String pasword) {
        boolean certFlag = false;
        try {            
            KeyStore cc = KeyStore.getInstance("PKCS11", this.getProvider());
            KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(pasword.toCharArray());
            cc.load(null,  pp.getPassword());
            Enumeration aliases = cc.aliases();
            while (aliases.hasMoreElements() && !(certFlag)) {
                Object alias = aliases.nextElement();
                try {
                    this.setCertificado((X509Certificate) cc.getCertificate(alias.toString()));
                    certFlag = true;
                    
                    // Log
                    System.out.println("Se ha obtenido el certificado correctamente.");
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (KeyStoreException ex) {
            Logger.getLogger(SecImp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Ha fallado la conexion con el token. Compruebe que el token esta correctamente conectado.");
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(SecImp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Ha fallado la conexion. Compruebe el pin.");
        } catch (CertificateException ex) {
            Logger.getLogger(SecImp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Ha fallado la conexion al certificado.");
        }
    }
    
    public boolean estaEnchufada() {    
        KeyStore cc = null;
        try {
            cc = KeyStore.getInstance("PKCS11", this.getProvider());
            return true;
        } catch (KeyStoreException ex) {
            return false;
        }
    }
    
    public boolean verificarPin(String pasword) {
        try {
            KeyStore cc = KeyStore.getInstance("PKCS11", this.getProvider());
            KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(pasword.toCharArray());
            cc.load(null,  pp.getPassword());
            return true;
        } catch (KeyStoreException ex) {
            Logger.getLogger(SecImp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Se ha desenchufado la cedula");
            return false;
        } catch (IOException ex) {
            Logger.getLogger(SecImp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo el ingreso de pin.");
            return false;
        } catch (NoSuchAlgorithmException | CertificateException ex) {
            Logger.getLogger(SecImp.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Exploto el certificado.");
            return false;
        }
    }
    
    /**
     * @return the configName
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * @param configName the configName to set
     */
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     * @return the provider
     */
    public Provider getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    /**
     * @return the certificado
     */
    public X509Certificate getCertificado() {
        return certificado;
    }

    /**
     * @param certificado the certificado to set
     */
    public void setCertificado(X509Certificate certificado) {
        this.certificado = certificado;
    }
}

