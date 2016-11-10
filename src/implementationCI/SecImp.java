/*
 * Universidad Catolica - Seguridad - Obligatorio.
 */
package implementationCI;

import java.io.FileInputStream; 
import java.io.IOException;
import java.io.InputStream; 
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Properties; 

/**
 * Esta clase es la encargada de interactuar con la Cedula de Identidad.
 * @author Masoller, Artegoytia, Galleto, Olivera.
 */
public class SecImp implements ISecImp {

    private String configName;
    private Provider provider;
    private X509Certificate certificado;

    public void SecImp(String configName) { this.configName = configName; }

    public void SecImp() {
        String configName = ".\\conf\\pkcs11.cfg";
        Provider provider = new sun.security.pkcs11.SunPKCS11(configName);
        Security.addProvider(provider);
    }

    public void remProvider() { Security.removeProvider(provider.getName()); }

    public void conectCI() {
        String pin = "";
        KeyStore cc = KeyStore.getInstance("PKCS11", provider);
        KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(pin.toCharArray());
        cc.load(null,  pp.getPassword());
        Enumeration aliases = cc.aliases();
        while (aliases.hasMoreElements()) {
            Object alias = aliases.nextElement();
            try {
                certificado = (X509Certificate) cc.getCertificate(alias.toString());
                System.out.println("I am: " + cert0.getSubjectDN().getName());
            } catch (Exception e) {
                continue;
            }
        }
    }

    public void 

    public static void main(String[] args) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        
        //String pin = "Poner pin aca"; 
        //KeyStore ks = KeyStore.getInstance("PKCS11");
        //ks.load(null, pin.toCharArray());
        //ks.load(null, null);
                
        
    }
    
}
